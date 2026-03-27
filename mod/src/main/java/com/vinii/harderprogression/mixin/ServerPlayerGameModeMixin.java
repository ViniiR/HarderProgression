package com.vinii.harderprogression.mixin;

import com.vinii.harderprogression.item.ModItems;
import com.vinii.harderprogression.tags.ModBlockTags;
import com.vinii.harderprogression.tags.ModItemTags;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;

import java.util.Objects;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;
    @Shadow
    private GameType gameModeForPlayer = GameType.DEFAULT_MODE;
    @Shadow
    private int gameTicks;
    @Shadow
    private int destroyProgressStart;
    @Shadow
    private boolean isDestroyingBlock;
    @Shadow
    private BlockPos destroyPos = BlockPos.ZERO;
    @Shadow
    private boolean hasDelayedDestroy;
    @Shadow
    private BlockPos delayedDestroyPos = BlockPos.ZERO;
    @Shadow
    private int delayedTickStart;
    @Shadow
    private int lastSentState = -1;


    @Shadow
    private void debugLogging(BlockPos blockPos, boolean bl, int i, String string) {
    }

    @Shadow
    public void destroyAndAck(BlockPos blockPos, int i, String string) {
    }

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"), cancellable = true)
    void injected(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, int j, CallbackInfo ci) {
        ci.cancel();

        if (!this.player.isWithinBlockInteractionRange(blockPos, 1.0)) {
            this.debugLogging(blockPos, false, j, "too far");
        } else if (blockPos.getY() > i) {
            this.player.connection.send(new ClientboundBlockUpdatePacket(blockPos, this.level.getBlockState(blockPos)));
            this.debugLogging(blockPos, false, j, "too high");
        } else {
            if (action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
                // Unsure
                if (!this.level.mayInteract(this.player, blockPos)) {
                    this.player.connection.send(new ClientboundBlockUpdatePacket(blockPos, this.level.getBlockState(blockPos)));
                    this.debugLogging(blockPos, false, j, "may not interact");
                    return;
                }

                // Creative mode
                if (this.player.getAbilities().instabuild) {
                    this.destroyAndAck(blockPos, j, "creative destroy");
                    return;
                }

                // Adventure mode
                if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
                    this.player.connection.send(new ClientboundBlockUpdatePacket(blockPos, this.level.getBlockState(blockPos)));
                    this.debugLogging(blockPos, false, j, "block action restricted");
                    return;
                }

                this.destroyProgressStart = this.gameTicks;
                float f = 1.0F;
                BlockState blockState = this.level.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    EnchantmentHelper.onHitBlock(
                        this.level,
                        this.player.getMainHandItem(),
                        this.player,
                        this.player,
                        EquipmentSlot.MAINHAND,
                        Vec3.atCenterOf(blockPos),
                        blockState,
                        item -> this.player.onEquippedItemBroken(item, EquipmentSlot.MAINHAND)
                    );
                    blockState.attack(this.level, blockPos, this.player);
                    f = blockState.getDestroyProgress(this.player, this.player.level(), blockPos);
                }

                // Grass blocks && instamines
                if (!blockState.isAir() && f >= 1.0F) {
                    this.destroyAndAck(blockPos, j, "insta mine");
                } else {
                    // Unsure
                    if (this.isDestroyingBlock) {
                        this.player.connection.send(new ClientboundBlockUpdatePacket(this.destroyPos, this.level.getBlockState(this.destroyPos)));
                        this.debugLogging(blockPos, false, j, "abort destroying since another started (client insta mine, server disagreed)");
                    }

                    this.isDestroyingBlock = true;
                    this.destroyPos = blockPos.immutable();
                    int k = (int) (f * 10.0F);
                    this.level.destroyBlockProgress(this.player.getId(), blockPos, k);
                    this.debugLogging(blockPos, true, j, "actual start of destroying");
                    this.lastSentState = k;
                }
            } else if (action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
                if (blockPos.equals(this.destroyPos)) {
                    int l = this.gameTicks - this.destroyProgressStart;
                    BlockState blockStatex = this.level.getBlockState(blockPos);
                    if (!blockStatex.isAir()) {
                        float g = blockStatex.getDestroyProgress(this.player, this.player.level(), blockPos) * (l + 1);
                        if (g >= 0.7F) {
                            // Custom logic
                            Pair<Block, Item> logBlockItems = getBarkContent(blockStatex);
                            Pair<Block, Item> rockBlockItems = getRockContent(blockStatex);
                            boolean isHoldingAxe = isHoldingAxe();

                            if (!isHoldingAxe && logBlockItems != null) {
                                Direction.Axis rotation = blockStatex.getValue(BlockStateProperties.AXIS);
                                level.setBlock(blockPos, logBlockItems.getA().defaultBlockState().setValue(BlockStateProperties.AXIS, rotation), 2);
                                spawnBlockDropAt(blockPos, logBlockItems.getB());
                                return;
                            } else if (isHoldingChisel() && rockBlockItems != null) {
                                level.setBlock(blockPos, rockBlockItems.getA().defaultBlockState(), 2);
                                spawnBlockDropAt(blockPos, rockBlockItems.getB());
                                return;
                            }
                            //

                            this.isDestroyingBlock = false;
                            this.level.destroyBlockProgress(this.player.getId(), blockPos, -1);
                            this.destroyAndAck(blockPos, j, "destroyed");
                            return;
                        }

                        if (!this.hasDelayedDestroy) {
                            this.isDestroyingBlock = false;
                            this.hasDelayedDestroy = true;
                            this.delayedDestroyPos = blockPos;
                            this.delayedTickStart = this.destroyProgressStart;
                        }
                    }
                }

                this.debugLogging(blockPos, true, j, "stopped destroying");
            } else if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
                this.isDestroyingBlock = false;
                if (!Objects.equals(this.destroyPos, blockPos)) {
                    LOGGER.warn("Mismatch in destroy block pos: {} {}", this.destroyPos, blockPos);
                    this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                    this.debugLogging(blockPos, true, j, "aborted mismatched destroying");
                }

                this.level.destroyBlockProgress(this.player.getId(), blockPos, -1);
                this.debugLogging(blockPos, true, j, "aborted destroying");
            }
        }
    }

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    void injectedPreventBlockDrops(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (!this.player.getMainHandItem().canDestroyBlock(blockState, this.level, blockPos, this.player)) {
            cir.setReturnValue(false);
            return;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            cir.setReturnValue(false);
            return;
        }
        if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
            cir.setReturnValue(false);
            return;
        }
        BlockState blockState2 = block.playerWillDestroy(this.level, blockPos, blockState, this.player);
        boolean bl = this.level.removeBlock(blockPos, false);
        if (SharedConstants.DEBUG_BLOCK_BREAK) {
            LOGGER.info("server broke {} {} -> {}", blockPos, blockState2, this.level.getBlockState(blockPos));
        }

        if (bl) {
            block.destroy(this.level, blockPos, blockState2);
        }

        // Custom logic
        if (!isHoldingAxe() && blockState.is(ModBlockTags.STRIPPED_LOGS)){
            cir.setReturnValue(true);
            return;
        }

        if (this.player.preventsBlockDrops()) {
            cir.setReturnValue(true);
            return;
        }
        ItemStack itemStack = this.player.getMainHandItem();
        ItemStack itemStack2 = itemStack.copy();
        boolean bl2 = this.player.hasCorrectToolForDrops(blockState2);
        itemStack.mineBlock(this.level, blockState2, blockPos, this.player);
        if (bl && bl2) {
            block.playerDestroy(this.level, this.player, blockPos, blockState2, blockEntity, itemStack2);
        }

        cir.setReturnValue(true);
    }

    @Unique
    @Nullable
    private Pair<Block, Item> getBarkContent(BlockState state) {
        if (state.is(Blocks.OAK_LOG)) {
            return new Pair<>(Blocks.STRIPPED_OAK_LOG, ModItems.OAK_BARK);
        }
        if (state.is(Blocks.OAK_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_OAK_WOOD, ModItems.OAK_BARK);
        }
        if (state.is(Blocks.SPRUCE_LOG)) {
            return new Pair<>(Blocks.STRIPPED_SPRUCE_LOG, ModItems.SPRUCE_BARK);
        }
        if (state.is(Blocks.SPRUCE_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_SPRUCE_WOOD, ModItems.SPRUCE_BARK);
        }
        if (state.is(Blocks.DARK_OAK_LOG)) {
            return new Pair<>(Blocks.STRIPPED_DARK_OAK_LOG, ModItems.DARK_OAK_BARK);
        }
        if (state.is(Blocks.DARK_OAK_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_DARK_OAK_WOOD, ModItems.DARK_OAK_BARK);
        }
        if (state.is(Blocks.BIRCH_LOG)) {
            return new Pair<>(Blocks.STRIPPED_BIRCH_LOG, ModItems.BIRCH_BARK);
        }
        if (state.is(Blocks.BIRCH_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_BIRCH_WOOD, ModItems.BIRCH_BARK);
        }
        if (state.is(Blocks.JUNGLE_LOG)) {
            return new Pair<>(Blocks.STRIPPED_JUNGLE_LOG, ModItems.JUNGLE_BARK);
        }
        if (state.is(Blocks.JUNGLE_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_JUNGLE_WOOD, ModItems.JUNGLE_BARK);
        }
        if (state.is(Blocks.ACACIA_LOG)) {
            return new Pair<>(Blocks.STRIPPED_ACACIA_LOG, ModItems.ACACIA_BARK);
        }
        if (state.is(Blocks.ACACIA_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_ACACIA_WOOD, ModItems.ACACIA_BARK);
        }
        if (state.is(Blocks.CHERRY_LOG)) {
            return new Pair<>(Blocks.STRIPPED_CHERRY_LOG, ModItems.CHERRY_BARK);
        }
        if (state.is(Blocks.CHERRY_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_CHERRY_WOOD, ModItems.CHERRY_BARK);
        }
        if (state.is(Blocks.MANGROVE_LOG)) {
            return new Pair<>(Blocks.STRIPPED_MANGROVE_LOG, ModItems.MANGROVE_BARK);
        }
        if (state.is(Blocks.MANGROVE_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_MANGROVE_WOOD, ModItems.MANGROVE_BARK);
        }
        if (state.is(Blocks.PALE_OAK_LOG)) {
            return new Pair<>(Blocks.STRIPPED_PALE_OAK_LOG, ModItems.PALE_OAK_BARK);
        }
        if (state.is(Blocks.PALE_OAK_WOOD)) {
            return new Pair<>(Blocks.STRIPPED_PALE_OAK_WOOD, ModItems.PALE_OAK_BARK);
        }
        if (state.is(Blocks.CRIMSON_STEM)) {
            return new Pair<>(Blocks.STRIPPED_CRIMSON_STEM, ModItems.CRIMSON_BARK);
        }
        if (state.is(Blocks.CRIMSON_HYPHAE)) {
            return new Pair<>(Blocks.STRIPPED_CRIMSON_HYPHAE, ModItems.CRIMSON_BARK);
        }
        if (state.is(Blocks.WARPED_STEM)) {
            return new Pair<>(Blocks.STRIPPED_WARPED_STEM, ModItems.WARPED_BARK);
        }
        if (state.is(Blocks.WARPED_HYPHAE)) {
            return new Pair<>(Blocks.STRIPPED_WARPED_HYPHAE, ModItems.WARPED_BARK);
        }
        return null;
    }

    @Unique
    @Nullable
    private Pair<Block, Item> getRockContent(BlockState state) {
        if (state.is(Blocks.STONE)) {
            return new Pair<>(Blocks.COBBLESTONE, ModItems.ROCK);
        }
//        if (state.is(Blocks.ANDESITE)) {
//            return new Pair<>(Blocks.COBBLESTONE, ModItems.ROCK);
//        }
//        if (state.is(Blocks.DIORITE)) {
//            return new Pair<>(Blocks.COBBLESTONE, ModItems.ROCK);
//        }
//        if (state.is(Blocks.GRANITE)) {
//            return new Pair<>(Blocks.COBBLESTONE, ModItems.ROCK);
//        }
        return null;
    }

    @Unique
    private boolean isHoldingAxe() {
        return this.player.getMainHandItem().is(ItemTags.AXES);
    }

    @Unique
    private boolean isHoldingChisel() {
        return this.player.getMainHandItem().is(ModItemTags.CHISELS);
    }

    // Hey this works decently, I'm calling it a day!
    @Unique
    private void spawnBlockDropAt(BlockPos blockPos, Item item) {
        // Yes these fucking angle calculations were surely generated by AI, but the dumbass got it backwards lmao so i added .reverse()
        Vec3 look = player.getLookAngle().reverse();

        ItemEntity drop = new ItemEntity(
            level,
            blockPos.getX() + 0.5 * look.x * 0.65,
            blockPos.getY() + 0.5 * look.y * 0.65,
            blockPos.getZ() + 0.5 * look.z * 0.65,
            new ItemStack(item)
        );
        drop.setDeltaMovement(
            look.x * 0.1 + (level.random.nextDouble() - 0.5) * 0.02,
            look.y * 0.1 + 0.05,
            look.z * 0.1 + (level.random.nextDouble() - 0.5) * 0.02
        );

        level.addFreshEntity(drop);
    }
}

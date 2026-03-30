package com.vinii.harderprogression.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.vinii.harderprogression.item.ModItems;
import com.vinii.harderprogression.tags.ModBlockTags;
import com.vinii.harderprogression.tags.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(
        method = "handleBlockBreakAction",
        at = @At(
            value = "JUMP",
            opcode = Opcodes.IFLT,
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    void handleChiselableBlocks(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, int j, CallbackInfo ci) {
        BlockState blockStatex = level.getBlockState(blockPos);

        Pair<Block, Item> logBlockItems = getBarkContent(blockStatex);
        Pair<Block, Item> rockBlockItems = getRockContent(blockStatex);

        if (!isHoldingAxe() && logBlockItems != null) {
            Direction.Axis rotation = blockStatex.getValue(BlockStateProperties.AXIS);
            level.setBlock(blockPos, logBlockItems.getA().defaultBlockState().setValue(BlockStateProperties.AXIS, rotation), 2);
            spawnBlockDropAt(blockPos, logBlockItems.getB());
            ci.cancel();
        } else if (isHoldingChisel() && rockBlockItems != null) {
            level.setBlock(blockPos, rockBlockItems.getA().defaultBlockState(), 2);
            spawnBlockDropAt(blockPos, rockBlockItems.getB());
            ci.cancel();
        }
    }

    @Inject(
        method = "destroyBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    void preventStrippedLogDrops(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, @Local BlockState blockState2) {
        // blockState2 is the same variable as blockState, But it is returned by Block.playerWillDestroy
        // which just executed some extra functions like spawn particles and alert piglins
        if (!isHoldingAxe() && blockState2.is(ModBlockTags.STRIPPED_LOGS)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
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

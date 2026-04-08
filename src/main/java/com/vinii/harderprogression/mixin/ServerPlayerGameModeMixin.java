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

import java.util.Map;
import java.util.Optional;

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

        Optional<Pair<Block, Item>> logBlockItems = getChiselableBlockContent(blockStatex, barkMap);
        Optional<Pair<Block, Item>> rockBlockItems = getChiselableBlockContent(blockStatex, rockMap);

        if (!isHoldingAxe() && logBlockItems.isPresent()) {
            Pair<Block, Item> values = logBlockItems.get();

            Direction.Axis rotation = blockStatex.getValue(BlockStateProperties.AXIS);
            level.setBlock(blockPos, values.getA().defaultBlockState().setValue(BlockStateProperties.AXIS, rotation), 2);
            spawnBlockDropAt(blockPos, values.getB());
            ci.cancel();
        } else if (isHoldingChisel() && rockBlockItems.isPresent()) {
            Pair<Block, Item> values = rockBlockItems.get();

            level.setBlock(blockPos, values.getA().defaultBlockState(), 2);
            spawnBlockDropAt(blockPos, values.getB());
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
    private static final Map<Block, Pair<Block, Item>> barkMap = Map.ofEntries(
        Map.entry(Blocks.OAK_LOG, new Pair<>(Blocks.STRIPPED_OAK_LOG, ModItems.OAK_BARK)),
        Map.entry(Blocks.OAK_WOOD, new Pair<>(Blocks.STRIPPED_OAK_WOOD, ModItems.OAK_BARK)),
        Map.entry(Blocks.SPRUCE_LOG, new Pair<>(Blocks.STRIPPED_SPRUCE_LOG, ModItems.SPRUCE_BARK)),
        Map.entry(Blocks.SPRUCE_WOOD, new Pair<>(Blocks.STRIPPED_SPRUCE_WOOD, ModItems.SPRUCE_BARK)),
        Map.entry(Blocks.DARK_OAK_LOG, new Pair<>(Blocks.STRIPPED_DARK_OAK_LOG, ModItems.DARK_OAK_BARK)),
        Map.entry(Blocks.DARK_OAK_WOOD, new Pair<>(Blocks.STRIPPED_DARK_OAK_WOOD, ModItems.DARK_OAK_BARK)),
        Map.entry(Blocks.BIRCH_LOG, new Pair<>(Blocks.STRIPPED_BIRCH_LOG, ModItems.BIRCH_BARK)),
        Map.entry(Blocks.BIRCH_WOOD, new Pair<>(Blocks.STRIPPED_BIRCH_WOOD, ModItems.BIRCH_BARK)),
        Map.entry(Blocks.JUNGLE_LOG, new Pair<>(Blocks.STRIPPED_JUNGLE_LOG, ModItems.JUNGLE_BARK)),
        Map.entry(Blocks.JUNGLE_WOOD, new Pair<>(Blocks.STRIPPED_JUNGLE_WOOD, ModItems.JUNGLE_BARK)),
        Map.entry(Blocks.ACACIA_LOG, new Pair<>(Blocks.STRIPPED_ACACIA_LOG, ModItems.ACACIA_BARK)),
        Map.entry(Blocks.ACACIA_WOOD, new Pair<>(Blocks.STRIPPED_ACACIA_WOOD, ModItems.ACACIA_BARK)),
        Map.entry(Blocks.CHERRY_LOG, new Pair<>(Blocks.STRIPPED_CHERRY_LOG, ModItems.CHERRY_BARK)),
        Map.entry(Blocks.CHERRY_WOOD, new Pair<>(Blocks.STRIPPED_CHERRY_WOOD, ModItems.CHERRY_BARK)),
        Map.entry(Blocks.MANGROVE_LOG, new Pair<>(Blocks.STRIPPED_MANGROVE_LOG, ModItems.MANGROVE_BARK)),
        Map.entry(Blocks.MANGROVE_WOOD, new Pair<>(Blocks.STRIPPED_MANGROVE_WOOD, ModItems.MANGROVE_BARK)),
        Map.entry(Blocks.PALE_OAK_LOG, new Pair<>(Blocks.STRIPPED_PALE_OAK_LOG, ModItems.PALE_OAK_BARK)),
        Map.entry(Blocks.PALE_OAK_WOOD, new Pair<>(Blocks.STRIPPED_PALE_OAK_WOOD, ModItems.PALE_OAK_BARK)),
        Map.entry(Blocks.CRIMSON_STEM, new Pair<>(Blocks.STRIPPED_CRIMSON_STEM, ModItems.CRIMSON_BARK)),
        Map.entry(Blocks.CRIMSON_HYPHAE, new Pair<>(Blocks.STRIPPED_CRIMSON_HYPHAE, ModItems.CRIMSON_BARK)),
        Map.entry(Blocks.WARPED_STEM, new Pair<>(Blocks.STRIPPED_WARPED_STEM, ModItems.WARPED_BARK)),
        Map.entry(Blocks.WARPED_HYPHAE, new Pair<>(Blocks.STRIPPED_WARPED_HYPHAE, ModItems.WARPED_BARK))
    );

    @Unique
    private static final Map<Block, Pair<Block, Item>> rockMap = Map.ofEntries(
        Map.entry(Blocks.STONE, new Pair<>(Blocks.COBBLESTONE, ModItems.ROCK))
    );

    @Unique
    private Optional<Pair<Block, Item>> getChiselableBlockContent(BlockState state, Map<Block, Pair<Block, Item>> map) {
        Pair<Block, Item> res = map.get(state.getBlock());
        return res == null ? Optional.empty() : Optional.of(res);
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

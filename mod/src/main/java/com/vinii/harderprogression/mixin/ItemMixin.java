package com.vinii.harderprogression.mixin;

import com.vinii.harderprogression.tags.ModBlockTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(
        method = "getDestroySpeed",
        at = @At("HEAD"),
        cancellable = true
    )
    void increaseStrippedLogDestroySpeed(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Float> cir) {
        if (!itemStack.is(ItemTags.AXES) && blockState.is(ModBlockTags.STRIPPED_LOGS)) {
            cir.setReturnValue(0.5f);
            cir.cancel();
        }
    }
}

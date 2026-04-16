package com.vinii.harderprogression.mixin;

import com.vinii.harderprogression.mixin.accessor.ItemCombinerMenuAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Final
    @Shadow
    private DataSlot cost;

    @Inject(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V",
            shift = At.Shift.AFTER,
            ordinal = 5
        )
    )
    void limitAnvilRepairCost(CallbackInfo ci) {
        Container inputSlots = ((ItemCombinerMenuAccessor) this).getInputSlots();

        ItemStack itemStack = inputSlots.getItem(0);
        ItemStack itemStack3 = inputSlots.getItem(1);

        if (cost.get() > 10 && itemStack.isValidRepairItem(itemStack3) && !itemStack3.has(DataComponents.STORED_ENCHANTMENTS)) {
            cost.set(10);
        }
    }
}

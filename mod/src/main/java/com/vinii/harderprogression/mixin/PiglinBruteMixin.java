package com.vinii.harderprogression.mixin;

import com.vinii.harderprogression.mixin.accessors.MobAccessor;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinBrute.class)
public abstract class PiglinBruteMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injected(CallbackInfo info) {
        ((MobAccessor) this).setXpReward(50);
    }
}
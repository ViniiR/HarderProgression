package com.vinii.harderprogression.mixin;

import com.vinii.harderprogression.mixin.accessors.MobAccessor;
import net.minecraft.world.entity.monster.ElderGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElderGuardian.class)
public class ElderGuardianMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injected(CallbackInfo info){
        ((MobAccessor)this).setXpReward(100);
    }
}

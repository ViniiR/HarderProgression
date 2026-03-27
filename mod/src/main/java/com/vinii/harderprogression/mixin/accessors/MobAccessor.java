package com.vinii.harderprogression.mixin.accessors;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("xpReward")
    void setXpReward(int val);

    @Accessor("xpReward")
    int getXpReward();
}

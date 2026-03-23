package com.vinii.harderprogression.item.items;

import com.vinii.harderprogression.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class KnifeItem extends Item {
    public KnifeItem(ToolMaterial toolMaterial, Item.Properties properties) {
        super(properties);
    }


    public static class Properties extends Item.Properties {

        /// @param f: Damage modifier (multiplier)
        /// @param g: Attack speed (add to base 4 attack speed)
        public Item.Properties knife(ToolMaterial toolMaterial, float f, float g) {
            /// h: knockback
            return this.tool(toolMaterial, ModBlockTags.MINEABLE_WITH_KNIFE, f, g, 0.0f);
        }
    }

    @Override
    public boolean mineBlock(@NonNull ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        Tool tool = itemStack.get(DataComponents.TOOL);
        if (tool == null) {
            return false;
        }

        // Lose durability with any blocks
        // super method does not lose durability on plants/instabreak blocks
        if (!level.isClientSide() && tool.damagePerBlock() > 0) {
            itemStack.hurtAndBreak(tool.damagePerBlock(), livingEntity, EquipmentSlot.MAINHAND);
        }

        return true;
    }
}
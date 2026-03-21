package com.vinii.harderprogression.item;

import com.vinii.harderprogression.HarderProgression;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

import java.util.function.Function;

public class ModItems {
    // Tools
    // TODO:
    public static final Item STONE_KNIFE = registerItem(
        "stone_knife",
        Item::new,
        new Item.Properties()
            .rarity(Rarity.COMMON)
//            .sword(ToolMaterial.STONE, 1.6f, 2.0f)
//            .tool(ToolMaterial.STONE, TagKey.create())
//            .durability(100)
    );

    // Plant
    public static final Item PLANT_FIBER = registerItem(
        "plant_fiber",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item PLANT_STRING = registerItem(
        "plant_string",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );

    // Upgrades
    public static final Item IRON_UPGRADE = registerItem(
        "iron_upgrade_smithing_template",
        Item::new,
        new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item DIAMOND_UPGRADE = registerItem(
        "diamond_upgrade_smithing_template",
        Item::new,
        new Item.Properties().rarity(Rarity.UNCOMMON)
    );

    private static <T extends Item> T registerItem(
        String name,
        Function<Item.Properties, T> factory,
        Item.Properties properties
    ) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HarderProgression.MOD_ID, name));

        T item = factory.apply(properties.setId(key));

        Registry.register(BuiltInRegistries.ITEM, key, item);

        return item;
    }


    public static void registerItems() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, HP_CREATIVE_TAB_KEY, HP_CREATIVE_TAB);

        HarderProgression.LOGGER.info("Registered items for " + HarderProgression.MOD_ID);
    }

    public static final ResourceKey<CreativeModeTab> HP_CREATIVE_TAB_KEY =
        ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            Identifier.fromNamespaceAndPath(HarderProgression.MOD_ID, "creative_tab")
        );
    public static final CreativeModeTab HP_CREATIVE_TAB = FabricItemGroup.builder()
        .icon(() -> new ItemStack(Items.COMMAND_BLOCK))
        .title(Component.translatable("itemgroup.harderprogression"))
        .displayItems(((itemDisplayParameters, output) -> {
            output.accept(ModItems.STONE_KNIFE);
            output.accept(ModItems.PLANT_FIBER);
            output.accept(ModItems.PLANT_STRING);
            output.accept(ModItems.IRON_UPGRADE);
            output.accept(ModItems.DIAMOND_UPGRADE);
        }))
        .build();
}

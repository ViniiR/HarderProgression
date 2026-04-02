package com.vinii.harderprogression.item;

import com.vinii.harderprogression.HarderProgression;
import com.vinii.harderprogression.item.items.KnifeItem;
import com.vinii.harderprogression.tags.ModItemTags;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.FuelValues;

import java.util.List;
import java.util.function.Function;

public class ModItems {
    private static final Identifier EMPTY_SLOT_HELMET = Identifier.withDefaultNamespace("container/slot/helmet");
    private static final Identifier EMPTY_SLOT_CHESTPLATE = Identifier.withDefaultNamespace("container/slot/chestplate");
    private static final Identifier EMPTY_SLOT_LEGGINGS = Identifier.withDefaultNamespace("container/slot/leggings");
    private static final Identifier EMPTY_SLOT_BOOTS = Identifier.withDefaultNamespace("container/slot/boots");
    private static final Identifier EMPTY_SLOT_HOE = Identifier.withDefaultNamespace("container/slot/hoe");
    private static final Identifier EMPTY_SLOT_AXE = Identifier.withDefaultNamespace("container/slot/axe");
    private static final Identifier EMPTY_SLOT_SWORD = Identifier.withDefaultNamespace("container/slot/sword");
    private static final Identifier EMPTY_SLOT_SHOVEL = Identifier.withDefaultNamespace("container/slot/shovel");
    private static final Identifier EMPTY_SLOT_SPEAR = Identifier.withDefaultNamespace("container/slot/spear");
    private static final Identifier EMPTY_SLOT_PICKAXE = Identifier.withDefaultNamespace("container/slot/pickaxe");
    private static final Identifier EMPTY_SLOT_INGOT = Identifier.withDefaultNamespace("container/slot/ingot");
    private static final Identifier EMPTY_SLOT_DIAMOND = Identifier.withDefaultNamespace("container/slot/diamond");

    private static List<Identifier> createUpgradeIconList() {
        return List.of(
            EMPTY_SLOT_HELMET,
            EMPTY_SLOT_SWORD,
            EMPTY_SLOT_CHESTPLATE,
            EMPTY_SLOT_PICKAXE,
            EMPTY_SLOT_LEGGINGS,
            EMPTY_SLOT_AXE,
            EMPTY_SLOT_BOOTS,
            EMPTY_SLOT_HOE,
            EMPTY_SLOT_SHOVEL,
            EMPTY_SLOT_SPEAR
        );
    }

    // Tools
    public static final Item STONE_KNIFE = registerItem(
        "stone_knife",
        KnifeItem::new,
        new KnifeItem.Properties()
            .knife(ToolMaterial.STONE, 1.0f, -2.4f)
            .rarity(Rarity.COMMON)
    );
    public static final Item SHARP_STICK = registerItem(
        "sharp_stick",
        Item::new,
        new Item.Properties()
            .rarity(Rarity.COMMON)
            .stacksTo(1)
    );

    // Materials
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

    public static final Item ROCK = registerItem(
        "rock",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );

    // Wood Bark
    public static final Item OAK_BARK = registerItem(
        "oak_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item DARK_OAK_BARK = registerItem(
        "dark_oak_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item PALE_OAK_BARK = registerItem(
        "pale_oak_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item BIRCH_BARK = registerItem(
        "birch_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item SPRUCE_BARK = registerItem(
        "spruce_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item ACACIA_BARK = registerItem(
        "acacia_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item JUNGLE_BARK = registerItem(
        "jungle_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item MANGROVE_BARK = registerItem(
        "mangrove_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item CHERRY_BARK = registerItem(
        "cherry_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item CRIMSON_BARK = registerItem(
        "crimson_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );
    public static final Item WARPED_BARK = registerItem(
        "warped_bark",
        Item::new,
        new Item.Properties().rarity(Rarity.COMMON)
    );

    public static void registerFuels() {
        FuelRegistryEvents.BUILD.register(((builder, context) -> {
            builder.add(ModItemTags.OVERWORLD_BARKS, 200);
        }));
    }

    // Upgrades
    public static final Item IRON_UPGRADE = registerItem(
        "iron_upgrade_smithing_template",
        properties -> {
            return new SmithingTemplateItem(
                Component.translatable("item.harderprogression.iron_upgrade_smithing_template.applies_to"),
                Component.translatable("item.harderprogression.iron_upgrade_smithing_template.ingredients"),
                Component.translatable("item.harderprogression.iron_upgrade_smithing_template.base_slot_description"),
                Component.translatable("item.harderprogression.iron_upgrade_smithing_template.additions_slot_description"),
                createUpgradeIconList(),
                List.of(
                    EMPTY_SLOT_INGOT
                ),
                properties
            );
        },

        new Item.Properties()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item DIAMOND_UPGRADE = registerItem(
        "diamond_upgrade_smithing_template",
        properties -> {
            return new SmithingTemplateItem(
                Component.translatable("item.harderprogression.diamond_upgrade_smithing_template.applies_to"),
                Component.translatable("item.harderprogression.diamond_upgrade_smithing_template.ingredients"),
                Component.translatable("item.harderprogression.diamond_upgrade_smithing_template.base_slot_description"),
                Component.translatable("item.harderprogression.diamond_upgrade_smithing_template.additions_slot_description"),
                createUpgradeIconList(),
                List.of(
                    EMPTY_SLOT_DIAMOND
                ),
                properties
            );
        },

        new Item.Properties()
            .rarity(Rarity.UNCOMMON)
    );

    private static <T extends Item, P extends Item.Properties> T registerItem(
        String name,
        Function<Item.Properties, T> factory,
        P properties
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
        .icon(() -> new ItemStack(ModItems.ROCK))
        .title(Component.translatable("itemgroup.harderprogression"))
        .displayItems(((itemDisplayParameters, output) -> {
            output.accept(ModItems.STONE_KNIFE);
            output.accept(ModItems.SHARP_STICK);

            output.accept(ModItems.ROCK);

            output.accept(ModItems.OAK_BARK);
            output.accept(ModItems.DARK_OAK_BARK);
            output.accept(ModItems.PALE_OAK_BARK);
            output.accept(ModItems.ACACIA_BARK);
            output.accept(ModItems.BIRCH_BARK);
            output.accept(ModItems.JUNGLE_BARK);
            output.accept(ModItems.CHERRY_BARK);
            output.accept(ModItems.MANGROVE_BARK);
            output.accept(ModItems.SPRUCE_BARK);
            output.accept(ModItems.CRIMSON_BARK);
            output.accept(ModItems.WARPED_BARK);

            output.accept(ModItems.PLANT_FIBER);
            output.accept(ModItems.PLANT_STRING);

            output.accept(ModItems.IRON_UPGRADE);
            output.accept(ModItems.DIAMOND_UPGRADE);
        }))
        .build();
}

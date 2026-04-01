package com.vinii.harderprogression.tags;

import com.vinii.harderprogression.HarderProgression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> CHISELS = create("chisels");
    public static final TagKey<Item> OVERWORLD_BARKS = create("overworld_barks");

    private static TagKey<Item> create(String string) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HarderProgression.MOD_ID, string));
    }
}

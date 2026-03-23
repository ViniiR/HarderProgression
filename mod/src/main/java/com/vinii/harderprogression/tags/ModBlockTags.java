package com.vinii.harderprogression.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> MINEABLE_WITH_KNIFE = create("mineable/knife");
    public static final TagKey<Block> MINEABLE_WITH_CHISEL = create("mineable/chisel");

    private static TagKey<Block> create(String string) {
        return TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(string));
    }
}

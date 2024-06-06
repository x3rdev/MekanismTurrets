package com.github.x3r.mekanism_turrets;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MekanismTurretsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedEntities;


    static  {
        BUILDER.push("Mekanism Turrets Config");

        blacklistedEntities = BUILDER.comment("Entities which will never be targeted by turrets").defineListAllowEmpty("blacklistedEntities", List.of("minecraft:wolf", "minecraft:ender_dragon"), MekanismTurretsConfig::isEntityId);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static boolean isEntityId(Object o) {
        return o instanceof String string && ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(string));
    }
}

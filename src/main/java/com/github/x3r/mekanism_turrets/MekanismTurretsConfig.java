package com.github.x3r.mekanism_turrets;

import net.minecraftforge.common.ForgeConfigSpec;

public class MekanismTurretsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    static  {
        BUILDER.push("Mekanism Turrets Config");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

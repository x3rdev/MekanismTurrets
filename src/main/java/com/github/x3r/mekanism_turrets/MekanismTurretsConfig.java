package com.github.x3r.mekanism_turrets;

import net.minecraftforge.common.ForgeConfigSpec;

public class MekanismTurretsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue turretEnergyUsage;
    public static final ForgeConfigSpec.IntValue turretEnergyStorage;

    static  {
        BUILDER.push("Mekanism Turrets Config");

        turretEnergyUsage = BUILDER
                .comment("Energy usage per tick for the turret")
                .translation("mekanism_turrets.config.turretEnergyUsage")
                .worldRestart()
                .defineInRange("turretEnergyUsage", 1, 0, 10000);
        turretEnergyStorage = BUILDER
                .comment("Energy storage capacity for the turret")
                .translation("mekanism_turrets.config.turretEnergyStorage")
                .worldRestart()
                .defineInRange("turretEnergyStorage", 10000, 0, 1000000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

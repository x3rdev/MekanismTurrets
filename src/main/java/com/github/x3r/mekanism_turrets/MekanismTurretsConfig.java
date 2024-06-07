package com.github.x3r.mekanism_turrets;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MekanismTurretsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<?>> blacklistedEntities;

    public static ForgeConfigSpec.IntValue basicLaserTurretCooldown;
    public static ForgeConfigSpec.IntValue basicLaserTurretDamage;
    public static ForgeConfigSpec.IntValue basicLaserTurretEnergyCapacity;
    public static ForgeConfigSpec.IntValue advancedLaserTurretCooldown;
    public static ForgeConfigSpec.IntValue advancedLaserTurretDamage;
    public static ForgeConfigSpec.IntValue advancedLaserTurretEnergyCapacity;
    public static ForgeConfigSpec.IntValue eliteLaserTurretCooldown;
    public static ForgeConfigSpec.IntValue eliteLaserTurretDamage;
    public static ForgeConfigSpec.IntValue eliteLaserTurretEnergyCapacity;
    public static ForgeConfigSpec.IntValue ultimateLaserTurretCooldown;
    public static ForgeConfigSpec.IntValue ultimateLaserTurretDamage;
    public static ForgeConfigSpec.IntValue ultimateLaserTurretEnergyCapacity;


    static  {
        BUILDER.push("Mekanism Turrets Config");

        blacklistedEntities = BUILDER.comment("Entities which will never be targeted by turrets").defineListAllowEmpty("blacklistedEntities", List.of("minecraft:wolf", "minecraft:ender_dragon"), MekanismTurretsConfig::isEntityId);

        basicLaserTurretCooldown = BUILDER.comment("Cooldown of the Basic Laser Turret").defineInRange("basicLaserTurretCooldown", 80, 0, Integer.MAX_VALUE);
        basicLaserTurretDamage = BUILDER.comment("Damage of the Basic Laser Turret").defineInRange("basicLaserTurretDamage", 1, 0, Integer.MAX_VALUE);
        basicLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Basic Laser Turret").defineInRange("basicLaserTurretEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
        advancedLaserTurretCooldown = BUILDER.comment("Cooldown of the Advanced Laser Turret").defineInRange("advancedLaserTurretCooldown", 65, 0, Integer.MAX_VALUE);
        advancedLaserTurretDamage = BUILDER.comment("Damage of the Advanced Laser Turret").defineInRange("advancedLaserTurretDamage", 2, 0, Integer.MAX_VALUE);
        advancedLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Advanced Laser Turret").defineInRange("advancedLaserTurretEnergyCapacity", 40000, 0, Integer.MAX_VALUE);
        eliteLaserTurretCooldown = BUILDER.comment("Cooldown of the Elite Laser Turret").defineInRange("eliteLaserTurretCooldown", 50, 0, Integer.MAX_VALUE);
        eliteLaserTurretDamage = BUILDER.comment("Damage of the Elite Laser Turret").defineInRange("eliteLaserTurretDamage", 3, 0, Integer.MAX_VALUE);
        eliteLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Elite Laser Turret").defineInRange("eliteLaserTurretEnergyCapacity", 90000, 0, Integer.MAX_VALUE);
        ultimateLaserTurretCooldown = BUILDER.comment("Cooldown of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretCooldown", 35, 0, Integer.MAX_VALUE);
        ultimateLaserTurretDamage = BUILDER.comment("Damage of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretDamage", 4, 0, Integer.MAX_VALUE);
        ultimateLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretEnergyCapacity", 160000, 0, Integer.MAX_VALUE);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static boolean isEntityId(Object o) {
        return o instanceof String string && ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(string));
    }
}

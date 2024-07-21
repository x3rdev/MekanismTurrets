package com.github.x3r.mekanism_turrets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class MekanismTurretsConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedEntities;

    public static final ModConfigSpec.DoubleValue laserTurretRange;

    public static final ModConfigSpec.IntValue basicLaserTurretCooldown;
    public static final ModConfigSpec.DoubleValue basicLaserTurretDamage;
    public static final ModConfigSpec.IntValue basicLaserTurretEnergyCapacity;
    public static final ModConfigSpec.IntValue advancedLaserTurretCooldown;
    public static final ModConfigSpec.DoubleValue advancedLaserTurretDamage;
    public static final ModConfigSpec.IntValue advancedLaserTurretEnergyCapacity;
    public static final ModConfigSpec.IntValue eliteLaserTurretCooldown;
    public static final ModConfigSpec.DoubleValue eliteLaserTurretDamage;
    public static final ModConfigSpec.IntValue eliteLaserTurretEnergyCapacity;
    public static final ModConfigSpec.IntValue ultimateLaserTurretCooldown;
    public static final ModConfigSpec.DoubleValue ultimateLaserTurretDamage;
    public static final ModConfigSpec.IntValue ultimateLaserTurretEnergyCapacity;


    static  {
        BUILDER.push("Mekanism Turrets Config");

        List<String> defaultBlacklistedEntities = List.of(
                BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDER_DRAGON).toString(),
                BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.IRON_GOLEM).toString()
        );
        blacklistedEntities = BUILDER.comment("Entities which will never be targeted by turrets").defineListAllowEmpty("blacklistedEntities", defaultBlacklistedEntities, MekanismTurretsConfig::isEntityId);

        laserTurretRange = BUILDER.comment("Range of the Laser Turrets").defineInRange("laserTurretRange", 32D, 0D, 1000);

        basicLaserTurretCooldown = BUILDER.comment("Cooldown of the Basic Laser Turret").defineInRange("basicLaserTurretCooldown", 80, 0, Integer.MAX_VALUE);
        basicLaserTurretDamage = BUILDER.comment("Damage of the Basic Laser Turret").defineInRange("basicLaserTurretDamage", 1F, 0F, Float.MAX_VALUE);
        basicLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Basic Laser Turret").defineInRange("basicLaserTurretEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
        advancedLaserTurretCooldown = BUILDER.comment("Cooldown of the Advanced Laser Turret").defineInRange("advancedLaserTurretCooldown", 65, 0, Integer.MAX_VALUE);
        advancedLaserTurretDamage = BUILDER.comment("Damage of the Advanced Laser Turret").defineInRange("advancedLaserTurretDamage", 2F, 0F, Integer.MAX_VALUE);
        advancedLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Advanced Laser Turret").defineInRange("advancedLaserTurretEnergyCapacity", 40000, 0, Integer.MAX_VALUE);
        eliteLaserTurretCooldown = BUILDER.comment("Cooldown of the Elite Laser Turret").defineInRange("eliteLaserTurretCooldown", 50, 0, Integer.MAX_VALUE);
        eliteLaserTurretDamage = BUILDER.comment("Damage of the Elite Laser Turret").defineInRange("eliteLaserTurretDamage", 3F, 0F, Integer.MAX_VALUE);
        eliteLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Elite Laser Turret").defineInRange("eliteLaserTurretEnergyCapacity", 90000, 0, Integer.MAX_VALUE);
        ultimateLaserTurretCooldown = BUILDER.comment("Cooldown of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretCooldown", 35, 0, Integer.MAX_VALUE);
        ultimateLaserTurretDamage = BUILDER.comment("Damage of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretDamage", 4F, 0F, Integer.MAX_VALUE);
        ultimateLaserTurretEnergyCapacity = BUILDER.comment("Energy Capacity of the Ultimate Laser Turret").defineInRange("ultimateLaserTurretEnergyCapacity", 160000, 0, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static boolean isEntityId(Object o) {
        return o instanceof String string && BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(string));
    }
}

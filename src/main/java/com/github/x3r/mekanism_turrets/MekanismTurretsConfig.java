package com.github.x3r.mekanism_turrets;

import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretTier;
import mekanism.common.config.value.CachedIntValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MekanismTurretsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedEntities;

    public static final ForgeConfigSpec.DoubleValue laserTurretRange;

    public static final ForgeConfigSpec.IntValue basicLaserTurretCooldown;
    public static final ForgeConfigSpec.DoubleValue basicLaserTurretDamage;
    public static final ForgeConfigSpec.IntValue basicLaserTurretEnergyCapacity;
    public static final ForgeConfigSpec.IntValue advancedLaserTurretCooldown;
    public static final ForgeConfigSpec.DoubleValue advancedLaserTurretDamage;
    public static final ForgeConfigSpec.IntValue advancedLaserTurretEnergyCapacity;
    public static final ForgeConfigSpec.IntValue eliteLaserTurretCooldown;
    public static final ForgeConfigSpec.DoubleValue eliteLaserTurretDamage;
    public static final ForgeConfigSpec.IntValue eliteLaserTurretEnergyCapacity;
    public static final ForgeConfigSpec.IntValue ultimateLaserTurretCooldown;
    public static final ForgeConfigSpec.DoubleValue ultimateLaserTurretDamage;
    public static final ForgeConfigSpec.IntValue ultimateLaserTurretEnergyCapacity;


    static  {
        BUILDER.push("Mekanism Turrets Config");

        List<String> defaultBlacklistedEntities = List.of(
                ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ENDER_DRAGON).toString(),
                ForgeRegistries.ENTITY_TYPES.getKey(EntityType.IRON_GOLEM).toString()
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
        return o instanceof String string && ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(string));
    }
}

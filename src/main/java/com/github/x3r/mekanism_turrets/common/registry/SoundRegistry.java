package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MekanismTurrets.MOD_ID);

    public static final RegistryObject<SoundEvent> TURRET_SHOOT = SOUNDS.register("turret_shoot",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MekanismTurrets.MOD_ID, "turret_shoot")));
}

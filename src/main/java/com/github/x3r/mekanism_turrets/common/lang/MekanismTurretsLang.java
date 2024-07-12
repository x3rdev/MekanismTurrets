package com.github.x3r.mekanism_turrets.common.lang;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import net.minecraft.Util;
import net.minecraft.world.entity.EquipmentSlot;

import static mekanism.common.MekanismLang.*;

public enum MekanismTurretsLang implements ILangEntry {

    DESCRIPTION_LASER_TURRET("description", "laser_turret");

    private final String key;

    MekanismTurretsLang(String type, String path) {
        this(Util.makeDescriptionId(type, Mekanism.rl(path)));
    }

    MekanismTurretsLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

    public static MekanismLang get(EquipmentSlot type) {
        return switch (type) {
            case HEAD -> HEAD;
            case CHEST, BODY -> BODY;
            case LEGS -> LEGS;
            case FEET -> FEET;
            case MAINHAND -> MAINHAND;
            case OFFHAND -> OFFHAND;
        };
    }
}

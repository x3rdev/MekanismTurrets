package com.github.x3r.mekanism_turrets.client.renderer;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LaserTurretRenderer extends GeoBlockRenderer<LaserTurretBlockEntity> {

    public LaserTurretRenderer() {
        super(new DefaultedBlockGeoModel<>(new ResourceLocation(MekanismTurrets.MOD_ID, "basic_laser_turret")));
    }
}

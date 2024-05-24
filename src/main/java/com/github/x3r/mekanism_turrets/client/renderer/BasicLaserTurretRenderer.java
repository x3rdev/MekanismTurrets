package com.github.x3r.mekanism_turrets.client.renderer;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.BasicLaserTurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BasicLaserTurretRenderer extends GeoBlockRenderer<BasicLaserTurretBlockEntity> {

    public BasicLaserTurretRenderer() {
        super(new DefaultedBlockGeoModel<>(new ResourceLocation(MekanismTurrets.MOD_ID, "basic_laser_turret")));
    }

}

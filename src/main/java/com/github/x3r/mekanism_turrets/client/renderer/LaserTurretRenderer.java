package com.github.x3r.mekanism_turrets.client.renderer;

import com.github.x3r.mekanism_turrets.client.model.LaserTurretModel;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LaserTurretRenderer extends GeoBlockRenderer<LaserTurretBlockEntity> {

    public LaserTurretRenderer() {
        super(new LaserTurretModel());
    }
}

package com.github.x3r.mekanism_turrets.common.item;

import com.github.x3r.mekanism_turrets.client.model.DefaultedBlockItemGeoModel;
import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretTier;
import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.item.block.machine.ItemBlockMachine;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class LaserTurretBlockItem extends ItemBlockMachine implements GeoItem {

    private BlockEntityWithoutLevelRenderer renderer;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public LaserTurretBlockItem(LaserTurretBlock block) {
        super(block);
    }

    @Override
    public LaserTurretTier getTier() {
        return Attribute.getTier(getBlock(), LaserTurretTier.class);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(renderer == null) {

                    renderer = new GeoItemRenderer<GeckoBlockItem>(new DefaultedBlockItemGeoModel<>(ForgeRegistries.ITEMS.getKey(LaserTurretBlockItem.this)));
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

package com.github.x3r.mekanism_turrets.common.item;

import com.github.x3r.mekanism_turrets.client.model.DefaultedBlockItemGeoModel;
import com.github.x3r.mekanism_turrets.client.renderer.LaserTurretRenderer;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class LaserTurretBlockItem extends ItemBlockTooltip<BlockTile<?, ?>> implements GeoItem {
    private BlockEntityWithoutLevelRenderer renderer;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LaserTurretBlockItem(BlockTile<?, ?> block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if(renderer == null) {
                    renderer = new GeoItemRenderer<GeckoBlockItem>(new DefaultedBlockItemGeoModel<>(BuiltInRegistries.ITEM.getKey(LaserTurretBlockItem.this)));
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

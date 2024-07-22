package com.github.x3r.mekanism_turrets.common.block;

import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.DamageTypeRegistry;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ElectricFenceBlock extends IronBarsBlock implements IHasTileEntity<ElectricFenceBlockEntity> {
    public ElectricFenceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, pLevel, pPos);
        if (tile == null) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        } else if (pLevel.isClientSide) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return tile.tryWrench(pState, pPlayer, pStack).getInteractionResult();
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        pLevel.getBlockEntity(pPos, BlockEntityTypeRegistry.ELECTRIC_FENCE.get()).ifPresent(electricFence -> {
            IEnergyStorage energyStorage = electricFence.energyStorage;
            int total = energyStorage.getEnergyStored();
            if(total > 0 && pEntity instanceof LivingEntity && pEntity.isAlive()) {
                float ratio = (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
                boolean b = pEntity.hurt(new DamageTypeRegistry(pLevel.registryAccess()).electricFence(), ratio * 10.0F);
                if(b) {
                    energyStorage.extractEnergy((int) (energyStorage.getEnergyStored() * 0.1F), false);
                }
            }
        });
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    public TileEntityTypeRegistryObject<? extends ElectricFenceBlockEntity> getTileType() {
        return BlockEntityTypeRegistry.ELECTRIC_FENCE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : (pLevel1, pPos, pState1, pBlockEntity) -> ElectricFenceBlockEntity.serverTick(pLevel1, pPos, pState1, (ElectricFenceBlockEntity) pBlockEntity);
    }


}

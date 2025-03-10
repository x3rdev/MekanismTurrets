package com.github.x3r.mekanism_turrets.client.gui;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.packet.ModifyTurretTargetPayload;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.button.ToggleButton;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LaserTurretScreen extends GuiMekanismTile<LaserTurretBlockEntity, MekanismTileContainer<LaserTurretBlockEntity>> {

    private static final ResourceLocation TARGET_HOSTILE_OFF = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_hostile_off.png");
    private static final ResourceLocation TARGET_HOSTILE_ON = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_hostile_on.png");
    private static final ResourceLocation TARGET_PASSIVE_OFF = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_passive_off.png");
    private static final ResourceLocation TARGET_PASSIVE_ON = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_passive_on.png");
    private static final ResourceLocation TARGET_PLAYER_OFF = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_players_off.png");
    private static final ResourceLocation TARGET_PLAYER_ON = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_players_on.png");
    private static final ResourceLocation TARGET_TRUSTED_OFF = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_trusted_off.png");
    private static final ResourceLocation TARGET_TRUSTED_ON = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/gui/target_trusted_on.png");



    public LaserTurretScreen(MekanismTileContainer<LaserTurretBlockEntity> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, () -> {
                    MachineEnergyContainer<LaserTurretBlockEntity> energyContainer = tile.getEnergyContainer();
                    return energyContainer.getEnergyPerTick() > energyContainer.getEnergy();
                });
        long energyPerTick = tile.getEnergyContainer().getEnergyPerTick();
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(
                Component.translatable("gui.turret.energy_per_shot").append(EnergyDisplay.of(energyPerTick).getTextComponent()),
                MekanismLang.NEEDED.translate(EnergyDisplay.of(energyPerTick - tile.getEnergyContainer().getEnergy())))
        ));
        int i = 25;
        //Component.translatable("gui.turret.target_hostile")
        addRenderableWidget(new ToggleButton(this, 40, 33, 20, 20, TARGET_HOSTILE_OFF, TARGET_HOSTILE_ON, tile::targetsHostile,
                (element, mouseX, mouseY) -> {
                    PacketDistributor.sendToServer(new ModifyTurretTargetPayload(tile.getBlockPos(), (byte) 0, !tile.targetsHostile()));
                    return true;
                },
                Component.translatable("gui.turret.stop_targeting_hostile"),
                Component.translatable("gui.turret.start_targeting_hostile")
        ));
        addRenderableWidget(new ToggleButton(this, 40+i, 33, 20, 20, TARGET_PASSIVE_OFF, TARGET_PASSIVE_ON, tile::targetsPassive,
                (element, mouseX, mouseY) -> {
                    PacketDistributor.sendToServer(new ModifyTurretTargetPayload(tile.getBlockPos(), (byte) 1, !tile.targetsPassive()));
                    return true;
                },
                Component.translatable("gui.turret.stop_targeting_passive"),
                Component.translatable("gui.turret.start_targeting_passive")
        ));
        addRenderableWidget(new ToggleButton(this, 40+2*i, 33, 20, 20, TARGET_PLAYER_OFF, TARGET_PLAYER_ON, tile::targetsPlayers,
                (element, mouseX, mouseY) -> {
                    PacketDistributor.sendToServer(new ModifyTurretTargetPayload(tile.getBlockPos(), (byte) 2, !tile.targetsPlayers()));
                    return true;
                },
                Component.translatable("gui.turret.stop_targeting_players"),
                Component.translatable("gui.turret.start_targeting_players")
        ));
        addRenderableWidget(new ToggleButton(this, 40+3*i, 33, 20, 20, TARGET_TRUSTED_OFF, TARGET_TRUSTED_ON, tile::targetsTrusted,
                (element, mouseX, mouseY) -> {
                    PacketDistributor.sendToServer(new ModifyTurretTargetPayload(tile.getBlockPos(), (byte) 3, !tile.targetsTrusted()));
                    return true;
                },
                Component.translatable("gui.turret.stop_targeting_trusted"),
                Component.translatable("gui.turret.start_targeting_trusted")
        ){
            @Override
            public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                if(tile.targetsPlayers()){
                    super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public boolean isValidClickButton(int button) {
                return tile.targetsPlayers() && super.isValidClickButton(button);
            }

            @Override
            public void renderToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
                if(tile.targetsPlayers()){
                    super.renderToolTip(guiGraphics, mouseX, mouseY);
                }
            }
        });
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);

        drawTitleText(guiGraphics, playerInventoryTitle, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}

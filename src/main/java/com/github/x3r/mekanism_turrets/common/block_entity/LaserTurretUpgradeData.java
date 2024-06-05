package com.github.x3r.mekanism_turrets.common.block_entity;

import mekanism.common.upgrade.IUpgradeData;

public record LaserTurretUpgradeData(boolean targetsHostile, boolean targetsPassive, boolean targetsPlayers, boolean targetsTrusted) implements IUpgradeData {

}

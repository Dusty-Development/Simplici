package org.valkyrienskies.simplici.content.block.engine.thruster.propeller.simple_propeller

import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class SimplePropellerBlockEntity(pos: BlockPos, state: BlockState)
    : ThrusterBlockEntity(ModBlockEntities.SIMPLE_PROPELLER.get(), pos, state)
{

    override val thrusterForceKey: GameRules.Key<GameRules.IntegerValue> = ModGamerules.SIMPLE_PROPELLER_FORCE
    override val thrusterMaxSpeed: Double = 96.0
    override val thrusterSoftMaxSpeed: Double = 64.0

    override val fuelDefaultName: String = "simple_propeller"
}

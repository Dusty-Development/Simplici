package org.valkyrienskies.simplici.content.block.engine.thruster.firework_thruster

import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class FireworkThrusterBlockEntity(pos: BlockPos, state: BlockState)
    : ThrusterBlockEntity(ModBlockEntities.FIREWORK_THRUSTER.get(), pos, state)
{

    override val thrusterForceKey: GameRules.Key<GameRules.IntegerValue> = ModGamerules.FIREWORK_THRUSTER_FORCE
    override val thrusterMaxSpeed: Double = 256.0
    override val thrusterSoftMaxSpeed: Double = 128.0

    override val fuelDefaultName: String = "blast_propeller"

}

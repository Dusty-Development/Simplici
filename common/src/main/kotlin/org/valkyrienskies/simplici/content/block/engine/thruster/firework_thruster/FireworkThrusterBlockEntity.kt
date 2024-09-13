package org.valkyrienskies.simplici.content.block.engine.thruster.firework_thruster

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlockEntity

class FireworkThrusterBlockEntity(pos: BlockPos, state: BlockState)
    : ThrusterBlockEntity(ModBlockEntities.FIREWORK_THRUSTER.get(), pos, state)
{

    override val thrusterForce: Double = ModConfig.SERVER.FireworkThrusterForce
    override val thrusterMaxSpeed: Double = 256.0
    override val thrusterSoftMaxSpeed: Double = 128.0

    override val fuelDefaultName: String = "blast_propeller"

}

package org.valkyrienskies.simplici.content.block.engine.thruster.propeller.blast_propeller

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities

class BlastPropellerBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.BLAST_PROPELLER.get(), pos, state)
{
    var rotation:Double = 0.0
    var type: ThrusterMode = STATIC
}
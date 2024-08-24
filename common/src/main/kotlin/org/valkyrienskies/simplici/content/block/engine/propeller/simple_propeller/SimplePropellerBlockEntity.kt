package org.valkyrienskies.simplici.content.block.engine.propeller.simple_propeller

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterMode
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterMode.*

class SimplePropellerBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.SIMPLE_PROPELLER.get(), pos, state)
{
    var rotation:Double = 0.0
    var type: ThrusterMode = STATIC
}

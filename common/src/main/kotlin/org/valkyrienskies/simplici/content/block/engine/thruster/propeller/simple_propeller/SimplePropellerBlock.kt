package org.valkyrienskies.simplici.content.block.engine.thruster.propeller.simple_propeller

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlock

class SimplePropellerBlock : ThrusterBlock(
    Properties.of().mapColor(MapColor.STONE).strength(2.0F).sound(
        SoundType.STONE).noOcclusion())
{

    override val SHAPE: RotShape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = SimplePropellerBlockEntity(blockPos, blockState)

}
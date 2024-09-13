package org.valkyrienskies.simplici.content.block.engine.thruster.firework_thruster

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlock

class FireworkThrusterBlock : ThrusterBlock(
    Properties.of().mapColor(MapColor.WOOL).strength(1.0F).sound(
        SoundType.SWEET_BERRY_BUSH).noOcclusion())
{

    override val SHAPE: RotShape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = FireworkThrusterBlockEntity(blockPos, blockState)

}
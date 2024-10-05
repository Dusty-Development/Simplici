package org.valkyrienskies.simplici.content.block.tool.rope_hook.handle

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.tool.rope_hook.RopeHookBlock

class HandleBlock : RopeHookBlock(Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL).noOcclusion()) {

    override val SHAPE: RotShape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = HandleBlockEntity(blockPos, blockState)

}
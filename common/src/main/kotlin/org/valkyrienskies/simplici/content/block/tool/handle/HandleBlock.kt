package org.valkyrienskies.simplici.content.block.tool.handle

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.simplici.api.ModBaseBlock
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes

class HandleBlock : ModBaseBlock(Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL).noOcclusion()) {

    override val SHAPE: RotShape = RotShapes.box(2.0, 2.0, 0.0, 14.0, 14.0, 8.0)
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = HandleBlockEntity(blockPos, blockState)
    override fun blockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>): StateDefinition.Builder<Block, BlockState> {
        builder.add(FACING)
        return builder
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val dir = ctx.clickedFace.opposite
        return defaultBlockState().setValue(FACING, dir)
    }

}
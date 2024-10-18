package org.valkyrienskies.simplici.content.block.mechanical.head

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShapes

class MechanicalHeadBlock : BaseEntityBlock( Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound( SoundType.WOOD).noCollission().noOcclusion() )
{

    val SHAPE = RotShapes.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0)

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.up(SHAPE)[blockState.getValue(FACING)]

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MechanicalHeadBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun isSignalSource(state: BlockState): Boolean = true
    override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        val be = (level.getBlockEntity(pos) as MechanicalHeadBlockEntity)
        val signal = be.level?.getBestNeighborSignal(be.parentBlockPos)
        return signal!!
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _: Level, _: BlockPos, _: BlockState, t: T -> (t as MechanicalHeadBlockEntity).tick() }
    }

}
package org.valkyrienskies.simplici.content.block.engine.car.tractor

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
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
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlockEntity

class TractorEngineBlock : BaseEntityBlock(
    Properties.of().sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    private val shape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.north(shape)[blockState.getValue(
        FACING
    )]
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TractorEngineBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        (level.getBlockEntity(pos) as EngineBlockEntity).onPlaced()
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        (level.getBlockEntity(pos) as EngineBlockEntity).onRemoved()
        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.horizontalDirection
        if(ctx.player != null && !ctx.player!!.isShiftKeyDown)
            dir = dir.opposite
        return defaultBlockState()
            .setValue(FACING, dir)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {

        return BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
            (t as EngineBlockEntity).tick()
        }
    }

}
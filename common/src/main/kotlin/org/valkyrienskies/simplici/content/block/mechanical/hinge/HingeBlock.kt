package org.valkyrienskies.simplici.content.block.mechanical.hinge

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

class HingeBlock : BaseEntityBlock(
    Properties.of().sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    private val shape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0)

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape( blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext ): VoxelShape = DirectionalShape.up(shape)[blockState.getValue(FACING)]
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HingeBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        (level.getBlockEntity(pos) as HingeBlockEntity).onPlaced(state, level, pos, oldState, isMoving)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        (level.getBlockEntity(pos) as HingeBlockEntity).onRemoved(state, level, pos, newState, isMoving)
        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun use( state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult ): InteractionResult {
        (level.getBlockEntity(pos) as HingeBlockEntity).onUse(state, level, pos, player, hand, hit)
        return super.use(state, level, pos, player, hand, hit)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.nearestLookingDirection
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
            (t as HingeBlockEntity).tick()
        }
    }

}
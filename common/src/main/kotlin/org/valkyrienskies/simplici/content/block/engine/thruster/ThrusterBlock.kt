package org.valkyrienskies.simplici.content.block.engine.thruster

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.tool.handle.HandleBlockEntity

abstract class ThrusterBlock(properties: Properties) : BaseEntityBlock(properties)
{

    abstract val SHAPE: RotShape
    abstract override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.north(SHAPE)[blockState.getValue( FACING )]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    // PLAYER INTEGRATION \\
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        return (level.getBlockEntity(blockPos) as ThrusterBlockEntity).onUse(player,interactionHand,blockHitResult)
    }

    override fun onPlace(blockState: BlockState, level: Level, pos: BlockPos, blockState2: BlockState, bl: Boolean) {
        println(bl)
        if(blockState.block != blockState2.block)
        (level.getBlockEntity(pos) as ThrusterBlockEntity).onPlaced()
    }
    override fun playerWillDestroy(level: Level, blockPos: BlockPos, blockState: BlockState, player: Player) {
        if(!level.isClientSide) { (level.getBlockEntity(blockPos) as ThrusterBlockEntity).onRemoved() }
        super.playerWillDestroy(level, blockPos, blockState, player)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val dir = ctx.clickedFace
        return defaultBlockState().setValue(FACING, dir)
    }

    // WORLD \\

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _: Level, _: BlockPos, _: BlockState, t: T ->
            (t as ThrusterBlockEntity).tick()
        }
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)
    }
}

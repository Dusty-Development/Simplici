package org.valkyrienskies.simplici.content.block.logic.sensor

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
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
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult

class SensorBlock : BaseEntityBlock(
    Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(
        SoundType.STONE).noOcclusion()
) {

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SensorBlockEntity(pos, state)

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.nearestLookingDirection;
        if(ctx.player != null && !ctx.player!!.isShiftKeyDown)
            dir = dir.opposite
        return defaultBlockState()
            .setValue(FACING, dir)
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

    override fun isSignalSource(state: BlockState): Boolean {
        return true
    }

    override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        return (level.getBlockEntity(pos) as SensorBlockEntity).lastVal
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {

        return BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
            SensorBlockEntity.tick(
                levelB,
                posB,
                stateB,
                t
            )
        }
    }

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        val mode = (level.getBlockEntity(blockPos) as SensorBlockEntity).getMode(level, blockPos, blockState)
        player.sendSystemMessage(Component.literal("${mode.entity}, ${mode.color}, ${mode.dist}, ${mode.lensCount}"))
        return InteractionResult.CONSUME
    }

}
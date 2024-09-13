package org.valkyrienskies.simplici.content.block.tool.fuel_tank

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
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

class FuelTankBlock  : BaseEntityBlock(Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL).noOcclusion() )
{

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = FuelTankBlockEntity(pos, state)

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val dir = ctx.nearestLookingDirection.opposite
        return defaultBlockState().setValue(FACING, dir)
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

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker { _: Level, _: BlockPos, _: BlockState, t: T -> (t as FuelTankBlockEntity).tick() }
    override fun onPlace(blockState: BlockState, level: Level, pos: BlockPos, blockState2: BlockState, bl: Boolean) { (level.getBlockEntity(pos) as FuelTankBlockEntity).onPlaced() }
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) { (level.getBlockEntity(pos) as FuelTankBlockEntity).onRemoved() }
    override fun use(blockState: BlockState, level: Level, pos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult = (level.getBlockEntity(pos) as FuelTankBlockEntity).onUse(player, interactionHand, blockHitResult)


}
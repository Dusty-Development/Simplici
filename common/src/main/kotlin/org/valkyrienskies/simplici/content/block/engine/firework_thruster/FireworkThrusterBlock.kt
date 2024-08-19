package org.valkyrienskies.simplici.content.block.engine.firework_thruster

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
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterBlockHelper
import org.valkyrienskies.simplici.content.block.engine.blast_propeller.BlastPropellerBlockEntity
import org.valkyrienskies.simplici.content.ship.modules.thruster.IThrusterBlock
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterType

class FireworkThrusterBlock : BaseEntityBlock(
    Properties.of().mapColor(MapColor.WOOL).strength(1.0F).sound(
        SoundType.SWEET_BERRY_BUSH).noOcclusion()), IThrusterBlock {

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BlastPropellerBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWER)
        builder.add(BlockStateProperties.FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) = ThrusterBlockHelper.onThrusterPlaced(state, level, pos)

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) = ThrusterBlockHelper.onThrusterRemoved(level, pos)

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        ThrusterBlockHelper.onThrusterUse(state, level, pos)
        return super.use(state, level, pos, player, hand, hit)
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

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.clickedFace.opposite
        if(ctx.player != null && ctx.player!!.isShiftKeyDown)
            dir = dir.opposite
        return defaultBlockState()
                .setValue(BlockStateProperties.FACING, dir)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker {
                levelB: Level, posB: BlockPos, stateB: BlockState, _: T ->
            ThrusterBlockHelper.tickThruster(levelB, posB, stateB)
        }
    }

    override val thrusterType: ThrusterType get() = ThrusterType.FIREWORK_THRUSTER

}
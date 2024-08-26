package org.valkyrienskies.simplici.content.block.engine.propeller.simple_propeller

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
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType.*
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterBlockHelper
import org.valkyrienskies.simplici.content.ship.modules.thruster.IThrusterBlock
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterMode
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterType

class SimplePropellerBlock : BaseEntityBlock(
    Properties.of().mapColor(MapColor.STONE).strength(2.0F).sound(
        SoundType.STONE).noOcclusion()), IThrusterBlock
{

    private val shape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.up(shape)[blockState.getValue( FACING )]
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SimplePropellerBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWER)
        builder.add(BlockStateProperties.FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) = ThrusterBlockHelper.onThrusterPlaced(state, level, pos)

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) = ThrusterBlockHelper.onThrusterRemoved(level, pos)

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        if(level.isClientSide) return InteractionResult.CONSUME
        val be = level.getBlockEntity(pos) as SimplePropellerBlockEntity
        be.type = when (be.type) {
            ThrusterMode.STATIC -> ThrusterMode.DYNAMIC
            ThrusterMode.DYNAMIC -> ThrusterMode.STATIC
        }
        ThrusterBlockHelper.onThrusterUse(state, level, pos, be.type, player)
        return InteractionResult.CONSUME
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
        if(ctx.player != null && !ctx.player!!.isShiftKeyDown)
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
                levelB: Level, posB: BlockPos, stateB: BlockState, be: T ->
            ThrusterBlockHelper.tickThruster(levelB, posB, stateB)
        }
    }

    override val thrusterType:ThrusterType get() = ThrusterType.SIMPLE_PROPELLER

}
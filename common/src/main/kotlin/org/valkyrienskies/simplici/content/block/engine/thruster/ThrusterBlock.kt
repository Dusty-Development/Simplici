package org.valkyrienskies.simplici.content.block.engine.thruster

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.content.block.engine.thruster.propeller.simple_propeller.SimplePropellerBlockEntity

abstract class ThrusterBlock : BaseEntityBlock(Properties.of().sound(SoundType.STONE).strength(1.0f, 2.0f).noCollission().noParticlesOnBreak())
{

    abstract val SHAPE: RotShape
    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape =
        DirectionalShape.up(SHAPE)[blockState.getValue( BlockStateProperties.FACING )]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(blockState: BlockState, level: Level, pos: BlockPos, blockState2: BlockState, bl: Boolean) { (level.getBlockEntity(pos) as ThrusterBlockEntity).onPlaced() }
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) { (level.getBlockEntity(pos) as ThrusterBlockEntity).onRemoved() }
    override fun use(blockState: BlockState, level: Level, pos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult {
        (level.getBlockEntity(pos) as ThrusterBlockEntity).onUse(player, interactionHand, blockHitResult)
        return InteractionResult.CONSUME
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker {_: Level, _: BlockPos, _: BlockState, be: T -> (be as ThrusterBlockEntity).tick()}
}

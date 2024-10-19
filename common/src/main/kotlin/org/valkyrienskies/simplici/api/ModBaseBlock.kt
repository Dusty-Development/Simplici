package org.valkyrienskies.simplici.api

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
import org.valkyrienskies.simplici.api.util.RotShape

abstract class ModBaseBlock(properties: Properties) : BaseEntityBlock(properties)
{

    abstract val SHAPE: RotShape
    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.north(SHAPE)[blockState.getValue( FACING )]

    abstract override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState

    abstract fun blockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) : StateDefinition.Builder<Block, BlockState>
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) { super.createBlockStateDefinition(blockStateDefinition(builder)) }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, blockEntityType: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker { _: Level, _: BlockPos, _: BlockState, t: T -> (t as ModBaseBlockEntity).tick() }
    override fun onPlace(blockState: BlockState, level: Level, pos: BlockPos, blockState2: BlockState, bl: Boolean) { (level.getBlockEntity(pos) as ModBaseBlockEntity).onPlaced() }
    override fun playerWillDestroy(level: Level, blockPos: BlockPos, blockState: BlockState, player: Player) { (level.getBlockEntity(blockPos) as ModBaseBlockEntity).onRemoved(); super.playerWillDestroy(level, blockPos, blockState, player) }
    override fun use(blockState: BlockState, level: Level, pos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult = (level.getBlockEntity(pos) as ModBaseBlockEntity).onUse(player, interactionHand, blockHitResult)

}
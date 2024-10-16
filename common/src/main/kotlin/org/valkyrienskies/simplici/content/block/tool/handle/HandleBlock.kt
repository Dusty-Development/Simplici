package org.valkyrienskies.simplici.content.block.tool.handle

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
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.simplici.api.util.DirectionalShape
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes

class HandleBlock : BaseEntityBlock(Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL).noOcclusion()) {

    val SHAPE: RotShape = RotShapes.box(2.0, 2.0, 0.0, 14.0, 14.0, 8.0)
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = HandleBlockEntity(blockPos, blockState)

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext): VoxelShape = DirectionalShape.north(SHAPE)[blockState.getValue( FACING )]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        return (level.getBlockEntity(blockPos) as HandleBlockEntity).onUse(player,interactionHand,blockHitResult)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val dir = ctx.clickedFace.opposite
        return defaultBlockState().setValue(FACING, dir)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _: Level, _: BlockPos, _: BlockState, t: T ->
            (t as HandleBlockEntity).tick()
        }
    }

}
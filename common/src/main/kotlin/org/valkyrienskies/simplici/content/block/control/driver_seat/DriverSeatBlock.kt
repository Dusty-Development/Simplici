package org.valkyrienskies.simplici.content.block.control.driver_seat

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.simplici.content.ship.SimpliciShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos

class DriverSeatBlock() : BaseEntityBlock(
    Properties.of().mapColor(MapColor.STONE).strength(2.5F).sound(SoundType.NETHERITE_BLOCK)
) {

    init {
        registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        SimpliciShipControl.getOrCreate(ship)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipManagingPos(pos)?.getAttachment<SimpliciShipControl>()?.let { control -> {control.ship}}
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val blockEntity = level.getBlockEntity(pos) as DriverSeatBlockEntity

        return if (player.isSecondaryUseActive) {
//            player.openMenu(blockEntity)
            InteractionResult.CONSUME
        } else if (level.getShipManagingPos(pos) == null) {
            player.displayClientMessage(Component.literal("Sneak to open the ship helm!"), true)
            InteractionResult.CONSUME
        } else if (blockEntity.sit(player)) {
            InteractionResult.CONSUME
        } else InteractionResult.PASS
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? = defaultBlockState() .setValue(HORIZONTAL_FACING, ctx.horizontalDirection.opposite)
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) { builder.add(HORIZONTAL_FACING) }
    override fun newBlockEntity(blockPos: BlockPos, state: BlockState): BlockEntity = DriverSeatBlockEntity(blockPos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker
        if (blockEntity is DriverSeatBlockEntity) {
            blockEntity.tick()
        }
    }
}

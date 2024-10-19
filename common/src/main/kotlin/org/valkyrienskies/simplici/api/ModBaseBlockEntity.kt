package org.valkyrienskies.simplici.api

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

abstract class ModBaseBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(blockEntityType, pos, state)
{
    // EVENTS \\
    open fun tick() {}

    open fun onPlaced() {}
    open fun onRemoved() {}

    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) : InteractionResult { return InteractionResult.PASS }
}
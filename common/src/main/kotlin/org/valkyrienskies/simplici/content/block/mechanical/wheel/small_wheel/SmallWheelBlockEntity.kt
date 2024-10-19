package org.valkyrienskies.simplici.content.block.mechanical.wheel.small_wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class SmallWheelBlockEntity(pos: BlockPos, state: BlockState) : WheelBlockEntity(ModBlockEntities.SMALL_WHEEL.get(), pos, state) {
    override val wheelRadius = 0.5
    override val wheelMaxDistance = ModGamerules.SMALL_WHEEL_MAX_LENGTH // From center of block to center of wheel in rest
    override val wheelRestDistance = ModGamerules.SMALL_WHEEL_REST_HEIGHT
}
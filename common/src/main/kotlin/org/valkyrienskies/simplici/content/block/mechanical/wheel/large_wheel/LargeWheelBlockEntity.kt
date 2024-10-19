package org.valkyrienskies.simplici.content.block.mechanical.wheel.large_wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class LargeWheelBlockEntity(pos: BlockPos, state: BlockState) : WheelBlockEntity(ModBlockEntities.LARGE_WHEEL.get(), pos, state) {
    override val wheelRadius = 1.0
    override val wheelMaxDistance = ModGamerules.LARGE_WHEEL_MAX_LENGTH // From center of block to center of wheel in rest
    override val wheelRestDistance = ModGamerules.LARGE_WHEEL_REST_HEIGHT
}
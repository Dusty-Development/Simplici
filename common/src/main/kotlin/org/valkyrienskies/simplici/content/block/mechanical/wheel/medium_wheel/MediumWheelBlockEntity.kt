package org.valkyrienskies.simplici.content.block.mechanical.wheel.medium_wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class MediumWheelBlockEntity(pos: BlockPos, state: BlockState) : WheelBlockEntity(ModBlockEntities.MEDIUM_WHEEL.get(), pos, state) {
    override val wheelRadius = 0.75
    override val wheelRestHeight = ModGamerules.MEDIUM_WHEEL_REST_HEIGHT // From center of block to center of wheel in restFrom center of block to center of wheel in rest
    override val wheelDistanceLimit = ModGamerules.MEDIUM_WHEEL_MAX_LENGTH
}
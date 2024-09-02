package org.valkyrienskies.simplici.content.block.mechanical.wheel.medium_wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity

class MediumWheelBlockEntity(pos: BlockPos, state: BlockState) : WheelBlockEntity(ModBlockEntities.MEDIUM_WHEEL.get(), pos, state) {
    override val wheelRadius = 0.75
    override val wheelRestHeight = 1.0 // From center of block to center of wheel in rest
    override val wheelDistanceLimit = 1.5
}
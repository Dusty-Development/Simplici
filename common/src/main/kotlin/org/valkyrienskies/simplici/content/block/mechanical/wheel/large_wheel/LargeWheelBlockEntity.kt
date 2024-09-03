package org.valkyrienskies.simplici.content.block.mechanical.wheel.large_wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity

class LargeWheelBlockEntity(pos: BlockPos, state: BlockState) : WheelBlockEntity(ModBlockEntities.LARGE_WHEEL.get(), pos, state) {
    override val wheelRadius = 1.0
    override val wheelRestHeight = ModConfig.SERVER.LargeWheelRestHeight // From center of block to center of wheel in rest
    override val wheelDistanceLimit = ModConfig.SERVER.LargeWheelMaxLength
}
package org.valkyrienskies.simplici.content.block.engine.car.race

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.api.util.InterpolationCurve
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity

class RaceEngineBlockEntity(pos: BlockPos, state: BlockState) : EngineBlockEntity(ModBlockEntities.RACE_ENGINE.get(), pos, state) {
    override val maxPower: Double = 150000.0
    override val maxSpeed: Double = 75.0
    override val powerCurve: InterpolationCurve = InterpolationCurve()

    init {
        powerCurve.createDataPoint(0.0, 1.0) // X is speed Y is torque output
        powerCurve.createDataPoint(1.0, 0.0)
    }
}
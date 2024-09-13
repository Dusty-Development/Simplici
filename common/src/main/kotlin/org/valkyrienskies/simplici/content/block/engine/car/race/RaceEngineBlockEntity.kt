package org.valkyrienskies.simplici.content.block.engine.car.race

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.util.InterpolationCurve
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlockEntity

class RaceEngineBlockEntity(pos: BlockPos, state: BlockState) : EngineBlockEntity(ModBlockEntities.RACE_ENGINE.get(), pos, state) {
    override val maxPower: Double = ModConfig.SERVER.RaceEngineTorque
    override val maxSpeed: Double = ModConfig.SERVER.RaceEngineSpeed
    override val powerCurve: InterpolationCurve = InterpolationCurve()

    override val fuelDefaultName: String = "race_engine"

    init {
        powerCurve.createDataPoint(0.0, 1.0) // X is speed Y is torque output
        powerCurve.createDataPoint(1.0, 0.0)
    }
}
package org.valkyrienskies.simplici.content.block.engine.car.tractor

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.util.InterpolationCurve
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlockEntity

class TractorEngineBlockEntity(pos: BlockPos, state: BlockState) : EngineBlockEntity(ModBlockEntities.TRACTOR_ENGINE.get(), pos, state) {
    override val maxPower: Double = ModConfig.SERVER.TractorEngineTorque
    override val maxSpeed: Double = ModConfig.SERVER.TractorEngineSpeed
    override val powerCurve: InterpolationCurve = InterpolationCurve()

    override val fuelDefaultName: String = "tractor_engine"

    init {
        powerCurve.createDataPoint(0.0, 1.0) // X is speed Y is torque output
        powerCurve.createDataPoint(1.0, 0.0)
    }
}
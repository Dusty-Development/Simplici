package org.valkyrienskies.simplici.content.block.engine.car.tractor

import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.api.util.InterpolationCurve
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class TractorEngineBlockEntity(pos: BlockPos, state: BlockState) : EngineBlockEntity(ModBlockEntities.TRACTOR_ENGINE.get(), pos, state) {
    override var maxPowerKey: GameRules.Key<GameRules.IntegerValue> = ModGamerules.TRACTOR_ENGINE_MAX_POWER
    override var maxSpeedKey: GameRules.Key<GameRules.IntegerValue> = ModGamerules.TRACTOR_ENGINE_MAX_SPEED
    override val powerCurve: InterpolationCurve = InterpolationCurve()

    override val fuelDefaultName: String = "tractor_engine"

    init {
        powerCurve.createDataPoint(0.0, 0.5)
        powerCurve.createDataPoint(0.1, 0.55)
        powerCurve.createDataPoint(0.2, 0.6)
        powerCurve.createDataPoint(0.3, 0.7)
        powerCurve.createDataPoint(0.4, 0.85)
        powerCurve.createDataPoint(0.5, 0.95)
        powerCurve.createDataPoint(0.6, 1.0)
        powerCurve.createDataPoint(0.7, 1.0)
        powerCurve.createDataPoint(0.8, 0.9)
        powerCurve.createDataPoint(0.9, 0.7)
        powerCurve.createDataPoint(1.0, 0.0)
    }
}
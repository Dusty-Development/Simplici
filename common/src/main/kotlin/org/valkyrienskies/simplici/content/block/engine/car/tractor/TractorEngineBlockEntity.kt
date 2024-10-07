package org.valkyrienskies.simplici.content.block.engine.car.tractor

import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
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

    }
}
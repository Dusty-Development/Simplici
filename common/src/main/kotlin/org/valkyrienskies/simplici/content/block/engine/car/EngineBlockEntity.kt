package org.valkyrienskies.simplici.content.block.engine.car

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.simplici.api.util.InterpolationCurve
import org.valkyrienskies.simplici.content.block.FuelConsumerBlockEntity
import org.valkyrienskies.simplici.content.ship.modules.wheel.EngineData
import org.valkyrienskies.simplici.content.ship.modules.wheel.WheelControlModule

abstract class EngineBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : FuelConsumerBlockEntity(blockEntityType, pos, state)
{

    abstract val maxPowerKey: GameRules.Key<GameRules.IntegerValue>
    abstract val maxSpeedKey: GameRules.Key<GameRules.IntegerValue>
    abstract val powerCurve: InterpolationCurve

    // EVENTS \\

    override fun tick() {
        super.tick()
        shouldRefuel = true
        if (level!!.isClientSide) return

        val data = EngineData()
        data.maxPower = level!!.gameRules.getInt(maxPowerKey).toDouble()
        data.maxSpeed = level!!.gameRules.getInt(maxSpeedKey).toDouble()
        data.powerCurve = powerCurve
        data.isFueled = hasFuel && shouldRefuel

        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) WheelControlModule.getOrCreate(constrainedShip).addOrUpdateEngine(blockPos, data)
    }

    override fun onRemoved() {
        super.onRemoved()
        if (level!!.isClientSide) return
        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) WheelControlModule.getOrCreate(constrainedShip).removeEngine(blockPos)
    }
}
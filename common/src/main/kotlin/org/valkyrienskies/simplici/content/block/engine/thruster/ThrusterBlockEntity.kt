package org.valkyrienskies.simplici.content.block.engine.thruster

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.simplici.content.block.FuelConsumerBlockEntity
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterControlModule
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterForcesData

abstract class ThrusterBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : FuelConsumerBlockEntity(blockEntityType, pos, state)
{
    abstract val thrusterForce:Double
    abstract val thrusterMaxSpeed:Double
    abstract val thrusterSoftMaxSpeed:Double

    // State
    var animationAngle = 0.0 // Really only used for propellers but maybe a future thruster fan
    var isReversed = false
    var gimbalAngle = 0.0

    fun getThrusterData(ship: LoadedShip):ThrusterForcesData {
        val data = ThrusterForcesData(state = blockState)

        val bestNeighborSignal = level!!.getBestNeighborSignal(blockPos)
        shouldRefuel = bestNeighborSignal > 0
        data.throttle = bestNeighborSignal / 15.0
        data.force = thrusterForce
        data.maxSpeed = thrusterMaxSpeed
        data.softMaxSpeed = thrusterSoftMaxSpeed
        data.isReversed = isReversed
        data.gimbalAngle = gimbalAngle
        data.isFueled = hasFuel && shouldRefuel

        return data
    }

    // EVENTS \\

    override fun tick() {
        super.tick()
        val ship = level.getShipObjectManagingPos(blockPos)
        val data = ship?.let { getThrusterData(it) }

        if (level!!.isClientSide) return
        if (ship != null) data?.let { ThrusterControlModule.getOrCreate(ship as ServerShip).addThruster(blockPos, it) }
    }

    override fun onRemoved() {
        super.onRemoved()
        if (level!!.isClientSide) return
        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) ThrusterControlModule.getOrCreate(constrainedShip).removeThruster(blockPos)
    }

    override fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) : InteractionResult {
        if(player.isShiftKeyDown) {
            isReversed = !isReversed
            if(level?.isClientSide == false)player.sendSystemMessage(Component.literal("Is reversed: ${if (isReversed) "yes" else "no"}."))
            return InteractionResult.SUCCESS
        }

        return super.onUse(player, hand, hit)
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        isReversed = tag.getBoolean("IsReversed")
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putBoolean("IsReversed", isReversed)
        super.saveAdditional(tag)
    }
}
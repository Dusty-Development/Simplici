package org.valkyrienskies.simplici.content.block.engine.thruster

import dev.architectury.registry.fuel.FuelRegistry
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.StackedContentsCompatible
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.simplici.api.util.KtContainerData
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType.*
import org.valkyrienskies.simplici.content.ship.ModShipControl
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterControlModule
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterForcesData
import org.valkyrienskies.simplici.content.ship.modules.wheel.WheelControlModule

abstract class ThrusterBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BaseContainerBlockEntity(blockEntityType, pos, state), StackedContentsCompatible, WorldlyContainer
{
    abstract val thrusterForce:Double
    abstract val thrusterSpeed:Double

    // Fuel
    val data = KtContainerData()
    var fuelStack:ItemStack = ItemStack.EMPTY
    var fuelPoweredTicks = 0
    var shouldRefuel = false
    var hasFuel = false

    // State
    var rotation = 0.0
    var isReversed = false

    fun tickFuelConsumption() {
        // If the fuel amount is zero we want to grab another item
        if(fuelPoweredTicks <= 0 && shouldRefuel) {
            if(fuelStack.isEmpty) {
                // We are out of fuel with no items to get more from
                hasFuel = false
                return
            } else {
                // Add fuel amount to powered ticks
                val fuelValue = FuelRegistry.get(fuelStack)
                fuelPoweredTicks += fuelValue
                fuelStack.shrink(1)
            }
        }

        // We know we have fuel at this point
        fuelPoweredTicks -= 1
    }

    fun getThrusterData(ship: LoadedShip):ThrusterForcesData {
        val data = ThrusterForcesData(state = blockState)

        val bestNeighborSignal = level!!.getBestNeighborSignal(blockPos)
        shouldRefuel = bestNeighborSignal > 0

        return data
    }

    // EVENTS \\

    open fun tick() {
        val ship = level.getShipObjectManagingPos(blockPos)
        val data = ship?.let { getThrusterData(it) }
        tickFuelConsumption()

        if (level!!.isClientSide) return
        if (ship != null) data?.let { ThrusterControlModule.getOrCreate(ship as ServerShip).addThruster(blockPos, it) }
    }

    open fun onPlaced() { } // Dont worry about it. forces should be assigned first tick

    open fun onRemoved() {
        if (level!!.isClientSide) return
        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) ThrusterControlModule.getOrCreate(constrainedShip).removeThruster(blockPos)
    }

    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) {
        isReversed = !isReversed
        if(level?.isClientSide == false) player.sendSystemMessage(Component.literal("Is reversed: ${if(isReversed) "yes" else "no"}."))
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
    }
}
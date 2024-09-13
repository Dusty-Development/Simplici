package org.valkyrienskies.simplici.content.gui.fuel.consumer

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.FurnaceFuelSlot.isBucket
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.simplici.api.container.ItemHelper
import org.valkyrienskies.simplici.api.util.KtContainerData
import org.valkyrienskies.simplici.api.util.inventorySlots
import org.valkyrienskies.simplici.content.block.FuelConsumerBlockEntity
import org.valkyrienskies.simplici.content.gui.ModScreens
import org.valkyrienskies.simplici.content.gui.fuel.FuelSlot

class FuelConsumerMenu(syncId: Int, playerInv: Inventory, val blockEntity: FuelConsumerBlockEntity?) :
    AbstractContainerMenu(ModScreens.FUEL_CONSUMER.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    private val container: Container = blockEntity ?: SimpleContainer(1)
    private val data = blockEntity?.data?.clone() ?: KtContainerData()
    var fuelLeft by data
    var fuelTotal by data

    init {
        addSlot(FuelSlot(container, 0, 80, 57))
        inventorySlots(::addSlot, playerInv)
        addDataSlots(data)
    }

    override fun stillValid(player: Player): Boolean = container.stillValid(player)

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = this.slots[index]
        if (slot.hasItem() && (ItemHelper.getFuelTime(slot.item) > 0 || isBucket(slot.item))) {
            if (index != 0) {
                this.moveItemStackTo(slot.item, 0, 1, false)
                slot.setChanged()
            } else {
                slot.onTake(player, slot.item)
                this.moveItemStackTo(slot.item, 1, 37, true)
            }
        }

        return ItemStack.EMPTY
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> FuelConsumerMenu = ::FuelConsumerMenu
    }
}
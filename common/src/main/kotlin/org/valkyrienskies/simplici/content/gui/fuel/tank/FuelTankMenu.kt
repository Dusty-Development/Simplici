package org.valkyrienskies.simplici.content.gui.fuel.tank

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import org.joml.Vector2i
import org.valkyrienskies.simplici.api.util.KtContainerData
import org.valkyrienskies.simplici.api.util.inventorySlots
import org.valkyrienskies.simplici.content.block.tool.fuel_tank.FuelTankBlockEntity
import org.valkyrienskies.simplici.content.gui.ModScreens
import org.valkyrienskies.simplici.content.gui.fuel.FuelSlot

class FuelTankMenu(syncId: Int, playerInv: Inventory, val blockEntity: FuelTankBlockEntity?) :
    AbstractContainerMenu(ModScreens.FUEL_TANK.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)


    private val container: Container = blockEntity ?: SimpleContainer(SIZE_X * SIZE_Y) // 6 X 3 slots
    private val data = blockEntity?.data?.clone() ?: KtContainerData()


    init {
        var index = 0
        for (y in 0..<SIZE_Y) {
            for (x in 0..<SIZE_X) {
                addSlot(FuelSlot(container, index, (x*18) + offset.x, (y*18) + offset.y))
                index++
            }
        }
        println(index)
        inventorySlots(::addSlot, playerInv)
        addDataSlots(data)
    }

    override fun stillValid(player: Player): Boolean = container.stillValid(player)

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[i]

        if (slot.hasItem()) {
            val itemStack2 = slot.item
            itemStack = itemStack2.copy()
            if (i < SIZE_X * SIZE_Y) {
                if (!this.moveItemStackTo(
                        itemStack2, SIZE_X * SIZE_Y,
                        slots.size, true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, SIZE_X * SIZE_Y, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return itemStack
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> FuelTankMenu = ::FuelTankMenu

        const val SIZE_X = 6
        const val SIZE_Y = 3

        val offset = Vector2i(36,18)
    }
}
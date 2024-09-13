package org.valkyrienskies.simplici.content.gui.fuel

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.simplici.api.container.ItemHelper

class FuelSlot(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {

    override fun mayPlace(stack: ItemStack): Boolean = ItemHelper.getFuelTime(stack) > 0
    override fun getMaxStackSize(stack: ItemStack): Int = super.getMaxStackSize(stack)

}
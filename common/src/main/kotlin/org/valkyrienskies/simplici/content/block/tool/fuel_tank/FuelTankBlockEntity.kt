package org.valkyrienskies.simplici.content.block.tool.fuel_tank

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.StackedContentsCompatible
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.simplici.api.container.ItemHelper
import org.valkyrienskies.simplici.api.util.KtContainerData
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.gui.fuel.tank.FuelTankMenu

class FuelTankBlockEntity(pos:BlockPos, state: BlockState) : BaseContainerBlockEntity(ModBlockEntities.FUEL_TANK.get(), pos, state), StackedContentsCompatible, WorldlyContainer
{

    // Fuel
    val data = KtContainerData()
    var fuelStacks: Array<ItemStack> = Array(getContainerSize()) { ItemStack.EMPTY }

    // EVENTS \\

    open fun tick() { }
    open fun onPlaced() { } // Dont worry about it. forces should be assigned first tick
    open fun onRemoved() {
        if (!level?.isClientSide!!)
            // Drop inventory
            fuelStacks.forEach {
                if (!it.isEmpty) level!!.addFreshEntity(
                    ItemEntity(
                        level!!,
                        blockPos.x.toDouble(),
                        blockPos.y.toDouble(),
                        blockPos.z.toDouble(),
                        it
                    )
                )
            }
    }
    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) : InteractionResult {
        if (level?.isClientSide == true) return InteractionResult.SUCCESS
        player.openMenu(this)
        return InteractionResult.CONSUME
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
//        val slotsList = tag.getList("FuelSlots", 10)
//        for (i in 0..fuelStacks.size) {
//            val savedData = slotsList.getCompound(i)
//            fuelStacks[i] =  ItemStack.of(savedData)
//        }
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
//        val slotsList = ListTag()
//        tag.put("FuelSlots", slotsList)
//
//        for (i in 0..fuelStacks.size) {
//            slotsList[i] = fuelStacks[i].save(CompoundTag())
//        }
        super.saveAdditional(tag)
    }

    // Container

    override fun getContainerSize(): Int = FuelTankMenu.SIZE_X * FuelTankMenu.SIZE_Y
    override fun isEmpty(): Boolean {
        fuelStacks.forEach { if (!it.isEmpty) return false }
        return true
    }
    override fun clearContent() { for (i in 0..fuelStacks.size) fuelStacks[i] = ItemStack.EMPTY }

    override fun setItem(slot: Int, itemStack: ItemStack) { fuelStacks[slot] = itemStack }
    override fun getItem(slot: Int): ItemStack {
        if(slot < 0 || slot >= 18) return ItemStack.EMPTY
        return fuelStacks[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack = ContainerHelper.removeItem(fuelStacks.toList(), slot, amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack {
        fuelStacks[slot] = ItemStack.EMPTY
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return if (level!!.getBlockEntity(worldPosition) !== this) false else player.distanceToSqr(
            worldPosition.x.toDouble() + 0.5,
            worldPosition.y.toDouble() + 0.5,
            worldPosition.z.toDouble() + 0.5
        ) <= 64.0
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu = FuelTankMenu(containerId, inventory, this)
    override fun getDefaultName(): Component = Component.translatable("gui.simplici.fuel_tank")

    // For hoppers / pipes
    override fun getSlotsForFace(direction: Direction): IntArray = (0..fuelStacks.size).toList().toIntArray()
    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean = direction != Direction.DOWN && canPlaceItem(index, itemStack)
    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean = direction == Direction.DOWN && !isEmpty()
    override fun canPlaceItem(i: Int, itemStack: ItemStack): Boolean = ItemHelper.getFuelTime(itemStack) > 0
    override fun fillStackedContents(helper: StackedContents) = fuelStacks.forEach { helper.accountStack(it) }
}
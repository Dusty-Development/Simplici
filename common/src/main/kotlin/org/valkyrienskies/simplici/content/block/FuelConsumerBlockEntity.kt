package org.valkyrienskies.simplici.content.block

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
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.simplici.api.container.ItemHelper
import org.valkyrienskies.simplici.api.util.KtContainerData
import org.valkyrienskies.simplici.content.gamerule.ModGamerules
import org.valkyrienskies.simplici.content.gui.fuel.consumer.FuelConsumerMenu
import kotlin.math.roundToInt

abstract class FuelConsumerBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BaseContainerBlockEntity(blockEntityType, pos, state), StackedContentsCompatible, WorldlyContainer
{

    abstract val fuelDefaultName:String

    // Fuel
    val data = KtContainerData()
    var fuelStack: ItemStack = ItemStack.EMPTY
    var fuelPoweredTicks = 0.0
    var shouldRefuel = false // set to true if using fuel
    var hasFuel = false

    fun tickFuelConsumption() {
        if(level?.isClientSide == true) return

        val rate = (level!!.gameRules.getInt(ModGamerules.FUEL_CONSUMPTION_PERCENTAGE) * 0.01)

        // If the fuel amount is zero we want to grab another item
        if(fuelPoweredTicks <= 0 && shouldRefuel) {
            if(fuelStack.isEmpty) {
                // We are out of fuel with no items to get more from
                hasFuel = false
                return
            } else {
                // Add fuel amount to powered ticks
                val fuelValue = ItemHelper.getFuelTime(fuelStack)
                fuelPoweredTicks += fuelValue
                fuelStack.shrink(1)
                setChanged()
                println("")
            }
        }

        // We know we have fuel at this point
        hasFuel = true
        fuelPoweredTicks -= rate
    }

    // EVENTS \\

    open fun tick() { tickFuelConsumption() }
    open fun onPlaced() { tickFuelConsumption() }
    open fun onRemoved() {
        if (!level?.isClientSide!!) {
            // Drop inventory
            if (!fuelStack.isEmpty)
                level?.let { ItemEntity(it, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), fuelStack) }
                ?.let { level!!.addFreshEntity(it) }
        }
    }
    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) : InteractionResult {
        if (level?.isClientSide == true) return InteractionResult.SUCCESS
        val blockEntity = level!!.getBlockEntity(blockPos) as FuelConsumerBlockEntity

        player.openMenu(blockEntity)

        return InteractionResult.CONSUME
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        fuelStack = ItemStack.of(tag.getCompound("FuelSlot"))
        fuelPoweredTicks = tag.getDouble("FuelPoweredTicks")
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("FuelSlot", fuelStack.save(CompoundTag()))
        tag.putDouble("FuelPoweredTicks", fuelPoweredTicks)
        super.saveAdditional(tag)
    }

    // Container

    override fun getContainerSize(): Int = 1
    override fun isEmpty(): Boolean = fuelStack.isEmpty
    override fun clearContent() { fuelStack = ItemStack.EMPTY }

    override fun setItem(slot: Int, itemStack: ItemStack) { if (slot == 0) fuelStack = itemStack }
    override fun getItem(slot: Int): ItemStack = if (slot == 0) fuelStack else ItemStack.EMPTY

    override fun removeItem(slot: Int, amount: Int): ItemStack = ContainerHelper.removeItem(listOf(fuelStack), slot, amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == 0) fuelStack = ItemStack.EMPTY
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return if (level!!.getBlockEntity(worldPosition) !== this) false else player.distanceToSqr(
            worldPosition.x.toDouble() + 0.5,
            worldPosition.y.toDouble() + 0.5,
            worldPosition.z.toDouble() + 0.5
        ) <= 64.0
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu = FuelConsumerMenu(containerId, inventory, this)
    override fun getDefaultName(): Component = Component.translatable("gui.simplici.$fuelDefaultName")

    // For hoppers / pipes
    override fun getSlotsForFace(direction: Direction): IntArray = intArrayOf(0)
    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean = direction != Direction.DOWN && canPlaceItem(index, itemStack)
    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean = index == 0 && direction == Direction.DOWN && !fuelStack.isEmpty && ItemHelper.getFuelTime(fuelStack) <= 0
    override fun canPlaceItem(i: Int, itemStack: ItemStack): Boolean = ItemHelper.getFuelTime(itemStack) > 0
    override fun fillStackedContents(helper: StackedContents) = helper.accountStack(fuelStack)
}
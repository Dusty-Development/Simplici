package org.valkyrienskies.simplici.content.block.tool.handle

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

abstract class RopeHookBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(blockEntityType, pos, state)
{


    // EVENTS \\

    open fun tick() {  }
    open fun onPlaced() {  }
    open fun onRemoved() { }
    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) : InteractionResult {
        if (level?.isClientSide == true) return InteractionResult.SUCCESS
        return InteractionResult.CONSUME
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
//        fuelStack = ItemStack.of(tag.getCompound("FuelSlot"))
//        fuelPoweredTicks = tag.getInt("FuelPoweredTicks")
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
//        tag.put("FuelSlot", fuelStack.save(CompoundTag()))
//        tag.putInt("FuelPoweredTicks", fuelPoweredTicks)
        super.saveAdditional(tag)
    }

}
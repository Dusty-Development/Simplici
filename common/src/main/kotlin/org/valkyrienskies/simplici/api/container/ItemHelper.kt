package org.valkyrienskies.simplici.api.container

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlock
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlock

object ItemHelper {

    fun getFuelTime(item:ItemStack):Int = AbstractFurnaceBlockEntity.getFuel()[item.item] ?: 0
    fun isFuelConsumer(block:Block):Boolean {
        if(block is ThrusterBlock) return true
        if(block is EngineBlock) return true
        if(block is AbstractFurnaceBlock) return true
        return false
    }
}
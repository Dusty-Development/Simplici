package org.valkyrienskies.simplici.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.content.item.ModItems

object CreativeTabs {

    private val noTabBlocks = arrayListOf("pilot_seat", "passenger_seat", "mechanical_head")

    fun create(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.simplici"))
            .icon { ItemStack(ModBlocks.HINGE.get().asItem()) }
            .displayItems { _, output ->
                ModItems.ITEMS.forEach {
                    if (!noTabBlocks.contains(it.name)) output.accept(it.get())
                }
            }
            .build()
    }
}

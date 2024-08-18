package org.valkyrienskies.simplici.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.content.item.ModItems

object CreativeTabs {
    fun create(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.simplici"))
            .icon { ItemStack(ModBlocks.CONTROL_PANEL.get().asItem()) }
            .displayItems { _, output ->
                ModItems.ITEMS.forEach {
                    output.accept(it.get())
                }
            }
            .build()
    }
}

package org.valkyrienskies.simplici.content.item

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.registry.DeferredRegister

@Suppress("unused")
object ModItems {
    internal val ITEMS = DeferredRegister.create(Simplici.MOD_ID, Registries.ITEM)
    val TAB: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation(Simplici.MOD_ID, "simplici_tab"))

//    val HAMMER = ITEMS.register("hammer", ::GyroRotator)

    fun register() {
        ModBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}

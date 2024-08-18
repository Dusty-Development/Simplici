package org.valkyrienskies.simplici.api.services

import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.function.Supplier

// TODO: Delete this?
interface ModPlatformHelper {
    fun loadBakedModel(modelLocation: ResourceLocation): BakedModel?

    companion object {
        fun get() = ServiceLoader
            .load(ModPlatformHelper::class.java)
            .findFirst()
            .get()
    }
}

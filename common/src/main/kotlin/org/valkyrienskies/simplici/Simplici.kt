package org.valkyrienskies.simplici

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.content.entity.ModEntities
import org.valkyrienskies.simplici.content.item.ModItems
import org.valkyrienskies.simplici.content.gui.ModClientScreens
import org.valkyrienskies.simplici.content.gui.ModScreens
import org.valkyrienskies.simplici.content.network.ModNetworking

object Simplici {
    const val MOD_ID = "simplici"

    @JvmStatic
    fun init() {
        ModBlocks.register()
        ModBlockEntities.register()
        ModItems.register()
        ModScreens.register()
        ModEntities.register()

        ModNetworking.registerServer()

        VSConfigClass.registerConfig(MOD_ID, ModConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        ModClientScreens.register()
        ModEntities.registerRenderers()
        ModNetworking.registerClient()
    }


    interface ClientRenderers {
        fun <T: BlockEntity> registerBlockEntityRenderer(t: BlockEntityType<T>, r: BlockEntityRendererProvider<T>)
    }
    @JvmStatic
    fun initClientRenderers(clientRenderers: ClientRenderers) {
        ModBlockEntities.initClientRenderers(clientRenderers)
    }
}

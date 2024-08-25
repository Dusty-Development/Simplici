package org.valkyrienskies.simplici.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import org.valkyrienskies.core.impl.config.VSConfigClass.Companion.getRegisteredConfig
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.Simplici.init
import org.valkyrienskies.simplici.Simplici.initClientRenderers
import org.valkyrienskies.simplici.content.render.ModModels
import org.valkyrienskies.simplici.registry.CreativeTabs
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod(Simplici.MOD_ID)
class SimpliciModForge {
    init {
        runForDist (
            clientTarget = {
                SimpliciModForgeClient.registerClient()
            },
            serverTarget = {}
        )
        LOADING_CONTEXT.registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory::class.java
        ) {
            ConfigScreenHandler.ConfigScreenFactory { _: Minecraft?, parent: Screen? ->
                createConfigScreenFor(
                    parent!!,
                    getRegisteredConfig(ModConfig::class.java)
                )
            }
        }

        init()

        val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Simplici.MOD_ID)
        deferredRegister.register("general") {
            CreativeTabs.create()
        }
        deferredRegister.register(getModBus())

    }


    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}

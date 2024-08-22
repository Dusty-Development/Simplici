package org.valkyrienskies.simplici.forge

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.ModelEvent
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.Simplici.initClientRenderers
import org.valkyrienskies.simplici.content.render.ModModels
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object SimpliciModForgeClient {
    private var happendClientSetup = false

    fun registerClient() {
        MOD_BUS.addListener { event: ModelEvent.BakingCompleted ->
            clientSetup( event )
        }
        MOD_BUS.addListener { event: ModelEvent.RegisterAdditional ->
            println("${Simplici.MOD_ID}: Registering models")
            ModModels.MODELS.forEach { rl ->
                println("${Simplici.MOD_ID}: Registering model $rl")
                event.register(rl)
            }
        }

        MOD_BUS.addListener { event: EntityRenderersEvent.RegisterRenderers ->
            entityRenderers(
                event
            )
        }
    }


    private fun entityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        initClientRenderers(
            object : Simplici.ClientRenderers {
                override fun <T : BlockEntity> registerBlockEntityRenderer(
                    t: BlockEntityType<T>,
                    r: BlockEntityRendererProvider<T>
                ) = event.registerBlockEntityRenderer(t, r)
            }
        )
    }

    fun clientSetup(event: ModelEvent.BakingCompleted) {
        if (happendClientSetup) { return }
        happendClientSetup = true
        Simplici.initClient()
    }
}
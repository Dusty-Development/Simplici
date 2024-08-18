package org.valkyrienskies.simplici.forge

import net.minecraftforge.client.event.ModelEvent
import org.valkyrienskies.simplici.Simplici
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object SimpliciModForgeClient {
    private var happendClientSetup = false

    fun registerClient() {
        MOD_BUS.addListener { event: ModelEvent.BakingCompleted ->
            clientSetup( event )
        }
    }

    fun clientSetup(event: ModelEvent.BakingCompleted) {
        if (happendClientSetup) { return }
        happendClientSetup = true
        Simplici.initClient()
    }
}
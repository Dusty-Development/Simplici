package org.valkyrienskies.simplici.content.ship

import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

interface IShipControlModule {
    val shipControl: ModShipControl

    fun onTick()
    fun onPhysTick(physShip:PhysShipImpl)

}
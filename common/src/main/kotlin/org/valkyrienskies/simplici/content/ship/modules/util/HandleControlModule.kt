package org.valkyrienskies.simplici.content.ship.modules.util

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.api.extension.getVelAtPos
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.simplici.content.ship.ModShipControl
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

class HandleControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    override fun onPhysTick(physShip: PhysShipImpl) {



    }

    override fun onTick() { }

    companion object {
        fun getOrCreate(ship: ServerShip): HandleControlModule {
            val control: ModShipControl = ship.getAttachment<ModShipControl>() ?: ModShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is HandleControlModule) return it }
            val module = HandleControlModule(control)
            control.loadedModules.add(module)
            return module
        }
    }
}
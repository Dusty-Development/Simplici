package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.simplici.content.ship.ModShipControl
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.mod.common.util.toJOMLD
import java.util.concurrent.ConcurrentHashMap

class ThrusterControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    val thrusters = ConcurrentHashMap<BlockPos, ThrusterForcesData>()

    override fun onPhysTick(physShip: PhysShipImpl) {

        // Loops over all thrusters and updates the max thrust of that direction
        thrusters.forEach {
            val blockPos = it.key
            val data = it.value

            val direction = data.state.getValue(BlockStateProperties.FACING)
            val forceLocal = direction.normal.toJOMLD().mul(data.throttle)
            val forceGlobal = physShip.transform.transformDirectionNoScalingFromShipToWorld(forceLocal, Vector3d())
            val pos = blockPos.center.toJOML().sub(physShip.transform.positionInShip)

            val totalForce = forceGlobal.mul(data.force, Vector3d())

            if(totalForce.isFinite) {
                physShip.applyInvariantForceToPos(totalForce, pos)
            }
        }

    }

    override fun onTick() { }

    fun addThruster(pos: BlockPos, data: ThrusterForcesData) { thrusters[pos] = data }
    fun removeThruster(pos: BlockPos) { thrusters.remove(pos) }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterControlModule {
            val control: ModShipControl = ship.getAttachment<ModShipControl>() ?: ModShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is ThrusterControlModule) return it }
            val module = ThrusterControlModule(control)
            control.loadedModules.add(module)
            return module
        }
    }
}
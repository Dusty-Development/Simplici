package org.valkyrienskies.simplici.content.ship.modules.thruster

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

class ThrusterControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    val thrusters = ConcurrentHashMap<BlockPos, ThrusterForcesData>()

    override fun onPhysTick(physShip: PhysShipImpl) {

        // Loops over all thrusters and updates the max thrust of that direction
        thrusters.forEach {
            val blockPos = it.key
            val data = it.value

            val worldBlockPos = physShip.transform.shipToWorld.transformPosition(blockPos.center.toJOML())
            val direction = data.state.getValue(BlockStateProperties.FACING)
            val forceLocal = direction.normal.toJOMLD().mul(data.throttle)
            val forceGlobal = physShip.transform.transformDirectionNoScalingFromShipToWorld(forceLocal, Vector3d())
            val pos = blockPos.center.toJOML().sub(physShip.transform.positionInShip)

            val totalForce = forceGlobal.mul(data.force * speedLimiter(physShip.getVelAtPos(worldBlockPos).dot(forceGlobal), data.softMaxSpeed, data.maxSpeed, 2.0), Vector3d())

            if(data.isFueled) println("Fuel")
            if(totalForce.isFinite && data.isFueled) physShip.applyInvariantForceToPos(totalForce.mul(if(data.isReversed) -1.0 else 1.0), pos)
        }

    }

    private fun speedLimiter(speed:Double, soft:Double, hard:Double, sharpness:Double):Double {
        if (speed <= soft) return 1.0
        if (speed >= hard) return 0.0

        // Calculate the normalized speed between soft max and hard max (0 to 1 range)
        val normalizedSpeed = (speed - soft) / (hard - soft)
        val scaleFactor = (1 - normalizedSpeed).pow(sharpness)
        return scaleFactor
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
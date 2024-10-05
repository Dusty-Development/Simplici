package org.valkyrienskies.simplici.content.ship.modules.util

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
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
import kotlin.math.atan2
import kotlin.math.sqrt

class HandleControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    val holders = ConcurrentHashMap<BlockPos, HandleForcesData>()

    override fun onPhysTick(physShip: PhysShipImpl) {
        val dt = 1.0/60.0

        holders.forEach { (blockPos, data) -> run {

            // Linear \\

            val pos = blockPos.center.toJOML().add(data.offset, Vector3d())
            val worldPos = physShip.transform.shipToWorld.transformPosition(pos, Vector3d())
            val worldBlockPos = physShip.transform.shipToWorld.transformPosition(blockPos.center.toJOML())

            data.linearPID.setPID(1.0,1.0,0.25)
            data.linearPID.updatePID(dt, worldPos, data.target)

            var forceScalar = physShip.inertia.shipMass * 10000.0
            if (data.isCreative) forceScalar = forceScalar.coerceAtMost(50000.0)
            val force = data.linearPID.mul(5 * forceScalar, Vector3d())

            physShip.applyInvariantForceToPos(force, pos.sub(physShip.transform.positionInShip))

            // Angular \\

            val shipUpVector = physShip.transform.transformDirectionNoScalingFromShipToWorld(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space

            val currentDirection = physShip.transform.transformDirectionNoScalingFromShipToWorld(data.state.getValue(FACING).normal.toJOMLD(), Vector3d()).normalize()
            val targetDirection = Vector3d(data.targetDirection).normalize()

            val currentAngle = Vector3d() //vectorToEulerAngles(currentDirection, shipUpVector)
            val targetAngle = Vector3d() //vectorToEulerAngles(targetDirection, Vector3d(0.0,1.0,0.0))

            data.angularPID.setPID(1.0,1.0,0.25)
            data.angularPID.updateAnglePID(dt, currentAngle, targetAngle)

            var torqueScalar = physShip.inertia.shipMass * 1000.0
            if (data.isCreative) torqueScalar = torqueScalar.coerceAtMost(50000.0)
            val torque = data.angularPID.mul(5 * torqueScalar, Vector3d())

            physShip.applyInvariantTorque(torque)
        }}
    }

    fun vectorToEulerAngles(direction: Vector3d, up:Vector3d): Vector3d {
        // Calculate yaw (rotation around Y axis)
        val yaw = Math.toDegrees(atan2(direction.x, direction.z))

        // Calculate pitch (rotation around X axis)
        val pitch = Math.toDegrees(atan2(-direction.y, sqrt(direction.x * direction.x + direction.z * direction.z)))

        // Calculate roll (rotation around Z-axis) using the up vector
        // Roll is the angle between the up vector and the projected up vector on the plane perpendicular to the forward direction
        val right = Vector3d(direction).cross(up).normalize() // Right vector (perpendicular to both direction and up)
        val projectedUp = Vector3d(right).cross(direction).normalize() // Projected up vector
        val roll = Math.toDegrees(atan2(up.dot(right), up.dot(projectedUp)))

        return Vector3d(pitch, yaw, roll)
    }

    override fun onTick() { }

    fun addHolder(pos: BlockPos, data: HandleForcesData) { holders[pos] = data }
    fun removeHolder(pos: BlockPos) { holders.remove(pos) }

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
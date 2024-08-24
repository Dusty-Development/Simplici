package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.VSClientGameUtils
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.physics_api.PoseVel
import org.valkyrienskies.simplici.api.math.SpringHelper
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.simplici.content.ship.SimpliciShipControl
import java.util.concurrent.ConcurrentHashMap


class WheelControlModule(override val shipControl: SimpliciShipControl) : IShipControlModule {

    private val wheels = ConcurrentHashMap<BlockPos, WheelForcesData>()

    fun addOrUpdateWheel(pos: BlockPos, wheelForcesData: WheelForcesData) {
        wheels.put(pos, wheelForcesData)
    }
    fun removeWheel(pos: BlockPos) {
        wheels.remove(pos)
    }

    override fun onTick() {
    }

    override fun onPhysTick(physShip: PhysShipImpl) {
        wheels.forEach {
            val worldBlockPos = it.key.center.toJOML().sub(physShip.transform.positionInShip)

            val suspensionForce = calculateSuspension(physShip,it.key,it.value)
            val slidingForce = calculateSliding(physShip,it.key,it.value)
            val force = Vector3d(slidingForce,suspensionForce,0.0)

            if(it.value.colliding) physShip.applyRotDependentForceToPos(force.mul(physShip.inertia.shipMass / wheels.size), worldBlockPos)
        }
    }

    fun calculateSuspension(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData):Double {
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val shipUpVector = physShip.transform.transformDirectionNoScalingFromWorldToShip(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space
        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        val localVelocity = -velocity.y() //-velocity.mul(shipUpVector, Vector3d()).length() // <---- GOIN WRONG HERE THIS IS WRONG

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        val spring = (-SpringHelper.calculateSpringForceDouble(offset, localVelocity, 25.0, 10.0))
        return spring
    }

    fun calculateSliding(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData):Double {
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val shipUpVector = physShip.transform.transformDirectionNoScalingFromWorldToShip(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space
        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        val localVelocity = -velocity.y() //-velocity.mul(shipUpVector, Vector3d()).length() // <---- GOIN WRONG HERE THIS IS WRONG

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        return 0.0
    }

    private fun pointVelocity(physShip: PhysShipImpl, worldPointPosition: Vector3dc): Vector3dc {
        val centerOfMassPos = worldPointPosition.sub(physShip.transform.positionInWorld, Vector3d())
        return physShip.poseVel.vel.add(physShip.poseVel.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): WheelControlModule {
            val control: SimpliciShipControl = ship.getAttachment<SimpliciShipControl>() ?: SimpliciShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is WheelControlModule) return it }
            val module = WheelControlModule(control)
            control.loadedModules.add(module)
            return module
        }

    }
}
package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.math.SpringHelper
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType.*
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.simplici.content.ship.SimpliciShipControl
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.absoluteValue


class WheelControlModule(override val shipControl: SimpliciShipControl) : IShipControlModule {

    private val wheels = ConcurrentHashMap<BlockPos, WheelForcesData>()
    private val engines = ConcurrentHashMap<BlockPos, EngineData>()

    fun addOrUpdateWheel(pos: BlockPos, wheelForcesData: WheelForcesData) {
        wheels.put(pos, wheelForcesData)
    }
    fun removeWheel(pos: BlockPos) {
        wheels.remove(pos)
    }

    fun addOrUpdateEngine(pos: BlockPos, engineData: EngineData) {
        engines.put(pos, engineData)
    }
    fun removeEngine(pos: BlockPos) {
        engines.remove(pos)
    }

    override fun onTick() {

    }

    override fun onPhysTick(physShip: PhysShipImpl) {
        wheels.forEach {
            calculateSuspension(physShip,it.key,it.value)
            calculateSteering(physShip,it.key,it.value)
            calculateDriving(physShip,it.key,it.value)
        }
    }

    private fun calculateSuspension(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val shipUpVector = physShip.transform.transformDirectionNoScalingFromWorldToShip(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space
        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        val localVelocity = -velocity.y() //-velocity.mul(shipUpVector, Vector3d()).length() // <---- GOIN WRONG HERE THIS IS WRONG

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        val spring = (-SpringHelper.calculateSpringForceDouble(offset, localVelocity, ModConfig.SERVER.WheelSuspensionStiffness, ModConfig.SERVER.WheelSuspensionDamping))

        val force = Vector3d(0.0, spring, 0.0).mul(physShip.inertia.shipMass / wheels.size)
        if(wheelData.colliding) if(ModConfig.SERVER.SuspensionPullsToFloor || spring > 0.0) physShip.applyInvariantForceToPos(force, wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }

    private fun calculateSteering(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val direction = wheelData.state.getValue(BlockStateProperties.FACING).clockWise.normal.toJOMLD()
        val steeringControl = (shipControl.currentControlData?.leftImpulse?.toDouble() ?: 0.0) * ModConfig.SERVER.SteeringAngle
        val targetSteeringAngle = when (wheelData.steeringType) {
            NONE -> 0.0
            CLOCKWISE -> steeringControl
            COUNTER_CLOCKWISE -> -steeringControl
            LEAN_CLOCKWISE -> -steeringControl // Lean multi goes here
            LEAN_COUNTER_CLOCKWISE -> steeringControl // Lean multi goes here
        }
        wheelData.steeringAngle = targetSteeringAngle //org.joml.Math.lerp(wheelData.steeringAngle, targetSteeringAngle, 0.75)
        val dirWithSteering = direction.rotateY(Math.toRadians(wheelData.steeringAngle), Vector3d())
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(dirWithSteering, Vector3d()).normalize()

        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        var floorVelocity = wheelData.floorVel
        if(wheelData.floorVel == null) floorVelocity = Vector3d()

        val slidingVelocity = velocity.sub(floorVelocity, Vector3d()).dot(globalDir)

        var frictionForce = ModConfig.SERVER.WheelGripForce
        if(slidingVelocity.absoluteValue >= ModConfig.SERVER.WheelSlideThreshold) frictionForce = ModConfig.SERVER.WheelSlideForce
        if(wheelData.steeringType != NONE && ModConfig.SERVER.SteeringWheelsAwaysGrippy) ModConfig.SERVER.WheelGripForce

        val force = -slidingVelocity * (frictionForce * wheelData.floorFrictionMultiplier)
        if(wheelData.colliding) physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }

    private fun calculateDriving(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val direction = shipControl.currentControlData?.seatInDirection?.normal?.toJOMLD() ?: wheelData.state.getValue(BlockStateProperties.FACING).normal.toJOMLD()
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(direction.rotateY(Math.toRadians(wheelData.steeringAngle)), Vector3d()).normalize()

        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        val forwardVelocity = velocity.dot(globalDir)

        val a = "test"

        var throttle = (shipControl.currentControlData?.forwardImpulse?.toDouble() ?: 0.0) // Will be negative for reverse
        if(throttle < 0) throttle *= 0.5 // Half speed in reverse

        val force:Double = getBestTorqueForSpeed(abs(forwardVelocity)) * throttle
        if(wheelData.colliding) physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).div(wheels.size.toDouble()), wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
        if(wheelData.colliding && throttle < 0.1 && throttle > -0.1) physShip.applyInvariantForceToPos(globalDir.mul(-forwardVelocity * ModConfig.SERVER.WheelFreespinFriction, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }


    private fun pointVelocity(physShip: PhysShipImpl, worldPointPosition: Vector3dc): Vector3dc {
        val centerOfMassPos = worldPointPosition.sub(physShip.transform.positionInWorld, Vector3d())
        return physShip.poseVel.vel.add(physShip.poseVel.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
    }

    private fun getBestTorqueForSpeed(speed:Double):Double {
        var bestTorque = 0.0
        engines.forEach {
            val torqueAtSpeed = it.value.getTorqueAtSpeed(speed)
            if(torqueAtSpeed >= bestTorque) {
                bestTorque = torqueAtSpeed
            }
        }

        return bestTorque
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
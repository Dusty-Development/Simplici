package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.extension.getVelAtPos
import org.valkyrienskies.simplici.api.math.SpringHelper
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType.*
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.simplici.content.ship.ModShipControl
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.absoluteValue


class WheelControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    private val wheels = ConcurrentHashMap<BlockPos, WheelForcesData>()
    private val engines = ConcurrentHashMap<BlockPos, EngineData>()

    fun addOrUpdateWheel(pos: BlockPos, wheelForcesData: WheelForcesData) {
        wheels[pos] = wheelForcesData
    }
    fun removeWheel(pos: BlockPos) {
        wheels.remove(pos)
    }

    fun addOrUpdateEngine(pos: BlockPos, engineData: EngineData) {
        engines[pos] = engineData
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
        val velocity = physShip.getVelAtPos(worldBlockPos) // Global velocity
        val localVelocity = -velocity.y() //-velocity.mul(shipUpVector, Vector3d()).length() // <---- GOIN WRONG HERE THIS IS WRONG

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        val spring = (-SpringHelper.calculateSpringForceDouble(offset, localVelocity, ModConfig.SERVER.WheelSuspensionStiffness, ModConfig.SERVER.WheelSuspensionDamping))

        val rollingDir = wheelData.state.getValue(BlockStateProperties.FACING).normal.toJOMLD()
        val globalRollingDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(rollingDir, Vector3d()).normalize()

        val suspensionForce = shipUpVector.mul(spring, Vector3d()).mul(physShip.inertia.shipMass / wheels.size)
        val rollingForce = -suspensionForce.dot(globalRollingDir)
        val totalForce = suspensionForce.mul(0.0, 1.0 ,0.0, Vector3d())
        //if(shipControl.isControlled) { totalForce.add(globalRollingDir.mul(rollingForce)) }
        if(wheelData.colliding) if(ModConfig.SERVER.SuspensionPullsToFloor || spring > 0.0) physShip.applyInvariantForceToPos(totalForce, wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
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
        wheelData.steeringAngle = org.joml.Math.lerp(wheelData.steeringAngle, targetSteeringAngle, 0.75)
        val dirWithSteering = direction.rotateY(Math.toRadians(wheelData.steeringAngle), Vector3d())
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(dirWithSteering, Vector3d()).normalize()

        val velocity = physShip.getVelAtPos(worldBlockPos) // Global velocity
        var floorVelocity = wheelData.floorVel
        if(wheelData.floorVel == null) floorVelocity = Vector3d()

        val slidingVelocity = velocity.sub(floorVelocity, Vector3d()).dot(globalDir)

        var frictionForce = ModConfig.SERVER.WheelGripForce
        if(slidingVelocity.absoluteValue >= ModConfig.SERVER.WheelSlideThreshold) frictionForce = ModConfig.SERVER.WheelSlideForce
        if(wheelData.steeringType != NONE && ModConfig.SERVER.SteeringWheelsAwaysGrippy) ModConfig.SERVER.WheelGripForce

        val force = -slidingVelocity * (frictionForce * wheelData.floorFrictionMultiplier)
        var forcePoint = wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip)
        if(ModConfig.SERVER.ShouldFrictionApplyAtFloor) forcePoint = wheelBlockPos.center.toJOML().sub(0.0, wheelData.floorCastDistance + wheelData.wheelRadius, 0.0).sub(physShip.transform.positionInShip)
        if(wheelData.colliding) physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)
    }

    private fun calculateDriving(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {

        //feel free to delete this constant, im just experimenting lmfao
        var forcePoint = wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip)
        if(ModConfig.SERVER.ShouldFrictionApplyAtFloor) forcePoint = wheelBlockPos.center.toJOML().sub(0.0, wheelData.floorCastDistance + wheelData.wheelRadius, 0.0).sub(physShip.transform.positionInShip)

        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val direction = shipControl.currentControlData?.seatInDirection?.normal?.toJOMLD() ?: wheelData.state.getValue(BlockStateProperties.FACING).normal.toJOMLD()
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(direction.rotateY(Math.toRadians(wheelData.steeringAngle)), Vector3d()).normalize()

        val velocity = physShip.getVelAtPos(worldBlockPos) // Global velocity
        val forwardVelocity = velocity.dot(globalDir)

        var throttle = (shipControl.currentControlData?.forwardImpulse?.toDouble() ?: 0.0) // Will be negative for reverse
        if(throttle < 0) throttle *= 0.5 // Half speed in reverse

        val force:Double = getBestTorqueForSpeed(abs(forwardVelocity)) * throttle
        if(wheelData.colliding) physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).div(wheels.size.toDouble()), forcePoint)
        if(wheelData.colliding && throttle < 0.1 && throttle > -0.1 && shipControl.isControlled) physShip.applyInvariantForceToPos(globalDir.mul(-forwardVelocity * ModConfig.SERVER.WheelFreespinFriction, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)
        if(!shipControl.isControlled && shipControl.controlSeatCount > 0) physShip.applyInvariantForceToPos(globalDir.mul(-forwardVelocity * ModConfig.SERVER.WheelLockedFriction, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)

        physShip.applyInvariantForceToPos(globalDir.mul(-forwardVelocity * 0.2, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)
    }

    private fun getBestTorqueForSpeed(speed:Double):Double {
        var bestTorque = 0.0
        engines.forEach {
            val torqueAtSpeed = it.value.getTorqueAtSpeed(speed)
            if(torqueAtSpeed >= bestTorque && it.value.isFueled) {
                bestTorque = torqueAtSpeed
            }
        }

        return bestTorque
    }

    companion object {
        fun getOrCreate(ship: ServerShip): WheelControlModule {
            val control: ModShipControl = ship.getAttachment<ModShipControl>() ?: ModShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is WheelControlModule) return it }
            val module = WheelControlModule(control)
            control.loadedModules.add(module)
            return module
        }

    }
}
package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.util.Mth.lerp
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
            if(it.value.colliding) {
                calculateSuspension(physShip, it.key, it.value)
                calculateSteering(physShip, it.key, it.value)
                calculateDriving(physShip, it.key, it.value)
            }
        }
    }

    private fun calculateSuspension(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val shipUpVector = physShip.transform.transformDirectionNoScalingFromWorldToShip(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space
        val velocity = physShip.getVelAtPos(worldBlockPos) // Global velocity
        val localVelocity = -velocity.y()

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        val spring = (-SpringHelper.calculateSpringForceDouble(offset, localVelocity, ModConfig.SERVER.WheelSuspensionStiffness, ModConfig.SERVER.WheelSuspensionDamping))

        val suspensionForce = shipUpVector.mul(spring, Vector3d()).mul(physShip.inertia.shipMass / wheels.size)
        val totalForce = suspensionForce.mul(0.0, 1.0 ,0.0, Vector3d())
        if(ModConfig.SERVER.SuspensionPullsToFloor || spring > 0.0) physShip.applyInvariantForceToPos(totalForce, wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }

    private fun calculateSteering(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        val direction = wheelData.state.getValue(BlockStateProperties.FACING).clockWise.normal.toJOMLD()
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())

        // Handle steering angle
        val steeringControl = (shipControl.currentControlData?.leftImpulse?.toDouble() ?: 0.0) * ModConfig.SERVER.SteeringAngle
        val targetSteeringAngle = when (wheelData.steeringType) {
            CLOCKWISE -> steeringControl
            COUNTER_CLOCKWISE -> -steeringControl
            else -> 0.0
        }
        // TODO: if steering set to SHOPPING_CART then the steering angle should lerp to face vel
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

        physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)
    }

    private fun calculateDriving(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        val direction = shipControl.currentControlData?.seatInDirection?.normal?.toJOMLD() ?: wheelData.state.getValue(BlockStateProperties.FACING).normal.toJOMLD()
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(direction.rotateY(Math.toRadians(wheelData.steeringAngle)), Vector3d()).normalize()

        val forwardVelocity = getFloorVelocity(physShip, wheelBlockPos, wheelData).dot(globalDir)

        var throttle = (shipControl.currentControlData?.forwardImpulse?.toDouble() ?: 0.0) // Will be negative for reverse
        if(throttle < 0) throttle *= 0.5 // Half speed in reverse

        // Decide to apply forces at the block or the floor
        var forcePoint = wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip)
        if(ModConfig.SERVER.ShouldFrictionApplyAtFloor) forcePoint = wheelBlockPos.center.toJOML().sub(0.0, wheelData.floorCastDistance + wheelData.wheelRadius, 0.0).sub(physShip.transform.positionInShip)

        // Driving force when pressing W or S
        val throttleForce:Double = getTotalTorqueAtSpeed(abs(forwardVelocity)) * throttle
        physShip.applyInvariantForceToPos(globalDir.mul(throttleForce, Vector3d()).div(wheels.size.toDouble()), forcePoint)

        // Friction force for all wheels (so they don't just roll forever)
        // Multiply by either the locked or free spin friction
        val frictionMultiplier =
        if(!shipControl.isControlled && shipControl.controlSeatCount > 0) { ModConfig.SERVER.WheelLockedFriction }
        else { ModConfig.SERVER.WheelFreespinFriction }

        val frictionForce = getFrictionInDir(physShip, wheelBlockPos, wheelData, globalDir, frictionMultiplier)
        physShip.applyInvariantForceToPos(globalDir.mul(frictionForce, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), forcePoint)
    }

    private fun getFrictionInDir(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData, globalDirection: Vector3d, multi:Double) : Double {
        val slidingVelocity = getFloorVelocity(physShip, wheelBlockPos, wheelData).dot(globalDirection)

        // We decide to use either "grip" or "slide" friction based on the threshold
        var frictionForce = ModConfig.SERVER.WheelGripForce
        if(slidingVelocity.absoluteValue >= ModConfig.SERVER.WheelSlideThreshold) frictionForce = ModConfig.SERVER.WheelSlideForce
        if(wheelData.steeringType != NONE && ModConfig.SERVER.SteeringWheelsAwaysGrippy) ModConfig.SERVER.WheelGripForce

        return -slidingVelocity * (frictionForce * multi)
    }

    // Gets the velocity relative to the floor
    private fun getFloorVelocity(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) : Vector3d {
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val velocity = physShip.getVelAtPos(worldBlockPos) // Global velocity
        var floorVelocity = wheelData.floorVel
        if(wheelData.floorVel == null) floorVelocity = Vector3d()

        return velocity.sub(floorVelocity, Vector3d())
    }

    private fun getTotalTorqueAtSpeed(speed:Double):Double {
        var totalTorque = 0.0
        engines.forEach {
            val torqueAtSpeed = it.value.getTorqueAtSpeed(speed)
            totalTorque += torqueAtSpeed
        }

        return totalTorque
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
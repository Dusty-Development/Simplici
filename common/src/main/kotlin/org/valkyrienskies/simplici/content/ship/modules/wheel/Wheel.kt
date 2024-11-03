package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType
import org.valkyrienskies.simplici.content.gamerule.ModGamerules
import java.lang.Math.pow
import kotlin.math.pow
import kotlin.math.sqrt

class Wheel {

    var isMarkedForDeletion = false

    // Control
    var steeringAngle = 0.0
    var steeringType = WheelSteeringType.NONE
    var throttle = 0.0
    var brake = 0.0

    // Suspension
    var suspensionMaximumDistance = 0.0
    var suspensionRestDistance = 0.0
    var suspensionStiffness = 40.0
    var suspensionDamping = 3.0

    // ParentBody (the rigid body the wheel is on)
    var attachmentPoint = Vector3d() // Position in global space
    var attachmentForward = Vector3d(1.0,0.0,0.0) // In global space
    var attachmentRight = Vector3d(0.0,0.0,1.0) // In global space
    var attachmentUp = Vector3d(0.0,1.0,0.0) // In global space
    var attachmentMass = 0.0

    // Floor
    var floorFriction = 1.0 // Current friction Coefficient of the floor
    var floorVelocity = Vector3d() // Global
    var floorNormal = Vector3d(0.0,1.0,0.0) // The floors normal in global space

    // Wheel
    var wheelRadius = 0.5
    var wheelFrictionRollingSlow = 0.25 // Rolling friction Coefficient of the floor
    var wheelFrictionStatic = 1.0 // Static friction Coefficient of the floor
    var wheelFrictionDynamic = 0.7 // Dynamic friction Coefficient of the floor
    var wheelLocalVelocity = Vector3d()
    var wheelGlobalVelocity = Vector3d()
    var wheelCurrentHeight = 0.0 // Offset from suspension base
    var isGrounded = true

    // World
    var blockPos: BlockPos = BlockPos.ZERO
    var blockState:BlockState? = null
    var blockLevel: Level? = null

    // Forces
    var wheelForce = Vector3d()

    // This is time independent! you should update the wheel before reading values from it but do not expect this to do much of anything!
    fun updateForces() {
        wheelForce = Vector3d()
        if(isMarkedForDeletion) return

        updateWheelCollision()
        calculateSuspensionForces()
        calculateSlidingForces()
        calculateRollingForces()
    }

    private fun updateWheelCollision() {
        val level = blockLevel
        val blockState = blockState
        if(level == null || blockState == null) return

        val gameRules = level.gameRules
        val castResolution = gameRules.getInt(ModGamerules.WHEEL_CAST_RESOLUTION)

        val attachmentShip = level.getShipObjectManagingPos(blockPos)
        val collidingShips = HashMap<Ship, Int> () // Every ship the wheel is colliding with, and the amount of times it collided

        for (i in -castResolution..castResolution) {

            // Define some needed vars
            val blockCenterPos = blockPos.center.toJOML()
            val biasDirection = Direction.DOWN.normal.toJOMLD()

            // Find the offset to the bottom in a way that makes it spherical
            val wheelSphericalDistance:Double = (sqrt(1 - (((castOffsetForIndex/wheelRadius).pow(2)))) * wheelRadius)// + wheelRadius <-- TRY THIS TO SEE IF MIN HEIGHT IS OK

            // Starting Position
            val startPosShip = blockCenterPos.add(forwardOffset, Vector3d())
            startPosShip.add(suspensionDirection.mul(wheelSphericalDistance, Vector3d())) // Add the wheel radius distance
            val startPos = attachmentShip?.shipToWorld?.transformPosition(startPosShip, Vector3d()) ?: startPosShip

            // Ending Position
            val endPosShip = Vector3d(startPosShip)
            endPosShip.add(suspensionDirection.mul(suspensionMaximumDistance, Vector3d()))
            val endPos = attachmentShip?.shipToWorld?.transformPosition(endPosShip, Vector3d()) ?: endPosShip

            // Ray-cast for Collisions
        }
    }

    // returns a angle that the ray should be at WITH a bias
    private fun calculateCollisionRayAngle(index: Int, min: Int, max: Int, bias: Double, biasAngle: Double): Double {
        val totalRays = max - min + 1
        val normalizedIndex = (index - min).toDouble() / totalRays.toDouble()

        // Control the density around the bias angle
        val biasedFactor = pow(normalizedIndex, bias)

        // Map biasedFactor to an angle around biasAngle
        val angle = biasAngle + (biasedFactor - 0.5) * 360.0

        return angle % 360 // Keep within 0 - 360 range
    }

    private fun calculateSuspensionForces() {
        val force = Vector3d()

        wheelForce.add(force)
    }

    private fun calculateSlidingForces() {

    }

    private fun calculateRollingForces() {

    }

    fun getRollingResistanceFrictionCoefficient(speed:Double): Double {
        return 0.25 // TODO: Scale with speed according to https://devforum.roblox.com/t/raycast-vehicle-friction/1239471/2
    }
    fun getSlidingResistanceFrictionCoefficient(force:Double): Double {
        return wheelFrictionDynamic // TODO: Change this to take in the inertia of a car and use that to calculate friction
    }
}
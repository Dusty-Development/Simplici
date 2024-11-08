package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType
import org.valkyrienskies.simplici.content.gamerule.ModGamerules
import java.lang.Math.pow
import kotlin.math.pow

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
    var attachmentPoint = Vector3d() // Position in global space (center of block)
    var attachmentForward = Vector3d(1.0,0.0,0.0) // In global space
    var attachmentRight = Vector3d(0.0,0.0,1.0) // In global space
    var attachmentUp = Vector3d(0.0,1.0,0.0) // In global space
    var attachmentMass = 0.0

    // Floor
    var floorFriction = 1.0 // Current friction Coefficient of the floor
    var floorVelocity = Vector3d() // Global
    var floorNormal = Vector3d(0.0,1.0,0.0) // The floors normal in global space <- used to apply suspension force

    // Wheel
    var wheelRadius = 0.5
    var wheelLocalUp = Vector3d() // Wheel forward local to the ship
    var wheelLocalForward = Vector3d() // Wheel forward local to the ship
    var wheelLocalRight = Vector3d() // Wheel out local to the ship
    var wheelFrictionRollingSlow = 0.25 // Rolling friction Coefficient of the floor
    var wheelFrictionStatic = 1.0 // Static friction Coefficient of the floor
    var wheelFrictionDynamic = 0.7 // Dynamic friction Coefficient of the floor
    var wheelGlobalVelocity = Vector3d()
    var wheelSuspensionVelocity = 0.0 // The speed the wheel is traveling on suspension
    var wheelCurrentOffset = 0.0 // Offset from suspension base (center of block)
    var isGrounded = true

    // World
    var blockPos: BlockPos? = null
    var blockShip: Ship? = null
    var blockState:BlockState? = null
    var blockLevel: Level? = null

    // Forces
    var wheelForce = Vector3d()

    // This is time independent! you should update the wheel before reading values from it but do not expect this to do much of anything!
    fun updateForces() {
        wheelForce = Vector3d()
        if(isMarkedForDeletion) return

        updateData()
        updateWheelCollision()
        constrainWheelToBounds()
        calculateSuspensionForces()
        calculateSlidingForces()
        calculateRollingForces()
    }

    private fun constrainWheelToBounds() {
        if(wheelCurrentOffset > suspensionMaximumDistance) wheelCurrentOffset = suspensionMaximumDistance
        if(wheelCurrentOffset < 0.0) wheelCurrentOffset = 0.0
    }

    private fun updateData() {
        val level = blockLevel
        val blockState = blockState
        val blockPos = blockPos
        if(level == null || blockState == null || blockPos == null) return

        blockShip = level.getShipObjectManagingPos(blockPos)

        // Get local vectors
        wheelLocalUp = Direction.UP.normal.toJOMLD()
        wheelLocalForward = blockState.getValue(FACING).normal.toJOMLD()
        wheelLocalRight = wheelLocalForward.rotateAxis(Math.toRadians(90.0), wheelLocalUp.x, wheelLocalUp.y, wheelLocalUp.z, Vector3d())

        // Apply steering
        wheelLocalForward.rotateAxis(Math.toRadians(steeringAngle), wheelLocalUp.x, wheelLocalUp.y, wheelLocalUp.z)
        wheelLocalRight.rotateAxis(Math.toRadians(steeringAngle), wheelLocalUp.x, wheelLocalUp.y, wheelLocalUp.z)

        // Translate to world equivalents
        attachmentUp = blockShip?.transform?.transformDirectionNoScalingFromShipToWorld(wheelLocalUp, Vector3d())!!
        attachmentForward = blockShip?.transform?.transformDirectionNoScalingFromShipToWorld(wheelLocalForward, Vector3d())!!
        attachmentRight = blockShip?.transform?.transformDirectionNoScalingFromShipToWorld(wheelLocalRight, Vector3d())!!

    }

    private fun updateWheelCollision() {
        val level = blockLevel
        val blockState = blockState
        val blockPos = blockPos
        if(level == null || blockState == null || blockPos == null) return

        val gameRules = level.gameRules
        val castResolution = gameRules.getInt(ModGamerules.WHEEL_CAST_RESOLUTION)

        val attachmentShip = level.getShipObjectManagingPos(blockPos)

        // Used to snap wheel to floor
        val closestDistance = suspensionMaximumDistance

        // Used to calculate floor vars after casts are done
        var collisionCount = 0
        var accumulatedFloorFriction = 0.0
        val accumulatedFloorNormal = Vector3d()
        val accumulatedFloorVelocity = Vector3d()

        // Perform collision checks
        for (i in -castResolution..castResolution) {

            // Define some needed vars
            val blockCenterPos = blockPos.center.toJOML()

            // Calculate Ray angle
            val totalAngleDegrees = calculateCollisionRayAngle(i, -castResolution, castResolution)
            val rotatedSuspensionDirection = wheelLocalUp.rotateAxis(Math.toRadians(totalAngleDegrees), wheelLocalRight.x, wheelLocalRight.y, wheelLocalRight.z, Vector3d())

            // Ray positions
            val localStartPos = blockCenterPos.add(wheelLocalUp.mul(wheelCurrentOffset, Vector3d()))
            val localEndPos = localStartPos.add(rotatedSuspensionDirection.mul(wheelRadius), Vector3d())

            val worldStartPos = attachmentShip?.shipToWorld?.transformPosition(localStartPos, Vector3d()) ?: localStartPos
            val worldEndPos = attachmentShip?.shipToWorld?.transformPosition(localEndPos, Vector3d()) ?: localEndPos

            // Perform Ray-cast and add any ships to colliding ships
            val castResult = wheelCollisionCast(worldStartPos, worldEndPos, attachmentShip?.id, level)

            if (castResult != null) {
                // Get Ray-cast data
                val hitBlockPos = castResult.blockPos
                val hitPos = castResult.location.toJOML()
                val hitDir = castResult.direction.normal.toJOMLD()

                // Translate from ship to world
                val hitShip = level.getShipObjectManagingPos(hitBlockPos)
                val worldHit = hitShip?.shipToWorld?.transformPosition(hitPos) ?: hitPos
                val worldDir = hitShip?.transform?.transformDirectionNoScalingFromShipToWorld(hitDir, Vector3d()) ?: hitDir

                // This could be used to add rolling off of slabs and such ig
//                val globalNormal = worldStartPos.sub(worldHit, Vector3d()).normalize()

                accumulatedFloorFriction += (1.0 - (level.getBlockState(hitBlockPos).block.friction - 0.6f))
                accumulatedFloorNormal.add(worldDir)

                if(hitShip != null) accumulatedFloorVelocity.add(pointVelocity(hitShip, worldHit))

                isGrounded = true
                collisionCount++
            }
        }

        println(collisionCount)

        floorFriction = accumulatedFloorFriction / collisionCount
        floorNormal = accumulatedFloorNormal.normalize() // TODO: This might be wrong try dividing it like the other two?
        floorVelocity = accumulatedFloorVelocity.div(collisionCount.toDouble(), Vector3d())


        if(wheelCurrentOffset > closestDistance) wheelCurrentOffset = closestDistance
    }

    private fun wheelCollisionCast(start:Vector3d, end:Vector3d, shipId: ShipId?, level: Level):BlockHitResult? {
        val clipContext = ClipContext(start.toMinecraft(), end.toMinecraft(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
        val clipResult = level.clipIncludeShips(clipContext, false, shipId)

        if (clipResult.type == HitResult.Type.BLOCK) return clipResult
        return null
    }

    private fun calculateCollisionRayAngle(index: Int, min: Int, max: Int): Double {
        val remap = (index.toDouble() - min.toDouble()) * ((90.0) - (-90.0)) / (max.toDouble() - min.toDouble()) + (-90.0)
        return remap
    }

    // returns a angle that the ray should be at WITH a bias
    private fun calculateCollisionRayAngleWithBias(index: Int, min: Int, max: Int, bias: Double, biasAngle: Double): Double {
        val totalRays = max - min + 1
        val normalizedIndex = (index - min).toDouble() / totalRays.toDouble()

        // Control the density around the bias angle
        val biasedFactor = normalizedIndex.pow(bias)

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

    private fun pointVelocity(physShip: Ship, worldPointPosition: Vector3dc): Vector3dc {
        val centerOfMassPos = worldPointPosition.sub(physShip.transform.positionInWorld, Vector3d())
        return physShip.velocity.add(physShip.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
    }
}
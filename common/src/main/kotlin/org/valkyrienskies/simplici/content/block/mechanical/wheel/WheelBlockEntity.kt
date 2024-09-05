package org.valkyrienskies.simplici.content.block.mechanical.wheel

import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType.*
import org.valkyrienskies.simplici.content.ship.modules.wheel.WheelControlModule
import org.valkyrienskies.simplici.content.ship.modules.wheel.WheelForcesData
import kotlin.math.pow
import kotlin.math.sqrt


abstract class WheelBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(blockEntityType, pos, state)
{

    abstract val wheelRadius: Double
    abstract val wheelRestHeight: Double // From center of block to center of wheel in rest
    abstract val wheelDistanceLimit: Double

    var steeringAngle = 0.0
    var drivingAngle = 0.0
    var lastDist = 0.0
    var currentDist = 0.0

    var steeringType: WheelSteeringType = NONE
    var wheelData: WheelForcesData = WheelForcesData()

    fun generateWheelForcesData(): WheelForcesData {
        lastDist = currentDist

        steeringAngle = wheelData.steeringAngle

        if(level?.isClientSide == false) {
            // Send packet
            for (player in level?.players()!!) {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeBlockPos(blockPos);
                buf.writeDouble(steeringAngle)
                buf.writeDouble(0.0)
//                NetworkManager.sendToPlayer(player as ServerPlayer?, ModNetworking.WHEEL_DATA_S2C, buf)
            }
        }

        wheelData = WheelForcesData()

        wheelData.state = blockState
        wheelData.wheelLocalDirection = blockState.getValue(FACING)
        wheelData.wheelRadius = wheelRadius
        wheelData.steeringType = steeringType
        wheelData.restDistance = wheelRestHeight
        wheelData.floorCastDistance = wheelRestHeight
        wheelData.floorFrictionMultiplier = 1.0
        wheelData.floorVel = null
        wheelData.floorBlockPos = null
        wheelData.colliding = false

        if (level == null) return wheelData

        val ship = level.getShipObjectManagingPos(blockPos)

        for (i in -ModConfig.SERVER.WheelCastsResolution..ModConfig.SERVER.WheelCastsResolution) {

            val offset = (i.toDouble()/ModConfig.SERVER.WheelCastsResolution) * wheelRadius
            val startPosShip = blockPos.toJOMLD().add(0.5, 0.5, 0.5).add(blockState.getValue(FACING).normal.toJOMLD().mul(offset))

            val wheelBottomDistance:Double = sqrt(1 - (((offset/wheelRadius).pow(2)))) * wheelRadius
            val endPosShip = startPosShip.add(Vector3d(0.0, -(wheelDistanceLimit + wheelBottomDistance), 0.0), Vector3d())

            val startPos = ship?.shipToWorld?.transformPosition(startPosShip, Vector3d()) ?: startPosShip
            val endPos = ship?.shipToWorld?.transformPosition(endPosShip, Vector3d()) ?: endPosShip

            val clipContext = ClipContext(
                startPos.toMinecraft(),
                endPos.toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            )
            var clipResult = level!!.clipIncludeShips(clipContext, false)
            if (ship != null) clipResult = level!!.clipIncludeShips(clipContext, false, ship.id)

            if (clipResult.type == HitResult.Type.BLOCK) {
                val hitShip = level.getShipObjectManagingPos(clipResult.blockPos)
                val worldHit = hitShip?.shipToWorld?.transformPosition(clipResult.location.toJOML()) ?: clipResult.location.toJOML()

                val castDistance = startPos.distance(worldHit) - wheelBottomDistance
                if(wheelData.floorCastDistance > castDistance || !wheelData.colliding) wheelData.floorCastDistance = castDistance
                wheelData.colliding = true

                if(hitShip != null) {
                    val floorVelocity = pointVelocity(hitShip, worldHit)
                    wheelData.floorVel = Vector3d(floorVelocity) // Sets the id of the floor to the ship
                    wheelData.floorBlockPos = clipResult.blockPos
                    wheelData.floorFrictionMultiplier = level!!.getBlockState(clipResult.blockPos).block.friction.toDouble()
                }
            }
        }

        currentDist = wheelData.floorCastDistance
        return wheelData
    }

    fun pointVelocity(physShip: Ship, worldPointPosition: Vector3dc): Vector3dc {
        val centerOfMassPos = worldPointPosition.sub(physShip.transform.positionInWorld, Vector3d())
        return physShip.velocity.add(physShip.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
    }

    // EVENTS \\

    open fun tick() {
        val data = generateWheelForcesData()

        if (level!!.isClientSide) return

        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) WheelControlModule.getOrCreate(constrainedShip).addOrUpdateWheel(blockPos, data)


    }

    open fun onPlaced() { } // Dont worry about it. forces should be assigned first tick

    open fun onRemoved() {
        if (level!!.isClientSide) return
        val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(blockPos)
        if (constrainedShip != null) WheelControlModule.getOrCreate(constrainedShip).removeWheel(blockPos)
    }

    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) {
        steeringType = when (steeringType) {
            NONE -> CLOCKWISE
            CLOCKWISE -> COUNTER_CLOCKWISE
            COUNTER_CLOCKWISE -> LEAN_CLOCKWISE
            LEAN_CLOCKWISE -> LEAN_COUNTER_CLOCKWISE
            LEAN_COUNTER_CLOCKWISE -> NONE
        }
        if(level?.isClientSide == false) player.sendSystemMessage(Component.literal("Changed steering type to: $steeringType"))
    }


    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        super.load(tag)
        steeringType = valueOf(tag.getString("steering_type"))
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putString("steering_type", steeringType.toString())
        super.saveAdditional(tag)
    }
}
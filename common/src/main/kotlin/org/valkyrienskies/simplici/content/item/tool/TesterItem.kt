package org.valkyrienskies.simplici.content.item.tool

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSSphericalTwistLimitsConstraint
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.putVector3d
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalBlockHelper
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity.Companion.halfLength
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class TesterItem : Item(
    Properties().stacksTo(1)
){


    override fun useOn(context: UseOnContext): InteractionResult {

        val player = context.player
        val level = context.level
        val item = context.itemInHand
        val tag = item.orCreateTag
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation.toJOML()


        if(level.isClientSide || player == null) { return InteractionResult.PASS }
        if(level !is ServerLevel) { return InteractionResult.PASS }

        if(player.isShiftKeyDown) {
            // Reset params
            clearNBT(tag)
            player.sendSystemMessage(Component.literal("Cleared stored value"))
            return super.useOn(context)
        }

        if (tag.getBoolean(HasPosNBTString)) {

            // Create rope
            val blockPos = BlockPos.of(tag.getLong(BlockNBTString))
            val pos = Vector3d(tag.getDouble("${PosNBTString}x"), tag.getDouble("${PosNBTString}y"), tag.getDouble("${PosNBTString}z"))

            val firstShip = level.getShipObjectManagingPos(blockPos)
            val firstShipId: ShipId = firstShip?.id ?: (level).shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!.toLong()

            val secondShip = level.getShipObjectManagingPos(blockPosition)
            val secondShipId: ShipId = secondShip?.id ?: (level).shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!.toLong()

            val startPos = firstShip?.shipToWorld?.transformPosition(pos, Vector3d()) ?: pos
            val endPos = secondShip?.shipToWorld?.transformPosition(blockLocation, Vector3d()) ?: blockLocation

            val direction = endPos.sub(startPos, Vector3d()).normalize()
            val Orrientation = MechanicalBlockHelper.getRotationQuaternionFromDirection(Direction.getNearest(direction.x, direction.y, direction.z)).mul(Quaterniond( AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0) ), Quaterniond()).normalize()

            println("s: $startPos, e:$endPos")

            val rope = RopeSegmentEntity.createRope(level, startPos, endPos, false)

            // Attach constraint
            val startConstraint = VSAttachmentConstraint(
                firstShipId,
                rope.first.getID()!!,
                1e-10,
                pos,
                Vector3d(-halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(startConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(firstShipId, rope.first.getID()!!)

            val startTwistConstraint = VSSphericalTwistLimitsConstraint(
                firstShipId,
                rope.first.getID()!!,
                1e-10,
                Orrientation,
                Quaterniond(),
                1e150,
                -Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble()),
                Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble())
            )
            level.shipObjectWorld.createNewConstraint(startTwistConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(firstShipId, rope.first.getID()!!)


            val endTwistConstraint = VSSphericalTwistLimitsConstraint(
                secondShipId,
                rope.second.getID()!!,
                1e-10,
                Orrientation,
                Quaterniond(),
                1e150,
                -Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble()),
                Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble())
            )
            level.shipObjectWorld.createNewConstraint(endTwistConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(secondShipId, rope.second.getID()!!)

            val endConstraint = VSAttachmentConstraint(
                secondShipId,
                rope.second.getID()!!,
                1e-10,
                blockLocation,
                Vector3d(halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(endConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(secondShipId, rope.second.getID()!!)

            clearNBT(tag)
            player.sendSystemMessage(Component.literal("Created rope from ($pos) to ($blockLocation)"))

        } else {
            // Set first pos
            tag.putBoolean(HasPosNBTString, true)
            tag.putDouble("${PosNBTString}x", blockLocation.x)
            tag.putDouble("${PosNBTString}y", blockLocation.y)
            tag.putDouble("${PosNBTString}z", blockLocation.z)
            tag.putLong(BlockNBTString, blockPosition.asLong())
            player.sendSystemMessage(Component.literal("Started rope at ($blockLocation)"))
        }


        return super.useOn(context)
    }

    fun clearNBT(tag: CompoundTag) {
        tag.putBoolean(HasPosNBTString, false)
        tag.putVector3d(PosNBTString, Vector3d())
        tag.putLong(BlockNBTString, 0)
    }

    companion object {
        const val HasPosNBTString = "HasFirstPos"
        const val PosNBTString = "FirstPos"
        const val BlockNBTString = "BlockPos"
    }

}
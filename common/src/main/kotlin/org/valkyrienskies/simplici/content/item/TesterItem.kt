package org.valkyrienskies.simplici.content.item

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.putVector3d
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity.Companion.halfLength

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

            println("s: $startPos, e:$endPos")

            val rope = RopeSegmentEntity.createRope(level, startPos, endPos, false)

            // Attach constraint
            val startConstraint = VSAttachmentConstraint(
                firstShipId,
                rope.first.shipId!!,
                1e-10,
                pos,
                Vector3d(-halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(startConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(firstShipId, rope.first.shipId!!)

            val endConstraint = VSAttachmentConstraint(
                secondShipId,
                rope.second.shipId!!,
                1e-10,
                blockLocation,
                Vector3d(halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(endConstraint)
            level.shipObjectWorld.disableCollisionBetweenBodies(secondShipId, rope.second.shipId!!)

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
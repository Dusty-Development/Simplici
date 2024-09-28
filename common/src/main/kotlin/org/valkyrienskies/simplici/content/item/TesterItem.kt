package org.valkyrienskies.simplici.content.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.putVector3d
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity

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
            val pos = Vector3d(tag.getDouble("${PosNBTString}x"), tag.getDouble("${PosNBTString}y"), tag.getDouble("${PosNBTString}z"))

            RopeSegmentEntity.createRope(level, pos, blockLocation, true)

            clearNBT(tag)
            player.sendSystemMessage(Component.literal("Created rope from ($pos) to ($blockLocation)"))

        } else {
            // Set first pos
            tag.putBoolean(HasPosNBTString, true)
            tag.putDouble("${PosNBTString}x", blockLocation.x)
            tag.putDouble("${PosNBTString}y", blockLocation.y)
            tag.putDouble("${PosNBTString}z", blockLocation.z)
            player.sendSystemMessage(Component.literal("Started rope at ($blockLocation)"))
        }


        return super.useOn(context)
    }

    fun clearNBT(tag: CompoundTag) {
        tag.putBoolean(HasPosNBTString, false)
        tag.putVector3d(PosNBTString, Vector3d())
    }

    companion object {
        const val HasPosNBTString = "HasFirstPos"
        const val PosNBTString = "FirstPos"
    }

}
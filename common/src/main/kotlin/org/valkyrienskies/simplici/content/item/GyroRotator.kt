package org.valkyrienskies.simplici.content.item

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML

class GyroRotator : Item(
    Properties().stacksTo(1)
){

    private var pulseForce : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation.toJOML()

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

//        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS
//        val control = GyroControlModule.getOrCreate(ship)
//
//        if (player.isShiftKeyDown) {
//            control.targetDirection = control.targetDirection.rotateY(Math.toRadians(45.0), Quaterniond())
//        } else {
//            control.targetDirection = control.targetDirection.rotateY(Math.toRadians(-45.0), Quaterniond())
//        }

        return super.useOn(context)
    }
}
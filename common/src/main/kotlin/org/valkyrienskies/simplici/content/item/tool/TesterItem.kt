package org.valkyrienskies.simplici.content.item.tool

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
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
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.mod.util.putVector3d
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalBlockHelper
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity.Companion.halfLength
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class TesterItem : Item(
    Properties().stacksTo(1)
){


    override fun useOn(context: UseOnContext): InteractionResult {



        return super.useOn(context)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {

        if(level.isClientSide) { super.use(level, player, interactionHand) }
        if(level !is ServerLevel) { super.use(level, player, interactionHand) }

        val clipContext = ClipContext(
            player.eyePosition,
            player.eyePosition.add(player.forward.multiply(Vec3(0.5,0.5,0.5))),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            null
        )
        var clipResult = level.clipIncludeShips(clipContext, false)

        println()
        println(clipResult.location)
        println(clipResult.direction.normal)

        return super.use(level, player, interactionHand)
    }

    companion object {

    }

}
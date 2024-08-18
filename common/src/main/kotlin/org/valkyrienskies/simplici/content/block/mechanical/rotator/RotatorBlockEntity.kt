package org.valkyrienskies.simplici.content.block.mechanical.rotator

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.GrindstoneBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.extension.snapToGrid
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeConstraintBlockEntity
import org.valkyrienskies.simplici.content.ship.modules.motor.RotatorControlModule
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.physics_api.ConstraintId
import java.awt.TextComponent


// THIS WAS ALL TAKEN FROM BUGGY REWRITE WHEN AVAILABLE

class RotatorBlockEntity(pos: BlockPos, state: BlockState)
    : HingeConstraintBlockEntity(ModBlockEntities.ROTATOR.get(), pos, state)
{
    private var isFlipped = false

    override fun load(tag: CompoundTag) {
        super.load(tag)
        isFlipped = tag.getBoolean("flippedDirection")
    }

    override fun saveAdditional(tag: CompoundTag) {
        if (level!!.isClientSide() && isConstrained) return
        tag.putBoolean("flippedDirection", isFlipped)
        super.saveAdditional(tag)
    }

    override fun breakConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = false

        if (constrainedBlockPos != null) {
            (level as ServerLevel).getShipObjectManagingPos(constrainedBlockPos!!)?.let { RotatorControlModule.getOrCreate(it).removeSpinner(constrainedBlockPos!!) }
        }
        (level as ServerLevel).getShipObjectManagingPos(blockPos) ?.let { RotatorControlModule.getOrCreate(it).removeSpinner(blockPos) }

        super.breakConstraints()
    }

    override fun onUse(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult ) {
        if(player.isHolding(Items.LEVER) && !level.isClientSide() && hand == InteractionHand.MAIN_HAND) isFlipped = isFlipped.not() else super.onUse(state, level, pos, player, hand, hit)
    }

    override fun tick() {
        if (constrainedBlockPos == null) return

        if (isLoading && level?.isLoaded(constrainedBlockPos!!) == true) {
            loadConstraints()
            return
        }

        if(!level?.isClientSide!!) {
            val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(constrainedBlockPos!!)
            if (constrainedShip != null) RotatorControlModule.getOrCreate(constrainedShip).addSpinner(constrainedBlockPos!!, blockState, isFlipped)

            if(ModConfig.SERVER.NEWTONIAN_MOTORS) {
                val ship = (level as ServerLevel).getShipObjectManagingPos(blockPos)
                if (ship != null) RotatorControlModule.getOrCreate(ship).addSpinner(blockPos, blockState, !isFlipped)
            }
        }

        if (level!!.getBlockState(constrainedBlockPos!!).isAir && ModConfig.SERVER.REJECT_FLOATING_HINGES) breakConstraints()

    }
}
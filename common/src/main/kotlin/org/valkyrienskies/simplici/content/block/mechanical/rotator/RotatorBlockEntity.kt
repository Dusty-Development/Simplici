package org.valkyrienskies.simplici.content.block.mechanical.rotator

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.ship.modules.motor.RotatorControlModule
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalConstraintBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeHelper


// THIS WAS ALL TAKEN FROM BUGGY REWRITE WHEN AVAILABLE

class RotatorBlockEntity(pos: BlockPos, state: BlockState) : MechanicalConstraintBlockEntity(ModBlockEntities.ROTATOR.get(), pos, state)
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

    override fun createConstraints(shipId: ShipId, constrainedShipId: ShipId, compliance: Double, maxForce: Double) {
        // The facing rotations
        val facing = blockState.getValue(FACING)
        val headFacing = level!!.getBlockState(mechanicalHeadBlockPos!!).getValue(FACING)

        val hingeOrientation = HingeHelper.getRotationQuaternionFromDirection(facing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()
        val headOrientation = HingeHelper.getRotationQuaternionFromDirection(headFacing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()

        // Hinge constraint
        val hingeConstraint = VSHingeOrientationConstraint(
            shipId,
            constrainedShipId,
            compliance,
            hingeOrientation,
            headOrientation,
            maxForce
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(hingeConstraint)?.let { constraints.add(it) }

        // Attach constraint
        val attachConstraint = VSAttachmentConstraint(
            shipId,
            constrainedShipId,
            compliance,
            blockPos.toJOMLD().add(0.5,0.5,0.5),
            mechanicalHeadBlockPos!!.toJOMLD().add(0.5,0.5,0.5),
            maxForce,
            0.0
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(attachConstraint)?.let { constraints.add(it) }
    }

    override fun breakConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = false

        if (mechanicalHeadBlockPos != null) {
            (level as ServerLevel).getShipObjectManagingPos(mechanicalHeadBlockPos!!)?.let { RotatorControlModule.getOrCreate(it).removeSpinner(mechanicalHeadBlockPos!!) }
        }
        (level as ServerLevel).getShipObjectManagingPos(blockPos) ?.let { RotatorControlModule.getOrCreate(it).removeSpinner(blockPos) }

        super.breakConstraints()
    }

    override fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult ) {
        if(player.isHolding(Items.LEVER) && !level!!.isClientSide() && hand == InteractionHand.MAIN_HAND) isFlipped = isFlipped.not() else super.onUse(player, hand, hit)
    }

    override fun tick() {
        super.tick()

        if(!level?.isClientSide!!) {
            val constrainedShip = (level as ServerLevel).getShipObjectManagingPos(mechanicalHeadBlockPos!!)
            if (constrainedShip != null) RotatorControlModule.getOrCreate(constrainedShip).addSpinner(mechanicalHeadBlockPos!!, blockState, isFlipped)

            if(ModConfig.SERVER.NewtonianMotors) {
                val ship = (level as ServerLevel).getShipObjectManagingPos(blockPos)
                if (ship != null) RotatorControlModule.getOrCreate(ship).addSpinner(blockPos, blockState, !isFlipped)
            }
        }
    }
}
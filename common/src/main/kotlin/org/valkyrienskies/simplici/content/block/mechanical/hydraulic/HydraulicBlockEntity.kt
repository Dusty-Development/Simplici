package org.valkyrienskies.simplici.content.block.mechanical.hydraulic

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import org.joml.AxisAngle4d
import org.joml.Math.lerp
import org.joml.Quaterniond
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint
import org.valkyrienskies.core.apigame.constraints.VSSlideConstraint
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalBlockHelper
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalConstraintBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.head.MechanicalHeadBlockEntity

class HydraulicBlockEntity(pos: BlockPos, state: BlockState) : MechanicalConstraintBlockEntity(ModBlockEntities.HYDRAULIC.get(), pos, state)
{
    var currentPower = 0.0

    override fun createConstraints(
        shipId: ShipId,
        constrainedShipId: ShipId,
        compliance: Double,
        maxForce: Double,
        massAverage: Double
    ) {

        // The facing rotations
        val facing = blockState.getValue(FACING)
        val powered = level?.getBestNeighborSignal(blockPos)
        updateCurrentPower(powered?.toDouble()?.div(15) ?: 0.0)

        val headFacing = level!!.getBlockState(mechanicalHeadBlockPos!!).getValue(FACING)

        (level!!.getBlockEntity(mechanicalHeadBlockPos!!) as MechanicalHeadBlockEntity).shouldDrawBeam = level?.getBestNeighborSignal(blockPos)!! > 0

        val hingeOrientation = MechanicalBlockHelper.getRotationQuaternionFromDirection(facing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()
        val headOrientation = MechanicalBlockHelper.getRotationQuaternionFromDirection(headFacing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()

        // Attach constraint
        val attachConstraint = VSAttachmentConstraint(
            shipId,
            constrainedShipId,
            compliance*7,
            blockPos.toJOMLD().add(0.5,0.5,0.5).add(facing.normal.toJOMLD().mul(currentPower ?: 0.0)),
            mechanicalHeadBlockPos!!.toJOMLD().add(0.5,0.5,0.5),
            maxForce,
            0.0
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(attachConstraint)?.let { constraints.add(it) }


        // Slide constraint
        val slideConstraint = VSSlideConstraint(
            shipId,
            constrainedShipId,
            compliance,
            blockPos.toJOMLD().add(0.5,0.5,0.5).add(facing.normal.toJOMLD().mul(powered?.div(3.0) ?: 0.0)),
            mechanicalHeadBlockPos!!.toJOMLD().add(0.5,0.5,0.5),
            maxForce,
            facing.normal.toJOMLD(),
            999999999999.0
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(slideConstraint)?.let { constraints.add(it) }

        // Orientation constraint
        val orientationConstraint = VSFixedOrientationConstraint(
            shipId,
            constrainedShipId,
            compliance,
            hingeOrientation,
            headOrientation,
            maxForce
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(orientationConstraint)?.let { constraints.add(it) }
    }

    override fun tick() {
//        (mechanicalHeadBlockPos?.let { level?.getBlockEntity(it) } as MechanicalHeadBlockEntity).shouldDrawBeam = level?.getBestNeighborSignal(blockPos)!! > 0
        super.tick()
    }


    fun updateCurrentPower(target:Double) {
        currentPower = lerp (currentPower, target, 0.5)
        currentPower = currentPower.coerceIn(0.0,1.0)
    }
}
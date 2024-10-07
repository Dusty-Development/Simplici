package org.valkyrienskies.simplici.content.block.mechanical.ball_hinge

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSSphericalSwingLimitsConstraint
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalConstraintBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalBlockHelper
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class BallHingeBlockEntity(pos: BlockPos, state: BlockState) : MechanicalConstraintBlockEntity(ModBlockEntities.BALL_HINGE.get(), pos, state)
{

    override fun createConstraints(
        shipId: ShipId,
        constrainedShipId: ShipId,
        compliance: Double,
        maxForce: Double,
        massAverage: Double
    ) {
//        // The ship References
//        val shipReference:ServerShip? = level.shipObjectWorld.allShips.getById(shipId) as ServerShip?
//        val constrainedShipReference:ServerShip? = level.shipObjectWorld.allShips.getById(constrainedShipId) as ServerShip?

        // The facing rotations
        val facing = blockState.getValue(FACING)
        val headFacing = level!!.getBlockState(mechanicalHeadBlockPos!!).getValue(FACING)

        val hingeOrientation = MechanicalBlockHelper.getRotationQuaternionFromDirection(facing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()
        val headOrientation = MechanicalBlockHelper.getRotationQuaternionFromDirection(headFacing).mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()

        // Hinge constraint
        val hingeConstraint = VSSphericalSwingLimitsConstraint(
            shipId,
            constrainedShipId,
            compliance,
            hingeOrientation,
            headOrientation,
            maxForce,
            -Math.toRadians(level!!.gameRules.getInt(ModGamerules.BALL_HINGE_MAX_ANGLE).toDouble()),
            Math.toRadians(level!!.gameRules.getInt(ModGamerules.BALL_HINGE_MAX_ANGLE).toDouble())
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

}
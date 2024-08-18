package org.valkyrienskies.simplici.content.block.mechanical.hinge

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.extension.snapToGrid
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.game.ships.ShipObjectServer
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.physics_api.ConstraintId
import kotlin.math.abs

open class HingeConstraintBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState)
    : BlockEntity(blockEntityType, pos, state)
{

    var isConstrained:Boolean = false
    var constrainedBlockPos: BlockPos? = null
    @JsonIgnore val constraints:ArrayList<ConstraintId> = ArrayList()

    var isLoading:Boolean = false

    fun checkConstrainables() {
        val ship = level.getShipObjectManagingPos(blockPos)

        val startPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.51))
        val endPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(1.5))

        val startPos = ship?.shipToWorld?.transformPosition(startPosShip) ?: startPosShip
        val endPos = ship?.shipToWorld?.transformPosition(endPosShip) ?: endPosShip

        val clipContext = ClipContext( startPos.toMinecraft(), endPos.toMinecraft(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
        var clipResult = level?.clipIncludeShips(clipContext, false)
        if (ship != null) {
            clipResult = level?.clipIncludeShips(clipContext, false, ship.id)
        }


        if(clipResult!!.type != HitResult.Type.MISS) {
            createConstraintToBlock(clipResult.blockPos)
        }
    }

    // CONSTRAINTS \\

    open fun createConstraintToBlock(constrainedPos: BlockPos) {
        if (level!!.isClientSide()) return
        breakConstraints() // break any existing constraints just in case

        // Get the ship ids
        val shipId: ShipId = level.getShipObjectManagingPos(blockPos)?.id ?: (level as ServerLevel).shipObjectWorld.dimensionToGroundBodyIdImmutable[level!!.dimensionId]!!.toLong()
        val constrainedShipId: ShipId = level.getShipObjectManagingPos(constrainedPos)?.id ?: (level as ServerLevel).shipObjectWorld.dimensionToGroundBodyIdImmutable[level!!.dimensionId]!!.toLong()

        val facing = blockState.getValue(BlockStateProperties.FACING)

        if (shipId == constrainedShipId) {
            println("Tried to constrain to self")
            return
        }

        val shipRefrence:ServerShip? = level.shipObjectWorld.allShips.getById(shipId) as ServerShip?
        val constrainedShipRefrence:ServerShip? = level.shipObjectWorld.allShips.getById(constrainedShipId) as ServerShip?

        val shipRot = shipRefrence?.transform?.shipToWorldRotation ?: Quaterniond()
        val constrainedShipRot = constrainedShipRefrence?.transform?.shipToWorldRotation ?: Quaterniond()

        val relativeRot: Quaterniond = constrainedShipRot.difference(shipRot, Quaterniond()).snapToGrid()
        val rotationQuaternion: Quaterniondc = HingeHelper.getRotationQuaternionFromDirection(facing)
        val hingeOrientation = rotationQuaternion.mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()

        val massDiffrence = abs((shipRefrence?.inertiaData?.mass ?: constrainedShipRefrence?.inertiaData?.mass!!) + (constrainedShipRefrence?.inertiaData?.mass ?: shipRefrence?.inertiaData?.mass!!)) * 0.5
        val constraintCompliance = 1e-9 / ModConfig.SERVER.HINGE_COMPLIANCE / massDiffrence
        val constraintMaxForce = 1e150 * massDiffrence

        val hingeConstraint = VSHingeOrientationConstraint(
            shipId,
            constrainedShipId,
            constraintCompliance,
            hingeOrientation,
            relativeRot.mul(hingeOrientation, Quaterniond()),
            constraintMaxForce
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(hingeConstraint)?.let { constraints.add(it) }

        val attachConstraint = VSAttachmentConstraint(
            shipId,
            constrainedShipId,
            constraintCompliance,
            blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()),
            constrainedPos.toJOMLD().add(0.5,0.5,0.5),
            constraintMaxForce,
            0.0
        )
        (level as ServerLevel).shipObjectWorld.createNewConstraint(attachConstraint)?.let { constraints.add(it) }

        isConstrained = true
        constrainedBlockPos = constrainedPos
    }

    open fun breakConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = false
        constraints.forEach( (level as ServerLevel).shipObjectWorld::removeConstraint )
    }

    // BLOCK EVENTS \\

    fun onPlaced(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) = checkConstrainables()
    fun onRemoved(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) = breakConstraints()
    open fun onUse(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult) = if (player.isCrouching) breakConstraints() else checkConstrainables()

    open fun tick() {
        if (constrainedBlockPos == null) return

        if (isLoading && level?.isLoaded(constrainedBlockPos!!) == true) {
            loadConstraints()
            return
        }
        if (level!!.getBlockState(constrainedBlockPos!!).isAir && ModConfig.SERVER.REJECT_FLOATING_HINGES) breakConstraints()
    }

    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        super.load(tag)
        isLoading = true
        constrainedBlockPos = BlockPos.of(tag.getLong("constrainedPos"))
    }

    fun loadConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = (constrainedBlockPos != null)
        createConstraintToBlock(constrainedBlockPos!!)
        isLoading = false
    }

    override fun saveAdditional(tag: CompoundTag) {
        if (level!!.isClientSide() && isConstrained) return
        constrainedBlockPos?.let { tag.putLong("constrainedPos", it.asLong()) }
        super.saveAdditional(tag)
    }
}
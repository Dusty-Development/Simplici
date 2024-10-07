package org.valkyrienskies.simplici.content.block.mechanical

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.mod.common.yRange
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.simplici.api.extension.intPos
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.content.block.mechanical.head.MechanicalHeadBlockEntity
import kotlin.math.abs

abstract class MechanicalConstraintBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState)
    : BlockEntity(blockEntityType, pos, state)
{

    var wasStatic = false
    var wasStaticSaved = false
    var staticTicks = 0
    val staticMaxTicks = 1

    var isConstrained:Boolean = false
    var mechanicalHeadBlockPos: BlockPos? = null
    var constrainedShipId: ShipId? = null
    @JsonIgnore val constraints:ArrayList<ConstraintId> = ArrayList()

    var isLoading:Boolean = false

    open fun resetHingeHead() {
        // Destroy present head if needed
        staticTicks = staticMaxTicks
        mechanicalHeadBlockPos?.let { level!!.destroyBlock(it, false) }

        val ship = level.getShipObjectManagingPos(blockPos)

        // Get global position
        val startPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(FACING).normal.toJOMLD().mul(0.51))
        val endPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(FACING).normal.toJOMLD().mul(1.51))

        // Transform local position to global
        val startPos = ship?.shipToWorld?.transformPosition(startPosShip) ?: startPosShip
        val endPos = ship?.shipToWorld?.transformPosition(endPosShip) ?: endPosShip

        val clipContext = ClipContext( startPos.toMinecraft(), endPos.toMinecraft(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)

        val clipResult: BlockHitResult? =
            when {
                ship != null -> level?.clipIncludeShips(clipContext, false, ship.id)
                else -> level?.clipIncludeShips(clipContext, false)
            }

        if(clipResult!!.type == HitResult.Type.BLOCK && !level!!.isEmptyBlock(clipResult.blockPos)) {
            addHingeHeadToShip(clipResult)
        } else {
            createHingeHeadShip(ship)
        }
    }

    // Creates a new ship and puts our hinge head on it
    // this freaks tf out sometimes
    fun createHingeHeadShip(parentShip: LoadedShip?) {
        if (level!!.isClientSide) return

        // Make a ship
        val dimensionId = level?.dimensionId

        // Create the ship with some temp defaults
        val serverShip = (level as ServerLevel).shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, parentShip?.transform?.shipToWorldScaling?.x() ?: 1.0, dimensionId!!)
        constrainedShipId = serverShip.id

        // Create the head block
        val centerPos = serverShip.chunkClaim.getCenterBlockCoordinates((level as ServerLevel).yRange).toBlockPos()
        val newBlockState = ModBlocks.MECHANICAL_HEAD.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING))
        (level as ServerLevel).setBlock(centerPos, newBlockState, 1 or 2)
        mechanicalHeadBlockPos = centerPos
        (level!!.getBlockEntity(centerPos) as MechanicalHeadBlockEntity).parentBlockPos = blockPos

        wasStaticSaved = false
        serverShip.isStatic = true

        // Only if we are on a ship.
        if (parentShip != null) {
            // Compute the ship transform
            val newShipPosInWorld = parentShip.shipToWorld.transformPosition(blockPos.toJOMLD().add(0.5, 0.5, 0.5))
            val newShipPosInShipyard = blockPos.toJOMLD().add(0.5, 0.5, 0.5)
            val newShipRotation = parentShip.transform.shipToWorldRotation
            val newShipScaling = parentShip.transform.shipToWorldScaling

            val shipTransform = ShipTransformImpl(newShipPosInWorld, newShipPosInShipyard, newShipRotation, newShipScaling)
            (serverShip as ShipDataCommon).transform = shipTransform
            serverShip.updatePrevTickShipTransform()
        }

    }

    fun addHingeHeadToShip(clipResult: BlockHitResult) {
        if (level!!.isClientSide) return

        // Create the head block
        val centerPos = clipResult.blockPos.toJOML().add(clipResult.direction.normal.toJOML()).toBlockPos()
        val newBlockState = ModBlocks.MECHANICAL_HEAD.get().defaultBlockState().setValue(FACING, clipResult.direction.opposite)
        (level as ServerLevel).setBlock(centerPos, newBlockState, 1 or 2)
        mechanicalHeadBlockPos = centerPos
        (level!!.getBlockEntity(centerPos) as MechanicalHeadBlockEntity).parentBlockPos = blockPos
    }

    // CONSTRAINTS \\

    open fun applyConstraints() {
        if (level!!.isClientSide()) return
        breakConstraints() // break any existing constraints just in case

        // Don't apply if there is no block to apply to...
        // This shouldn't be called but just in case
        if(mechanicalHeadBlockPos == null || level!!.isEmptyBlock(mechanicalHeadBlockPos!!)) return

        // Get the ship ids
        val shipId: ShipId = level.getShipObjectManagingPos(blockPos)?.id ?: (level as ServerLevel).shipObjectWorld.dimensionToGroundBodyIdImmutable[level!!.dimensionId]!!.toLong()
        val constrainedShipId: ShipId = level.getShipObjectManagingPos(mechanicalHeadBlockPos!!.intPos())?.id ?: (level as ServerLevel).shipObjectWorld.dimensionToGroundBodyIdImmutable[level!!.dimensionId]!!.toLong()

        // Prevent trying to connect to self
        if (shipId == constrainedShipId) return

        // The ship References
        val shipReference:ServerShip? = level.shipObjectWorld.allShips.getById(shipId) as ServerShip?
        val constrainedShipReference:ServerShip? = level.shipObjectWorld.allShips.getById(constrainedShipId) as ServerShip?

        // Constraint vars
        val massAverage = abs((shipReference?.inertiaData?.mass ?: constrainedShipReference?.inertiaData?.mass!!) + (constrainedShipReference?.inertiaData?.mass ?: shipReference?.inertiaData?.mass!!)) * 0.5
        val constraintCompliance = (1e-7 / massAverage)
        val constraintMaxForce = 1e150 * massAverage

        if(level?.isLoaded(mechanicalHeadBlockPos!!) == true) createConstraints(shipId, constrainedShipId, constraintCompliance, constraintMaxForce, massAverage)

        if(staticTicks == 0) {
            shipReference?.isStatic = wasStatic
            constrainedShipReference?.isStatic = false
            wasStatic = false
        }
        if(staticTicks > 0) {
            wasStatic = shipReference?.isStatic == true
            wasStaticSaved = true
            shipReference?.isStatic = true
            constrainedShipReference?.isStatic = true
        }

        isConstrained = true
    }

    // if needed you can add this to get the ships:
    //  val shipReference:ServerShip? = level.shipObjectWorld.allShips.getById(shipId) as ServerShip?
    //  val constrainedShipReference:ServerShip? = level.shipObjectWorld.allShips.getById(constrainedShipId) as ServerShip?

    abstract fun createConstraints(shipId: ShipId, constrainedShipId: ShipId, compliance:Double, maxForce:Double, massAverage:Double = 1.0)

    open fun breakConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = false
        constraints.forEach( (level as ServerLevel).shipObjectWorld::removeConstraint )
    }

    // BLOCK EVENTS \\

    fun onPlaced() = resetHingeHead()
    fun onRemoved() {
        breakConstraints()
        mechanicalHeadBlockPos?.let { level!!.destroyBlock(it, false) }
    }
    open fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult) { }

    open fun tick() {
        if(level!!.isClientSide) return
        if (mechanicalHeadBlockPos == null) return

        if (isLoading && level?.isLoaded(mechanicalHeadBlockPos!!) == true) {
            loadConstraints()
            return
        }
        if (level!!.isEmptyBlock(mechanicalHeadBlockPos!!)) {
            onRemoved()
            level!!.destroyBlock(blockPos, true) // This doesn't trigger a break event...
            return
        }

        applyConstraints()
        if (staticTicks >= 0) staticTicks--
    }

    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        super.load(tag)
        isLoading = true
        mechanicalHeadBlockPos = BlockPos.of(tag.getLong("head_pos"))
    }

    fun loadConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = (mechanicalHeadBlockPos != null)
        applyConstraints()
        isLoading = false
    }

    override fun saveAdditional(tag: CompoundTag) {
        if (level!!.isClientSide() && isConstrained) return
        mechanicalHeadBlockPos?.let { tag.putLong("head_pos", it.asLong()) }
        super.saveAdditional(tag)
    }
}
package org.valkyrienskies.simplici.content.entity.rope

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.entity.EntityInLevelCallback
import org.joml.Matrix3d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintId
import org.valkyrienskies.core.apigame.constraints.VSSphericalTwistLimitsConstraint
import org.valkyrienskies.core.apigame.physics.PhysicsEntityData
import org.valkyrienskies.core.apigame.physics.VSCapsuleCollisionShapeData
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.entity.VSPhysicsEntity
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.simplici.content.entity.ModEntities
import org.valkyrienskies.simplici.content.gamerule.ModGamerules
import org.valkyrienskies.simplici.content.item.ModItems
import org.valkyrienskies.simplici.content.ship.util.PulseForceInducer
import kotlin.math.PI

class RopeSegmentEntity (type: EntityType<RopeSegmentEntity>, level: Level): VSPhysicsEntity(type as EntityType<VSPhysicsEntity>, level) {

    var setToDestroy = false
    var parentRope:RopeSegmentEntity? = null
    val childrenRopes:ArrayList<RopeSegmentEntity> = ArrayList()
    val constraints:ArrayList<VSConstraintId> = ArrayList()
    val worldConstraints:ArrayList<VSConstraintId> = ArrayList()

    fun setNeedsUpdating(enabled: Boolean) { this.physicsEntityServer!!.needsUpdating = enabled }

    override fun interact(player: Player, interactionHand: InteractionHand): InteractionResult {

        if(player.isShiftKeyDown && !setToDestroy) {
            // Collect rope

            destroyRope()
            val itemStack = ModItems.ROPE.get().defaultInstance
            player.inventory.add(itemStack)

        } else {
            // Pull rope
            if(!this.level().isClientSide) {
                val inducer = getPulseInducer()
                val force = player.forward.toJOML().mul(-75000.0)
                force?.let { inducer?.addLinearPulse(it) }
            }
        }

        return super.interact(player, interactionHand)
    }

    override fun hurt(damageSource: DamageSource, f: Float): Boolean {
        if(!this.level().isClientSide) {
            val inducer = getPulseInducer()
            val force = damageSource.entity?.forward?.toJOML()?.mul(75000.0)
            force?.let { inducer?.addLinearPulse(it) }
        }
        return super.hurt(damageSource, f)
    }

    fun destroyRope() {
        val root = if( parentRope == null ) this else parentRope!!

        root.setToDestroy = true
        root.childrenRopes.forEach {
            it.discard()
            it.setToDestroy = true
        }
        root.discard()
    }

    override fun tick() {
        super.tick()
    }

    override fun setLevelCallback(callback: EntityInLevelCallback) {
        super.setLevelCallback(callback)
        if(level().isClientSide) return

        physicsEntityServer?.forceInducers?.add(PulseForceInducer())
    }

    fun getPulseInducer() : PulseForceInducer? = (this.physicsEntityServer?.forceInducers?.firstOrNull {it is PulseForceInducer})  as PulseForceInducer?
    fun getID():ShipId? = this.physicsEntityData?.shipId

    override fun mayInteract(level: Level, blockPos: BlockPos): Boolean = true
    override fun isAttackable(): Boolean = true
    override fun isPickable(): Boolean = true
    override fun skipAttackInteraction(entity: Entity): Boolean = super.skipAttackInteraction(entity)

    override fun getDimensions(pose: Pose): EntityDimensions = EntityDimensions(0.5F,0.5F,false)

    companion object {

        val radius: Double get() = 0.125
        val halfLength: Double get() = 0.25
        val mass:   Double get() = 100.0

        fun createSegmentEntity(level: ServerLevel, pos: Vector3dc, rotation: Quaterniond, spawnStatic: Boolean = false, parentEntity: RopeSegmentEntity? = null): RopeSegmentEntity {
            val entity = ModEntities.ROPE_SEGMENT.get().create(level)!!
            val shipId = level.shipObjectWorld.allocateShipId(level.dimensionId)

            val transform = ShipTransformImpl.create(pos, Vector3d(), rotation)
            val physEntityData = createShapeData(shipId, transform, radius, halfLength, mass, spawnStatic)

            entity.setPhysicsEntityData(physEntityData)
            entity.parentRope = parentEntity
            entity.setPos(pos.x(), pos.y(), pos.z())
//            entity.setNeedsUpdating(true)
            level.addFreshEntity(entity)

            parentEntity?.childrenRopes?.add(entity)
            return entity
        }

        fun createSegmentConstrants(level: ServerLevel, parent:RopeSegmentEntity, first:RopeSegmentEntity, second:RopeSegmentEntity) {
            val shipA = first.physicsEntityData?.shipId
            val shipB = second.physicsEntityData?.shipId

            if(shipA == null || shipB == null) return

            // Attach constraint
            val attachConstraint = VSAttachmentConstraint(
                shipA,
                shipB,
                1e-12,
                Vector3d(halfLength,0.0,0.0),
                Vector3d(-halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(attachConstraint)?.let { parent.constraints.add(it) }
            level.shipObjectWorld.disableCollisionBetweenBodies(shipA, shipB)

            // Twist constraint
            val twistLimitsConstraint = VSSphericalTwistLimitsConstraint(
                shipA,
                shipB,
                1e-12,
                Quaterniond(),
                Quaterniond(),
                1e150,
                -Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble()),
                Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble())
            )
            level.shipObjectWorld.createNewConstraint(twistLimitsConstraint)?.let { parent.constraints.add(it) }
            level.shipObjectWorld.disableCollisionBetweenBodies(shipA, shipB)
        }

        // Creates a full rope and returns the (start rope, end rope)
        fun createRope(level: ServerLevel, start: Vector3dc, end: Vector3dc, spawnStatic: Boolean = false): Pair<RopeSegmentEntity,RopeSegmentEntity> {
            val ropeTotalLength = start.distance(end)
            val ropeTotalNormal = end.sub(start, Vector3d()).normalize()

            val length = halfLength * 2
            val tolerance = 0.1 // The margin to decide whether an extra segment is needed
            val segmentDivisions = (ropeTotalLength / length).toInt() // this dosent include any overlap with the last point
            val remainder = ropeTotalLength % length
            val segmentCount = if (remainder > tolerance) segmentDivisions + 1 else segmentDivisions

            val rotation = Quaterniond().rotationTo(Vector3d(1.0,0.0,0.0), ropeTotalNormal)

            println("distance of: $ropeTotalLength created #${segmentCount}")
            var previousSegment: RopeSegmentEntity? = null
            var parentSegment: RopeSegmentEntity? = null

            val halfLengthOffset = ropeTotalNormal.mul(halfLength, Vector3d())
            val startSegmentPosition = start.add(halfLengthOffset, Vector3d())

            for (i in 0 until segmentCount) {
                val segmentPos = startSegmentPosition.add(ropeTotalNormal.mul(i * length, Vector3d()), Vector3d())
                val segment = createSegmentEntity(level, segmentPos, rotation, spawnStatic, parentSegment)

                // Create constraints
                if (previousSegment != null) { parentSegment?.let { createSegmentConstrants(level, it, previousSegment!!, segment) } }

                if (i == 0) { parentSegment = segment }

                previousSegment = segment
            }

            return Pair(parentSegment!!, previousSegment!!)
        }

        fun constrainRopeToBlock(level: ServerLevel, ropeSegment: RopeSegmentEntity, ropeSegmentProgress:Double, pos:Vector3dc, shipId: ShipId) {
            if(ropeSegment.parentRope == null) return

            val ropeID = ropeSegment.physicsEntityData?.shipId ?: return

            // Attach constraint
            val attachConstraint = VSAttachmentConstraint(
                shipId,
                ropeID,
                1e-12,
                Vector3d(halfLength,0.0,0.0),
                Vector3d(-halfLength,0.0,0.0),
                1e150,
                0.0
            )
            level.shipObjectWorld.createNewConstraint(attachConstraint)?.let { ropeSegment.parentRope!!.worldConstraints.add(it) }
            level.shipObjectWorld.disableCollisionBetweenBodies(shipId, ropeID)

            // Twist constraint
            val twistLimitsConstraint = VSSphericalTwistLimitsConstraint(
                shipId,
                ropeID,
                1e-12,
                Quaterniond(),
                Quaterniond(),
                1e150,
                -Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble()),
                Math.toRadians(level.gameRules.getInt(ModGamerules.ROPE_MAX_TWIST).toDouble())
            )
            level.shipObjectWorld.createNewConstraint(twistLimitsConstraint)?.let { ropeSegment.parentRope!!.worldConstraints.add(it) }
            level.shipObjectWorld.disableCollisionBetweenBodies(shipId, ropeID)
       }

        //https://www.gamedev.net/tutorials/programming/math-and-physics/capsule-inertia-tensor-r3856/
        fun createShapeData(shipId: ShipId, transform: ShipTransform, radius: Double, length: Double, mass: Double, spawnStatic: Boolean=false): PhysicsEntityData {
            val radiusSquared = radius * radius

            val c = length * radiusSquared * PI
            val hs = 2.0 * radiusSquared * radius * PI * (1.0/3.0)

            val density = mass / (c + hs)

            val cM = c * density
            val hsM = hs * density

            val inertiaTensor = Matrix3d()

            inertiaTensor.m11 = radiusSquared * cM * 0.5

            inertiaTensor.m22 = inertiaTensor.m11 * 0.5 + cM * length * length * (1.0/12.0)
            inertiaTensor.m00 = inertiaTensor.m22

            val temp0 = hsM * 2.0 * radiusSquared / 5.0
            inertiaTensor.m11 += temp0 * 2.0
            val temp1 = length * 0.5
            val temp2 = temp0 + hsM * (temp1 * temp1 + 3.0 * (1.0/8.0) * length * radius)
            inertiaTensor.m00 += temp2 * 2.0
            inertiaTensor.m22 += temp2 * 2.0

            val inertiaData: ShipInertiaData = ShipInertiaDataImpl(Vector3d(), mass, inertiaTensor)
            return PhysicsEntityData(
                shipId,
                transform,
                inertiaData,
                Vector3d(),
                Vector3d(),
                VSCapsuleCollisionShapeData(radius, length),
                isStatic = spawnStatic
            )
        }

    }
}
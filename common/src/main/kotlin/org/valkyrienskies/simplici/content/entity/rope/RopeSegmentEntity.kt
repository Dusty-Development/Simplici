package org.valkyrienskies.simplici.content.entity.rope

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.level.Level
import org.joml.*
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.physics.PhysicsEntityData
import org.valkyrienskies.core.apigame.physics.VSCapsuleCollisionShapeData
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.entity.VSPhysicsEntity
import org.valkyrienskies.mod.common.getShipManaging
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.simplici.content.entity.ModEntities
import kotlin.math.PI
import kotlin.math.roundToInt

class RopeSegmentEntity (type: EntityType<RopeSegmentEntity>, level: Level): VSPhysicsEntity(type as EntityType<VSPhysicsEntity>, level) {

    var shipId:ShipId? = null

    var parentRope:RopeSegmentEntity? = null
    val childrenRopes:ArrayList<RopeSegmentEntity> = ArrayList()
    val constraints:ArrayList<ConstraintId> = ArrayList()

    fun setNeedsUpdating(enabled: Boolean) { this.physicsEntityServer!!.needsUpdating = enabled }

    override fun getDimensions(pose: Pose): EntityDimensions { return EntityDimensions(0.25F,0.25F,false) }

    companion object {

        val radius: Double get() = 0.25
        val halfLength: Double get() = 0.5
        val mass:   Double get() = 100.0

        fun createSegmentEntity(level: ServerLevel, pos: Vector3dc, rotation: Quaterniond, spawnStatic: Boolean = false, parentEntity: RopeSegmentEntity? = null): RopeSegmentEntity {
            val entity = ModEntities.ROPE_SEGMENT.get().create(level)!!
            val shipId = level.shipObjectWorld.allocateShipId(level.dimensionId)

            val transform = ShipTransformImpl.create(pos, Vector3d(), rotation)
            val physEntityData = createShapeData(shipId, transform, radius, halfLength, mass, spawnStatic)

            entity.setPhysicsEntityData(physEntityData)
            entity.shipId = shipId
            entity.parentRope = parentEntity
            entity.setPos(pos.x(), pos.y(), pos.z())
            //entity.setNeedsUpdating(true)
            level.addFreshEntity(entity)

            parentEntity?.childrenRopes?.add(entity)
            return entity
        }

        fun createSegmentConstrants(level: ServerLevel, parent:RopeSegmentEntity, first:RopeSegmentEntity, second:RopeSegmentEntity) {
            val shipA = first.shipId
            val shipB = second.shipId

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
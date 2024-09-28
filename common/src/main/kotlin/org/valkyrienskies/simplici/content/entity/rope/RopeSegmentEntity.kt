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
import org.valkyrienskies.core.apigame.physics.PhysicsEntityData
import org.valkyrienskies.core.apigame.physics.VSCapsuleCollisionShapeData
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.entity.VSPhysicsEntity
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.simplici.content.entity.ModEntities
import kotlin.math.PI
import kotlin.math.roundToInt

class RopeSegmentEntity (type: EntityType<RopeSegmentEntity>, level: Level): VSPhysicsEntity(type as EntityType<VSPhysicsEntity>, level) {



    var parentRope:RopeSegmentEntity? = null
    val childrenRopes:ArrayList<RopeSegmentEntity> = ArrayList()
    val constraints:ArrayList<RopeSegmentEntity> = ArrayList()

    fun setNeedsUpdating(enabled: Boolean) { this.physicsEntityServer!!.needsUpdating = enabled }

    override fun getDimensions(pose: Pose): EntityDimensions { return EntityDimensions(0.25F,0.25F,false) }

    companion object {

        val radius: Double get() = 0.25
        val length: Double get() = 0.5
        val mass:   Double get() = 15.0

        fun createEntity(level: ServerLevel, pos: Vector3dc, rotation: Quaterniond, spawnStatic: Boolean = false, parentEntity: RopeSegmentEntity? = null): RopeSegmentEntity {
            val entity = ModEntities.ROPE_SEGMENT.get().create(level)!!
            val shipId = level.shipObjectWorld.allocateShipId(level.dimensionId)

            val transform = ShipTransformImpl.create(pos, Vector3d(), rotation)
            val physEntityData = createShapeData(shipId, transform, radius, length, mass, spawnStatic)

            entity.setPhysicsEntityData(physEntityData)
            entity.parentRope = parentEntity
            entity.setPos(pos.x(), pos.y(), pos.z())
            //entity.setNeedsUpdating(true)
            level.addFreshEntity(entity)

            return entity
        }

        // Creates a full rope and returns the (start rope, end rope)
        fun createRope(level: ServerLevel, start: Vector3dc, end: Vector3dc, spawnStatic: Boolean = false): Pair<RopeSegmentEntity,RopeSegmentEntity> {

            val ropeTotalLength = start.distance(end)
            val ropeTotalNormal = end.sub(start, Vector3d()).normalize()
            val segmentLength = length + radius

            val segmentCount = Math.ceil(ropeTotalLength / (segmentLength * 2)).roundToInt()

            val rotation = Quaterniond().rotationTo(Vector3d(1.0,0.0,0.0), ropeTotalNormal)

            println("distance of: $ropeTotalLength created #${segmentCount + 1}")
            var startEntity:RopeSegmentEntity? = null
            var lastEntity:RopeSegmentEntity? = null
            for (i in 0..segmentCount) {
                val position = start.add(ropeTotalNormal.mul(i * segmentLength, Vector3d()), Vector3d())
                val entity = createEntity(level, position, rotation, spawnStatic, startEntity)
                entity.childrenRopes.add(entity)

                lastEntity = entity
                if(startEntity == null) startEntity = entity
            }

            return Pair(startEntity!!,lastEntity!!)
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
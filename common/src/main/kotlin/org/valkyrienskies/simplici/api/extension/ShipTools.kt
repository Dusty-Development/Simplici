package org.valkyrienskies.simplici.api.extension

import org.joml.Vector3d
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

fun PhysShipImpl.getVelAtPos(worldPointPosition: Vector3d): Vector3d {
    val centerOfMassPos = worldPointPosition.sub(this.transform.positionInWorld, Vector3d())
    return this.velocity.add(this.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
}

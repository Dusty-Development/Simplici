package org.valkyrienskies.simplici.api.math

import org.joml.Vector3d

object SpringHelper {

    // Spring equation from Toyful Games: https://www.youtube.com/watch?v=CdPYlj5uZeI&t=537s
    fun calculateSpringForceDouble(offset:Double, velocity:Double, strength:Double, damping:Double):Double = (offset * strength) - (velocity * damping)
    fun calculateSpringForceVector3d(offset:Vector3d, velocity:Vector3d, strength:Double, damping:Double):Vector3d = (offset.mul(strength, Vector3d())).sub(velocity.mul(damping))

}
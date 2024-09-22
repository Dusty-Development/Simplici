package org.valkyrienskies.simplici.content.block.mechanical

import net.minecraft.core.Direction
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Vector3d

object MechanicalBlockHelper {

    fun getRotationQuaternionFromDirection(facing:Direction) : Quaterniond {
        return when (facing) {
            Direction.DOWN -> Quaterniond(AxisAngle4d(Math.PI, Vector3d(1.0, 0.0, 0.0)))
            Direction.NORTH -> Quaterniond(AxisAngle4d(Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            Direction.EAST -> Quaterniond(AxisAngle4d(0.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            Direction.SOUTH -> Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0))).normalize()
            Direction.WEST -> Quaterniond(AxisAngle4d(1.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            Direction.UP -> Quaterniond()
            else -> Quaterniond() // This should be impossible, but have this here just in case
        }
    }

}
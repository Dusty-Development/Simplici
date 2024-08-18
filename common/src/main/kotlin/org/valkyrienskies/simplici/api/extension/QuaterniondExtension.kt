package org.valkyrienskies.simplici.api.extension

import org.joml.Quaterniond
import org.joml.Quaterniondc
import kotlin.math.absoluteValue

fun Quaterniond.snapToGrid() : Quaterniond { return this.snapToGrid(null) }
fun Quaterniond.snapToGrid(dest: Quaterniond?) : Quaterniond {
    val target = dest ?: this // use dest if possible

    var currentBiggestDotProduct = Double.NEGATIVE_INFINITY
    var smallestIndex = -1
    snappingRotations.forEachIndexed { i: Int, snapRot: Quaterniondc ->
        val dotProduct = this.dot(snapRot).absoluteValue
        if (dotProduct > currentBiggestDotProduct) {
            currentBiggestDotProduct = dotProduct
            smallestIndex = i
        }
    }

    target.set(snappingRotations[smallestIndex])
    return target
}

private val snappingRotations:List<Quaterniondc> = listOf(
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 0.000E+0,  7.071E-1,  0.000E+0,  7.071E-1),
    Quaterniond(-0.000E+0,  1.000E+0,  0.000E+0, -0.000E+0),
    Quaterniond(-0.000E+0,  7.071E-1,  0.000E+0, -7.071E-1),
    Quaterniond( 7.071E-1,  0.000E+0,  0.000E+0,  7.071E-1),
    Quaterniond( 5.000E-1,  5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond(-0.000E+0,  7.071E-1,  7.071E-1, -0.000E+0),
    Quaterniond(-5.000E-1,  5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond( 1.000E+0,  0.000E+0,  0.000E+0, -0.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0,  7.071E-1, -0.000E+0),
    Quaterniond( 0.000E+0, -0.000E+0,  1.000E+0,  0.000E+0),
    Quaterniond(-7.071E-1, -0.000E+0,  7.071E-1,  0.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0,  0.000E+0, -7.071E-1),
    Quaterniond( 5.000E-1, -5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond( 0.000E+0, -7.071E-1,  7.071E-1,  0.000E+0),
    Quaterniond(-5.000E-1, -5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 0.000E+0,  0.000E+0,  7.071E-1,  7.071E-1),
    Quaterniond( 0.000E+0, -0.000E+0,  1.000E+0, -0.000E+0),
    Quaterniond( 0.000E+0, -0.000E+0,  7.071E-1, -7.071E-1),
    Quaterniond( 0.000E+0,  7.071E-1,  0.000E+0,  7.071E-1),
    Quaterniond( 5.000E-1,  5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond( 7.071E-1, -0.000E+0,  7.071E-1, -0.000E+0),
    Quaterniond( 5.000E-1, -5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond( 0.000E+0,  1.000E+0,  0.000E+0, -0.000E+0),
    Quaterniond( 7.071E-1,  7.071E-1,  0.000E+0, -0.000E+0),
    Quaterniond( 1.000E+0,  0.000E+0, -0.000E+0,  0.000E+0),
    Quaterniond( 7.071E-1, -7.071E-1, -0.000E+0,  0.000E+0),
    Quaterniond( 0.000E+0,  7.071E-1,  0.000E+0, -7.071E-1),
    Quaterniond( 5.000E-1,  5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond( 7.071E-1,  0.000E+0, -7.071E-1,  0.000E+0),
    Quaterniond( 5.000E-1, -5.000E-1, -5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0,  0.000E+0,  7.071E-1),
    Quaterniond( 1.000E+0,  0.000E+0, -0.000E+0, -0.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0, -0.000E+0, -7.071E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  7.071E-1,  7.071E-1),
    Quaterniond( 5.000E-1,  5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond( 7.071E-1,  7.071E-1, -0.000E+0, -0.000E+0),
    Quaterniond( 5.000E-1,  5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  1.000E+0, -0.000E+0),
    Quaterniond( 0.000E+0,  7.071E-1,  7.071E-1, -0.000E+0),
    Quaterniond(-0.000E+0,  1.000E+0,  0.000E+0,  0.000E+0),
    Quaterniond(-0.000E+0,  7.071E-1, -7.071E-1,  0.000E+0),
    Quaterniond( 0.000E+0,  0.000E+0,  7.071E-1, -7.071E-1),
    Quaterniond(-5.000E-1,  5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond(-7.071E-1,  7.071E-1,  0.000E+0,  0.000E+0),
    Quaterniond(-5.000E-1,  5.000E-1, -5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0,  0.000E+0,  7.071E-1),
    Quaterniond( 1.000E+0,  0.000E+0, -0.000E+0, -0.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0, -0.000E+0, -7.071E-1),
    Quaterniond( 0.000E+0,  7.071E-1,  0.000E+0,  7.071E-1),
    Quaterniond( 5.000E-1,  5.000E-1, -5.000E-1,  5.000E-1),
    Quaterniond( 7.071E-1,  0.000E+0, -7.071E-1, -0.000E+0),
    Quaterniond( 5.000E-1, -5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond(-0.000E+0,  1.000E+0,  0.000E+0,  0.000E+0),
    Quaterniond(-0.000E+0,  7.071E-1, -7.071E-1,  0.000E+0),
    Quaterniond( 0.000E+0,  0.000E+0, -1.000E+0,  0.000E+0),
    Quaterniond( 0.000E+0, -7.071E-1, -7.071E-1,  0.000E+0),
    Quaterniond(-0.000E+0,  7.071E-1,  0.000E+0, -7.071E-1),
    Quaterniond(-5.000E-1,  5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond(-7.071E-1,  0.000E+0, -7.071E-1,  0.000E+0),
    Quaterniond(-5.000E-1, -5.000E-1, -5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 0.000E+0,  7.071E-1,  0.000E+0,  7.071E-1),
    Quaterniond(-0.000E+0,  1.000E+0,  0.000E+0, -0.000E+0),
    Quaterniond(-0.000E+0,  7.071E-1,  0.000E+0, -7.071E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  7.071E-1,  7.071E-1),
    Quaterniond(-5.000E-1,  5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond(-7.071E-1,  7.071E-1,  0.000E+0, -0.000E+0),
    Quaterniond(-5.000E-1,  5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond( 0.000E+0, -0.000E+0,  1.000E+0,  0.000E+0),
    Quaterniond(-7.071E-1, -0.000E+0,  7.071E-1,  0.000E+0),
    Quaterniond(-1.000E+0,  0.000E+0,  0.000E+0,  0.000E+0),
    Quaterniond(-7.071E-1,  0.000E+0, -7.071E-1,  0.000E+0),
    Quaterniond( 0.000E+0, -0.000E+0,  7.071E-1, -7.071E-1),
    Quaterniond(-5.000E-1, -5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond(-7.071E-1, -7.071E-1,  0.000E+0,  0.000E+0),
    Quaterniond(-5.000E-1, -5.000E-1, -5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0,  0.000E+0,  0.000E+0,  1.000E+0),
    Quaterniond( 0.000E+0,  0.000E+0,  7.071E-1,  7.071E-1),
    Quaterniond( 0.000E+0, -0.000E+0,  1.000E+0, -0.000E+0),
    Quaterniond( 0.000E+0, -0.000E+0,  7.071E-1, -7.071E-1),
    Quaterniond( 7.071E-1,  0.000E+0,  0.000E+0,  7.071E-1),
    Quaterniond( 5.000E-1, -5.000E-1,  5.000E-1,  5.000E-1),
    Quaterniond( 0.000E+0, -7.071E-1,  7.071E-1, -0.000E+0),
    Quaterniond(-5.000E-1, -5.000E-1,  5.000E-1, -5.000E-1),
    Quaterniond( 1.000E+0,  0.000E+0, -0.000E+0,  0.000E+0),
    Quaterniond( 7.071E-1, -7.071E-1, -0.000E+0,  0.000E+0),
    Quaterniond( 0.000E+0, -1.000E+0,  0.000E+0,  0.000E+0),
    Quaterniond(-7.071E-1, -7.071E-1,  0.000E+0,  0.000E+0),
    Quaterniond( 7.071E-1,  0.000E+0, -0.000E+0, -7.071E-1),
    Quaterniond( 5.000E-1, -5.000E-1, -5.000E-1, -5.000E-1),
    Quaterniond( 0.000E+0, -7.071E-1, -7.071E-1,  0.000E+0),
    Quaterniond(-5.000E-1, -5.000E-1, -5.000E-1,  5.000E-1),
)
package org.valkyrienskies.simplici.api.util

class InterpolationCurve {
    private val dataPoints: MutableMap<Double, Double> = mutableMapOf()

    var lowestX = 0.0
    var highestX = 0.0

    var negativeInfY = 0.0
    var positiveInfY = 0.0

    fun createDataPoint(x: Double, y: Double) : InterpolationCurve {
        dataPoints[x] = y

        if (x > highestX) {
            highestX = x
            positiveInfY = y
        }
        if (x < lowestX) {
            lowestX = x
            negativeInfY = y
        }


        return this
    }

    fun getValueAtX(x: Double): Double {

        val xValues = dataPoints.keys.sorted()
        val lowerX = xValues.lastOrNull { it <= x } ?: xValues.first()
        val upperX = xValues.firstOrNull { it > x } ?: xValues.last()

        val lowerY = dataPoints[lowerX] ?: 0.0
        val upperY = dataPoints[upperX] ?: 0.0

        return interpolate(x, lowerX, upperX, lowerY, upperY)
    }

    private fun interpolate(x: Double, x0: Double, x1: Double, y0: Double, y1: Double): Double {
        val t = (x - x0) / (x1 - x0)
        return y0 + t * (y1 - y0)
    }
}
package org.valkyrienskies.simplici.api.math

import net.minecraft.util.Mth
import org.joml.Quaterniond

class PIDController {
    enum class DerivativeMeasurement {
        Velocity,
        ErrorRateOfChange
    }

    //PID coefficients
    var proportionalGain: Double = 0.0
    var integralGain: Double = 0.0
    var derivativeGain: Double = 0.0

    var outputMin: Double = -1.0
    var outputMax: Double = 1.0
    var integralSaturation: Double = 0.0
    var derivativeMeasurement: DerivativeMeasurement? = null

    var valueLast: Double = 0.0
    var errorLast: Double = 0.0
    var integrationStored: Double = 0.0
    var velocity: Double = 0.0 //only used for the info display
    var derivativeInitialized: Boolean? = null

    fun Reset() {
        derivativeInitialized = false
    }

    fun Update(dt: Double, currentValue: Double, targetValue: Double): Double {
        val error = targetValue - currentValue

        //calculate P term
        val P = proportionalGain * error

        //calculate I term
        integrationStored = Mth.clamp(integrationStored + (error * dt), -integralSaturation, integralSaturation)
        val I = integralGain * integrationStored

        //calculate both D terms
        val errorRateOfChange = (error - errorLast) / dt
        errorLast = error

        val valueRateOfChange = (currentValue - valueLast) / dt
        valueLast = currentValue
        velocity = valueRateOfChange

        //choose D term to use
        var deriveMeasure = 0.0

        if (derivativeInitialized == true) {
            deriveMeasure = if (derivativeMeasurement == DerivativeMeasurement.Velocity) {
                -valueRateOfChange
            } else {
                errorRateOfChange
            }
        } else {
            derivativeInitialized = true
        }

        val D = derivativeGain * deriveMeasure

        val result = P + I + D

        return Mth.clamp(result, outputMin, outputMax)
    }

    fun AngleDifference(a: Double, b: Double): Double {
        return (a - b + 540) % 360 - 180 //calculate modular difference, and remap to [-180, 180]
    }

    fun UpdateAngle(dt: Double, currentAngle: Double, targetAngle: Double): Double {
        val error = AngleDifference(targetAngle, currentAngle)

        //calculate P term
        val P = proportionalGain * error

        //calculate I term
        integrationStored = Mth.clamp(integrationStored + (error * dt), -integralSaturation, integralSaturation)
        val I = integralGain * integrationStored

        //calculate both D terms
        val errorRateOfChange = AngleDifference(error, errorLast) / dt
        errorLast = error

        val valueRateOfChange = AngleDifference(currentAngle, valueLast) / dt
        valueLast = currentAngle
        velocity = valueRateOfChange

        //choose D term to use
        var deriveMeasure = 0.0

        if (derivativeInitialized == true) {
            deriveMeasure = if (derivativeMeasurement == DerivativeMeasurement.Velocity) {
                -valueRateOfChange
            } else {
                errorRateOfChange
            }
        } else {
            derivativeInitialized = true
        }

        val D = derivativeGain * deriveMeasure

        val result = P + I + D

        return Mth.clamp(result, outputMin, outputMax)
    }

    fun setPID(p:Double, i:Double, d:Double) {
        proportionalGain = p
        integralGain = i
        derivativeGain = d
    }
}
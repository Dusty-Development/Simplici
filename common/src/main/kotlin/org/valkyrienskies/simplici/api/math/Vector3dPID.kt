package org.valkyrienskies.simplici.api.math

import org.joml.*

class Vector3dPID : Vector3d {

    var xPID = PIDController()
    var yPID = PIDController()
    var zPID = PIDController()

    constructor()

    constructor(d: Double) {
        this.x = d
        this.y = d
        this.z = d
    }

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(v: Vector3ic) {
        this.x = v.x().toDouble()
        this.y = v.y().toDouble()
        this.z = v.z().toDouble()
    }

    constructor(v: Vector2fc, z: Double) {
        this.x = v.x().toDouble()
        this.y = v.y().toDouble()
        this.z = z
    }

    constructor(v: Vector3dc) {
        this.x = v.x()
        this.y = v.y()
        this.z = v.z()
    }

    fun updatePID(dt: Double, currentValue: Double, targetValue: Double) {
        xPID.update(dt, currentValue, targetValue)
        yPID.update(dt, currentValue, targetValue)
        zPID.update(dt, currentValue, targetValue)
    }

    fun updateAnglePID(dt: Double, currentAngle: Double, targetAngle: Double) {
        xPID.updateAngle(dt, currentAngle, targetAngle)
        yPID.updateAngle(dt, currentAngle, targetAngle)
        zPID.updateAngle(dt, currentAngle, targetAngle)
    }

    fun setPID(proportionalGain: Double, integralGain: Double, derivativeGain: Double) {
        xPID.setPID(proportionalGain, integralGain, derivativeGain)
        yPID.setPID(proportionalGain, integralGain, derivativeGain)
        zPID.setPID(proportionalGain, integralGain, derivativeGain)
    }

    fun reset() {
        xPID.reset()
        yPID.reset()
        zPID.reset()
    }

}
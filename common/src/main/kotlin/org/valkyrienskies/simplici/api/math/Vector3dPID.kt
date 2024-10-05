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

    fun updatePID(dt: Double, currentValue: Vector3d, targetValue: Vector3d) {
        this.x = xPID.update(dt, currentValue.x, targetValue.x)
        this.y = yPID.update(dt, currentValue.y, targetValue.y)
        this.z = zPID.update(dt, currentValue.z, targetValue.z)
    }

    fun updateAnglePID(dt: Double, currentAngle: Vector3d, targetAngle: Vector3d) {
        this.x = xPID.updateAngle(dt, currentAngle.x, targetAngle.x)
        this.y = yPID.updateAngle(dt, currentAngle.y, targetAngle.y)
        this.z = zPID.updateAngle(dt, currentAngle.z, targetAngle.z)
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
package org.valkyrienskies.simplici.content.ship.modules.wheel

import org.valkyrienskies.simplici.api.util.InterpolationCurve

data class EngineData(
    var maxPower: Double = 10000.0,
    var maxSpeed: Double = 20.0,
    var powerCurve: InterpolationCurve = InterpolationCurve(),
    var isFueled:Boolean = false
) {
    fun getTorqueAtSpeed(speed:Double):Double {
        if(speed >= maxSpeed) return 0.0

        return powerCurve.getValueAtX( speed / maxSpeed ) * maxPower
    }
}

package org.valkyrienskies.simplici.content.ship.modules.thruster

import org.valkyrienskies.simplici.ModConfig.SERVER

object ThrusterSpecs {

    fun getMaxThrustForThruster(type:ThrusterType): Double {
        when (type) {
            ThrusterType.SIMPLE_PROPELLER -> return SERVER.SimplePropellerForce
            ThrusterType.BLAST_PROPELLER -> return SERVER.BlastPropellerForce
            ThrusterType.FIREWORK_THRUSTER -> return SERVER.FireworkThrusterForce
        }
        return SERVER.SimplePropellerForce
    }

}

enum class ThrusterType {
    SIMPLE_PROPELLER,
    BLAST_PROPELLER,
    FIREWORK_THRUSTER
}
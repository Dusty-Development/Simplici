package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.world.level.block.state.BlockState

data class ThrusterForcesData(
    var force:Double = 0.0,
    var maxSpeed:Double = 0.0,
    var softMaxSpeed:Double = 0.0,
    var throttle:Double = 0.0,
    var isFueled:Boolean = false,
    var isReversed:Boolean = false,
    var gimbalAngle:Double = 0.0,
    var state:BlockState
)

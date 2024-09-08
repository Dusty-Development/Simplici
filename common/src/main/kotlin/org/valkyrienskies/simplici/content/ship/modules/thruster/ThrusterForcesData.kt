package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.world.level.block.state.BlockState

data class ThrusterForcesData(
    var force:Double = 0.0,
    var speed:Double = 0.0,
    var throttle:Double = 0.0,
    var isReversed:Boolean = false,
    var hasGimbal:Boolean = false,
    var state:BlockState
)

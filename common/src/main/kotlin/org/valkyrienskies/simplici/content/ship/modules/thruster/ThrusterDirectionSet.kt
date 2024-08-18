package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.core.Direction
import org.joml.Vector3d
import org.valkyrienskies.simplici.api.math.PIDController

data class ThrusterDirectionSet(
    var localDirection: Direction = Direction.UP,   // The direction on the ship
    var globalDirection: Vector3d = Vector3d(),     // The direction in world space
    var currentThrust:Double = 0.0,                 // The desired force in N
    var maxThrust:Double = 0.0,                     // The max force in N that we can use
    var pidController: PIDController = PIDController()
)

package org.valkyrienskies.simplici.content.ship.modules.wheel

import org.joml.Vector3d

class Wheel {

    // Suspension
    var SuspensionMaximumDistance = 0.0
    var SuspensionRestDistance = 0.0
    var SuspensionStifness = 250.0
    var SuspensionDamping = 50.0

    // ParentBody (the rigid body the wheel is on)
    var AttachmentPoint = Vector3d() // In global space
    var AttachmentForward = Vector3d(1.0,0.0,0.0) // In global space
    var AttachmentRight = Vector3d(0.0,0.0,1.0) // In global space
    var AttachmentUp = Vector3d(0.0,1.0,0.0) // In global space

    // Floor
    var FloorFriction = 1.0 // Current friction Coefficient of the floor
    var FloorNormal = Vector3d(0.0,1.0,0.0) // The floors normal in global space

    // Wheel
    var WheelFrictionStatic = 1.0 // Static friction Coefficient of the floor
    var WheelFrictionDynamic = 0.7 // Dynamic friction Coefficient of the floor

    // This is time independent! you should update the wheel before reading values from it but do not expect this to do much of anything!
    fun update() {

    }

    fun getRollingResistanceFrictionCoefficient(speed:Double): Double {
        return 0.25 // TODO: Scale with speed according to https://devforum.roblox.com/t/raycast-vehicle-friction/1239471/2
    }
}
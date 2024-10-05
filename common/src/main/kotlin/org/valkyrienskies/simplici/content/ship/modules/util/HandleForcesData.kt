package org.valkyrienskies.simplici.content.ship.modules.util

import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.simplici.api.math.Vector3dPID

data class HandleForcesData(
    // 'blockPos' is stored elsewhere
    var offset:Vector3d = Vector3d(), // Offset from the block pos (this is on the ship so up is up on the ship but not the world)
    var target:Vector3d = Vector3d(), // Target position in world space
    var targetDirection:Vector3d = Vector3d(),
    var state:BlockState,
    var isCreative:Boolean = false,
    var linearPID: Vector3dPID = Vector3dPID(), // PID controller
    var angularPID: Vector3dPID = Vector3dPID() // PID controller
)

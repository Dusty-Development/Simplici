package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.simplici.content.block.ModBlocks
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelSteeringType

data class WheelForcesData(
    var floorCastDistance:Double = 0.0,
    var wheelRadius:Double = 0.5,
    var restDistance:Double = 0.5,
    var wheelDistanceLimit:Double = 1.0,
    var steeringAngle:Double = 0.0,
    var steeringType: WheelSteeringType = WheelSteeringType.NONE,
    var wheelLocalDirection:Direction = Direction.NORTH,
    var state:BlockState = ModBlocks.SMALL_WHEEL.get().defaultBlockState(),
    var colliding:Boolean = false,
    var floorVel: Vector3d? = null,
    var floorBlockPos: BlockPos? = null,
    var floorFrictionMultiplier: Double = 1.0
)

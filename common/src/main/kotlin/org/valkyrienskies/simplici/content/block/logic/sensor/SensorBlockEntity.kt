package org.valkyrienskies.simplici.content.block.logic.sensor

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import org.joml.Math
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.simplici.content.block.ModBlockEntities

class SensorBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.SENSOR.get(), pos, state)
{

    var lastVal = 0

    private fun basePoint(): Vector3d = blockPos.toJOMLD()
        .add(0.5, 0.5,0.5)
        .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5))

    fun getResult(level: ServerLevel): Int {
        val ship = level.getShipObjectManagingPos(blockPos)

        val startPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.51))
        val endPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(15.0))

        val startPos = ship?.shipToWorld?.transformPosition(startPosShip) ?: startPosShip
        val endPos = ship?.shipToWorld?.transformPosition(endPosShip) ?: endPosShip

        val clipContext = ClipContext( startPos.toMinecraft(), endPos.toMinecraft(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
        var clipResult = level.clipIncludeShips(clipContext, false)
        if (ship != null) {
            clipResult = level.clipIncludeShips(clipContext, false, ship.id)
        }

        if(clipResult.type == HitResult.Type.BLOCK) {
            val hitShip = level.getShipObjectManagingPos(clipResult.blockPos)
            val worldHit = hitShip?.shipToWorld?.transformPosition(clipResult.location.toJOML()) ?: clipResult.location.toJOML()

            val distance:Int = 15 - Math.round((startPos.distance(worldHit)).toFloat())
            return Math.clamp(distance, 0, 15)
        }
        return 0
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as SensorBlockEntity
            if(level.isClientSide)
                return

            be.lastVal = be.getResult(level as ServerLevel)
            level.updateNeighborsAt(pos, state.block)
        }
    }
}
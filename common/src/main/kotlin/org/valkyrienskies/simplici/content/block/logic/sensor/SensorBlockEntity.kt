package org.valkyrienskies.simplici.content.block.logic.sensor

import com.mojang.authlib.minecraft.client.MinecraftClient
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.HitResult
import org.joml.Math
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.mod.common.world.raytraceEntities
import org.valkyrienskies.simplici.content.block.ModBlockEntities

class SensorBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.SENSOR.get(), pos, state)
{

    var lastVal = 0

    fun getMode(level: Level, pos: BlockPos, state: BlockState): SensorMode {
        var range = 5
        var entity = false
        var color: MapColor? = null
        var pos = pos
        val normal = state.getValue(FACING).normal
        var state: BlockState
        var i = 0
        pos = pos
        level.players().forEach {

        }
        while (true) {
            pos = pos.offset(normal)
            level.addParticle(ParticleTypes.END_ROD, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.0, 0.0, 0.0)
            state = level.getBlockState(pos)
            val block = state.block
            when (block) {
                Blocks.GLASS -> range += 5
                Blocks.TINTED_GLASS -> entity = true
                else -> if (state.tags.anyMatch {
                    val name = it.location.toString()
                    for (player in level.players()) {
                    }
                    name.equals("forge:stained_glass") or name.equals("c:stained_glass")
                }) {
                    color = block.defaultMapColor()
                } else {
                    break;
                }
            }
            i++
        }
        return SensorMode(entity, color, range, i)
    }

    fun getResult(level: ServerLevel, mode: SensorMode): Int {
        val ship = level.getShipObjectManagingPos(blockPos)

        val startPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.51 + mode.lensCount))
        val endPosShip = blockPos.toJOMLD().add(0.5,0.5,0.5).add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(mode.dist.toDouble() + mode.lensCount))

        val startPos = ship?.shipToWorld?.transformPosition(startPosShip) ?: startPosShip
        val endPos = ship?.shipToWorld?.transformPosition(endPosShip) ?: endPosShip

        val clipContext = ClipContext( startPos.toMinecraft(), endPos.toMinecraft(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
        var clipResult = level.clipIncludeShips(clipContext, false)
        if (ship != null) {
            clipResult = level.clipIncludeShips(clipContext, false, ship.id)
        }

        if(clipResult.type == HitResult.Type.BLOCK && mode.color == null || mode.color == level.getBlockState(clipResult.blockPos).block.defaultMapColor() ) {
            val hitShip = level.getShipObjectManagingPos(clipResult.blockPos)
            val worldHit = hitShip?.shipToWorld?.transformPosition(clipResult.location.toJOML()) ?: clipResult.location.toJOML()

            val distance:Int = 15 - Math.round((startPos.distance(worldHit)).toFloat()/mode.dist*15)
            return Math.clamp(distance, 0, 15)
        }
        return 0
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as SensorBlockEntity
            if(level.isClientSide)
                return

            be.lastVal = be.getResult(level as ServerLevel, be.getMode(level, pos, state))
            level.updateNeighborsAt(pos, state.block)
        }
    }

    class SensorMode(val entity: Boolean, val color: MapColor?, val dist: Int, val lensCount: Int)
}
package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos

object ThrusterBlockHelper {

    fun onThrusterPlaced(state: BlockState, level: Level, pos: BlockPos) {
        if (level.isClientSide) return
        level as ServerLevel

        val ship: LoadedServerShip? = level.getShipObjectManagingPos(pos)
        if(ship != null) ThrusterControlModule.getOrCreate(ship).addThruster(pos, state)
    }

    fun onThrusterRemoved(level: Level, pos: BlockPos) {
        if (level.isClientSide) return
        level as ServerLevel

        val ship: LoadedServerShip = level.getShipObjectManagingPos(pos) ?: return
        ThrusterControlModule.getOrCreate(ship).removeThruster(pos)
    }

    fun onThrusterUse(state: BlockState, level: Level, pos: BlockPos, mode: ThrusterMode, player: Player) {
        if (level.isClientSide) return
        level as ServerLevel

        player.sendSystemMessage(Component.literal("Changed Gimbal mode to: $mode"))

        val ship: LoadedServerShip = level.getShipObjectManagingPos(pos) ?: return
        ThrusterControlModule.getOrCreate(ship).removeThruster(pos)
        ThrusterControlModule.getOrCreate(ship).addThruster(pos, state, mode)
    }

    fun tickThruster(level: Level, pos: BlockPos, state: BlockState, mode: ThrusterMode) {
        if (level.isClientSide) return
        level as ServerLevel

        val ship: LoadedServerShip = level.getShipObjectManagingPos(pos) ?: return
        val module = ThrusterControlModule.getOrCreate(ship)
        module.addThruster(pos, state, mode)
    }

}
package org.valkyrienskies.simplici.content.block.engine.firework_thruster

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.ship.modules.thruster.ThrusterControlModule
import org.valkyrienskies.mod.common.getShipObjectManagingPos

class FireworkThrusterBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.FIREWORK_THRUSTER.get(), pos, state)

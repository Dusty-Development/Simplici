package org.valkyrienskies.simplici.content.ship.modules.thruster

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.simplici.content.ship.SimpliciShipControl
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.mod.common.util.toJOMLD
import java.util.concurrent.ConcurrentHashMap

class ThrusterControlModule(override val shipControl: SimpliciShipControl) : IShipControlModule {

    val thrusters = ConcurrentHashMap<BlockPos, Pair<BlockState, ThrusterMode>>()

    override fun onPhysTick(physShip: PhysShipImpl) {

        // Loops over all thrusters and updates the max thrust of that direction
        thrusters.forEach {
            // Get the thruster max force
            val thrusterMaxForce = ThrusterSpecs.getMaxThrustForThruster((it.value.first.block as IThrusterBlock).thrusterType)

            val direction = it.value.first.getValue(BlockStateProperties.FACING) // The local thruster direction

            val redstonePower = it.value.first.getValue(BlockStateProperties.POWER).toDouble() // Restone power to the block from 0 -> 15

            // The global force multiplied by the 0 -> 1 redstone power
            val forceGlobal = direction.normal.toJOMLD().mul(redstonePower / 15.0)

            // Applies the force to the position
            val pos = it.key.toJOMLD().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            if(forceGlobal.mul(thrusterMaxForce, Vector3d()).isFinite) {
                when (it.value.second) {
                    ThrusterMode.STATIC -> physShip.applyRotDependentForceToPos(forceGlobal.mul(thrusterMaxForce), pos)
                    ThrusterMode.DYNAMIC -> physShip.applyInvariantForceToPos(forceGlobal.mul(thrusterMaxForce), pos)
                }
            }
        }

    }

    override fun onTick() { }

    fun addThruster(pos: BlockPos, state: BlockState) { thrusters.putIfAbsent(pos, Pair(state, ThrusterMode.STATIC)) }
    fun addThruster(pos: BlockPos, state: BlockState, mode: ThrusterMode) { thrusters.putIfAbsent(pos, Pair(state, mode)) }
    fun removeThruster(pos: BlockPos) { thrusters.remove(pos) }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterControlModule {
            val control: SimpliciShipControl = ship.getAttachment<SimpliciShipControl>() ?: SimpliciShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is ThrusterControlModule) return it }
            val module = ThrusterControlModule(control)
            control.loadedModules.add(module)
            return module
        }
    }
}
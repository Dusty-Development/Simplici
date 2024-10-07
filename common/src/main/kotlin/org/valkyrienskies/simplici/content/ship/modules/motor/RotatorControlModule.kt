package org.valkyrienskies.simplici.content.ship.modules.motor

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import org.joml.Math
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.simplici.content.ship.ModShipControl
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.gamerule.ModGamerules
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

class RotatorControlModule(override val shipControl: ModShipControl) : IShipControlModule {

    private val spinners = ConcurrentHashMap<BlockPos, Pair<BlockState, Boolean>>()

    fun addSpinner(pos: BlockPos, state: BlockState, isInverted: Boolean) {
        spinners.put(pos, Pair(state, isInverted))
    }
    fun removeSpinner(pos: BlockPos) {
        spinners.remove(pos)
    }

    override fun onPhysTick(physShip: PhysShipImpl) {
        val mass = physShip.inertia.shipMass
        val omega = physShip.poseVel.omega
        val vel = physShip.poseVel.vel

        spinners.forEach {
            val torque = it.value.first.getValue(FACING).normal.toJOMLD().mul(it.value.first.getValue(BlockStateProperties.POWER).toDouble(), Vector3d())

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            val rotationAxis = torqueGlobal.normalize(Vector3d())

            val inversionMultiplier = if(it.value.second) 1.0 else -1.0

            val rpm = omega.mul(rotationAxis, Vector3d()).length() * 10
            val powerAtRpm = calculateTorqueFromRpm(rpm)

            if (!powerAtRpm.isNaN() && !inversionMultiplier.isNaN()){
                physShip.applyInvariantTorque(torqueGlobal.mul(powerAtRpm * inversionMultiplier)) // replace with curve
            }
        }
    }

    private fun calculateTorqueFromRpm(rpm:Double) : Double {
        return Math.clamp(0.0, shipControl.gameRules!!.getInt(ModGamerules.ROTATOR_TORQUE).toDouble(), ((1 - ((rpm / shipControl.gameRules!!.getInt(ModGamerules.ROTATOR_RPM).toDouble()).pow(shipControl.gameRules!!.getInt(ModGamerules.ROTATOR_FALL_OFF).toDouble() * 0.01))) * shipControl.gameRules!!.getInt(ModGamerules.ROTATOR_TORQUE).toDouble()))
    }

    override fun onTick() { }

    companion object {
        fun getOrCreate(ship: ServerShip): RotatorControlModule {
            val control: ModShipControl = ship.getAttachment<ModShipControl>() ?: ModShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is RotatorControlModule) return it }
            val module = RotatorControlModule(control)
            control.loadedModules.add(module)
            return module
        }

    }
}
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

class ThrusterControlModule(override val shipControl: SimpliciShipControl) : IShipControlModule {

    val thrusterDirectonSets:HashMap<Direction, ThrusterDirectionSet> = hashMapOf(
        Pair(Direction.UP, ThrusterDirectionSet(Direction.UP )),
        Pair(Direction.DOWN, ThrusterDirectionSet(Direction.DOWN )),
        Pair(Direction.NORTH, ThrusterDirectionSet(Direction.NORTH)),
        Pair(Direction.SOUTH, ThrusterDirectionSet(Direction.SOUTH)),
        Pair(Direction.EAST, ThrusterDirectionSet(Direction.EAST )),
        Pair(Direction.WEST, ThrusterDirectionSet(Direction.WEST ))
    )

    val thrusters = HashMap<BlockPos, BlockState>()

    override fun onPhysTick(physShip: PhysShipImpl) {

        // Loops over the 6 sets and resets last ticks values
        thrusterDirectonSets.forEach {
            val set = it.value

            // Reset so thrust doesn't accumulate over multiple frames
            set.maxThrust = 0.0
            set.currentThrust = 0.0

            // Rotate current thrust to be global
            set.globalDirection = physShip.transform.shipToWorldRotation.transform(set.localDirection.normal.toJOMLD(), Vector3d())
        }

        // Loops over all thrusters and updates the max thrust of that direction
        thrusters.forEach {
            // Get the thruster max force
            val thrusterMaxForce = ThrusterSpecs.getMaxThrustForThruster((it.value.block as IThrusterBlock).thrusterType)

            val direction = it.value.getValue(BlockStateProperties.FACING) // The local thruster direction
            val set = thrusterDirectonSets[direction]!! // The current thruster set

            val redstonePower = it.value.getValue(BlockStateProperties.POWER).toDouble() // Restone power to the block from 0 -> 15

            // The global force multiplied by the 0 -> 1 redstone power
            val forceGlobal = set.localDirection.normal.toJOMLD().mul(redstonePower / 15.0)

            // Applies the force to the position
            val pos = it.key.toJOMLD().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            if(forceGlobal.mul(thrusterMaxForce, Vector3d()).isFinite) {
                physShip.applyRotDependentForceToPos(forceGlobal.mul(thrusterMaxForce), pos)
            }
        }

        // Calculate damping and control values, after witch apply the desired forces
        thrusterDirectonSets.forEach {
            val direction = it.key
            val set = it.value

            // Counteract the force needed to remove gravity
            if(shipControl.shouldResistLinear) {
                calculateGravityThrusterSetCurrentThrust(set, physShip)
                calculateVelocityThrusterSetCurrentThrust(set, physShip)
            }

            // Clamp current thrust to max thrust
            set.currentThrust = Mth.clamp(set.currentThrust, 0.0, set.maxThrust)

            // Apply forces to physShip
            if(set.globalDirection.mul(set.currentThrust, Vector3d()).isFinite) {
                physShip.applyInvariantForce(set.globalDirection.mul(set.currentThrust))
            }
        }

    }

    override fun onTick() { }

    fun addThruster(pos: BlockPos, state: BlockState) { thrusters.putIfAbsent(pos, state) }
    fun removeThruster(pos: BlockPos) { thrusters.remove(pos) }


    fun calculateGravityThrusterSetCurrentThrust(set: ThrusterDirectionSet, physShip: PhysShipImpl) {
        // gravity direction
        val gravityDir = shipControl.currentGravity.normalize(Vector3d())

        // A multiplier defining how much force needs to be used to counteract gravity
        val powerMultiplier:Double = Mth.clamp(set.globalDirection.dot(gravityDir.mul(-1.0)), 0.0, 1.0)

        // The force of gravity that these thrusters need to counteract
        val resistForce = (shipControl.currentGravity.length() * physShip.inertia.shipMass) * powerMultiplier

        // Set the current thrust to counteract it
        set.currentThrust += resistForce
    }

    fun calculateVelocityThrusterSetCurrentThrust(set: ThrusterDirectionSet, physShip: PhysShipImpl) {
        // velocity direction
        val velocityDir = physShip.poseVel.vel.normalize(Vector3d())

        val powerMultiplier:Double = Mth.clamp(set.globalDirection.dot(velocityDir.mul(-1.0)), 0.0, 1.0)
        val resistForce = (physShip.poseVel.vel.length() * physShip.inertia.shipMass) * powerMultiplier

        // Set the current thrust to counteract it
        set.currentThrust += resistForce
    }

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
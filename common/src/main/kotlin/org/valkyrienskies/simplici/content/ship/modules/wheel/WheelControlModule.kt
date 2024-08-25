package org.valkyrienskies.simplici.content.ship.modules.wheel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.api.math.SpringHelper
import org.valkyrienskies.simplici.content.ship.IShipControlModule
import org.valkyrienskies.simplici.content.ship.SimpliciShipControl
import java.util.concurrent.ConcurrentHashMap


class WheelControlModule(override val shipControl: SimpliciShipControl) : IShipControlModule {

    private val wheels = ConcurrentHashMap<BlockPos, WheelForcesData>()
    private val engines = ConcurrentHashMap<BlockPos, EngineData>()


    fun addOrUpdateWheel(pos: BlockPos, wheelForcesData: WheelForcesData) {
        wheels.put(pos, wheelForcesData)
    }
    fun removeWheel(pos: BlockPos) {
        wheels.remove(pos)
    }

    fun addOrUpdateEngine(pos: BlockPos, engineData: EngineData) {
        engines.put(pos, engineData)
    }
    fun removeEngine(pos: BlockPos) {
        engines.remove(pos)
    }

    override fun onTick() {
    }

    override fun onPhysTick(physShip: PhysShipImpl) {
        wheels.forEach {
            calculateSuspension(physShip,it.key,it.value)
            calculateSliding(physShip,it.key,it.value)
        }
    }

    private fun calculateSuspension(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val shipUpVector = physShip.transform.transformDirectionNoScalingFromWorldToShip(Vector3d(0.0,1.0,0.0), Vector3d()) // This is for the ship in world space
        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        val localVelocity = -velocity.y() //-velocity.mul(shipUpVector, Vector3d()).length() // <---- GOIN WRONG HERE THIS IS WRONG

        val offset = wheelData.floorCastDistance - wheelData.restDistance
        val spring = (-SpringHelper.calculateSpringForceDouble(offset, localVelocity, 25.0, 10.0))

        val force = Vector3d(0.0, spring, 0.0).mul(physShip.inertia.shipMass / wheels.size)
        if(wheelData.colliding) physShip.applyInvariantForceToPos(force, wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }

    private fun calculateSliding(physShip: PhysShipImpl, wheelBlockPos:BlockPos, wheelData:WheelForcesData) {
        if(!wheelData.colliding) return
        val worldBlockPos = physShip.transform.shipToWorld.transformPosition(wheelBlockPos.center.toJOML())
        val direction = wheelData.state.getValue(BlockStateProperties.FACING).clockWise.normal.toJOMLD()
        val globalDir = physShip.transform.transformDirectionNoScalingFromShipToWorld(direction, Vector3d()).normalize()
        val globalDirWithSteering = globalDir.rotateY(wheelData.steeringAngle, Vector3d())
        val velocity = pointVelocity(physShip, worldBlockPos) // Global velocity
        var floorVelocity = wheelData.floorVel
        if(wheelData.floorVel == null) floorVelocity = Vector3d()

        val slidingVelocity = velocity.sub(floorVelocity, Vector3d()).dot(globalDirWithSteering)

        val force = -slidingVelocity * 1.0
        if(wheelData.colliding) physShip.applyInvariantForceToPos(globalDir.mul(force, Vector3d()).mul(physShip.inertia.shipMass / wheels.size), wheelBlockPos.center.toJOML().sub(physShip.transform.positionInShip))
    }

    private fun pointVelocity(physShip: PhysShipImpl, worldPointPosition: Vector3dc): Vector3dc {
        val centerOfMassPos = worldPointPosition.sub(physShip.transform.positionInWorld, Vector3d())
        return physShip.poseVel.vel.add(physShip.poseVel.omega.cross(centerOfMassPos, Vector3d()), Vector3d())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): WheelControlModule {
            val control: SimpliciShipControl = ship.getAttachment<SimpliciShipControl>() ?: SimpliciShipControl().also { ship.saveAttachment(it) }
            control.loadedModules.forEach { if(it is WheelControlModule) return it }
            val module = WheelControlModule(control)
            control.loadedModules.add(module)
            return module
        }

    }
}
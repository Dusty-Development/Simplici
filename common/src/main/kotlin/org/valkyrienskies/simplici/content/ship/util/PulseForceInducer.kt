package org.valkyrienskies.simplici.content.ship.util

import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.simplici.content.ship.ModShipControl
import java.util.concurrent.CopyOnWriteArrayList

class PulseForceInducer : ShipForcesInducer {

    private val linearPosPulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()
    private val linearPulses = CopyOnWriteArrayList<Vector3d>()
    private val angularPulses = CopyOnWriteArrayList<Vector3d>()

    fun addLinearPulse(pos: Vector3d, force: Vector3d) { linearPosPulses.add(pos to force) }
    fun addLinearPulse(force: Vector3d) { linearPulses.add(force) }
    fun addAngularPulse(torque: Vector3d) { angularPulses.add(torque) }

    override fun applyForces(physShip: PhysShip) {
        linearPosPulses.forEach {
            val (pos, force) = it
            val tPos = Vector3d(pos).sub(Vector3d(physShip.transform.positionInShip))
            val tForce = Vector3d(physShip.transform.worldToShip.transformDirection(force))
            physShip.applyRotDependentForceToPos(tForce, tPos)
        }
        linearPosPulses.clear()

        linearPulses.forEach(physShip::applyInvariantForce)
        linearPulses.clear()

        angularPulses.forEach(physShip::applyInvariantTorque)
        angularPulses.clear()
    }

}
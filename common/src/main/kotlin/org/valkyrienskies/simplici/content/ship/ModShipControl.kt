package org.valkyrienskies.simplici.content.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameRules
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import java.util.concurrent.CopyOnWriteArrayList


// Hey person! if your wondering why this is overly complex then yell at dust-
// she wrote this code for a completely other project themed around space- and
// then decided to use it for this.

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
class ModShipControl : ShipForcesInducer, ServerTickListener {

    // Settings
    @JsonIgnore var gameRules: GameRules? = null

    // Control
    @JsonIgnore var seatedPlayer: Player? = null
    @JsonIgnore var controlSeat: ShipMountingEntity? = null
    @JsonIgnore var currentControlData:ShipControlData? = null
    var controlSeatCount = 0
    @JsonIgnore var isControlled = true

    // Damping
    @JsonIgnore var shouldResistLinear = true
    @JsonIgnore var shouldResistAngular = true

    // Physics
    @JsonIgnore internal var ship: ServerShip? = null
    @JsonIgnore var currentGravity = DEFAULT_GRAVITY

    // Module management
    @JsonIgnore val loadedModules = CopyOnWriteArrayList<IShipControlModule>()

    // Every physics tick:
    // - find any controlling players
    // - tick all the modules
    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        loadedModules.forEach {
            it.onPhysTick(physShip)
        }

        getControlData()
    }

    // Every server tick:
    // - tick all modules
    override fun onServerTick() {
        loadedModules.forEach {
            it.onTick()
        }
    }

    fun setRules(rules: GameRules) { gameRules = rules }

    private fun getControlData(): ShipControlData? {
        val controllingPlayer = ship?.getAttachment(SeatedControllingPlayer::class.java)

        if (controllingPlayer != null) {
            val player = controllingPlayer!!

            isControlled = true
            currentControlData = ShipControlData.create(player)
            return currentControlData
        }

        isControlled = false
        currentControlData = null
        return null
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ModShipControl {
            return ship.getAttachment<ModShipControl>()
                ?: ModShipControl().also { ship.saveAttachment(it) }
        }

        private val DEFAULT_GRAVITY = Vector3d(0.0, -10.0, 0.0)
    }
}

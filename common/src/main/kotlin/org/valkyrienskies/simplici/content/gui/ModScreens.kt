package org.valkyrienskies.simplici.content.gui

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.gui.fuel.consumer.FuelConsumerMenu
import org.valkyrienskies.simplici.content.gui.fuel.tank.FuelTankMenu
import org.valkyrienskies.simplici.registry.DeferredRegister

private typealias HFactory<T> = (syncId: Int, playerInv: Inventory) -> T

@Suppress("unused")
object ModScreens {
    private val SCREENS = DeferredRegister.create(Simplici.MOD_ID, Registries.MENU)

    val FUEL_CONSUMER = FuelConsumerMenu.factory withName "fuel_consumer"
    val FUEL_TANK = FuelTankMenu.factory withName "fuel_tank"
//    val PROPELLER = FuelConsumerMenu.factory withName "propeller"
//    val ENGINE = FuelConsumerMenu.factory withName "engine"

    fun register() {
        SCREENS.applyAll()
    }

    private infix fun <T : AbstractContainerMenu> HFactory<T>.withName(name: String) =
        SCREENS.register(name) { MenuType(this, FeatureFlags.VANILLA_SET) }
}

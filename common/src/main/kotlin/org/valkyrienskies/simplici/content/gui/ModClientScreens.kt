package org.valkyrienskies.simplici.content.gui

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.valkyrienskies.simplici.registry.RegistrySupplier

private typealias SFactory<T> = (handler: T, playerInv: Inventory, text: Component) -> AbstractContainerScreen<T>

private data class ClientScreenRegistar<T : AbstractContainerMenu>(
    val type: RegistrySupplier<MenuType<T>>,
    val factory: SFactory<T>
) {
    fun register() = MenuScreens.register(type.get(), factory)
}

object ModClientScreens {
    private val SCREENS_CLIENT = mutableListOf<ClientScreenRegistar<*>>()

    init {
//        SHIP_HELM withScreen ::ShipHelmScreen
//        ENGINE withScreen ::EngineScreen
    }

    fun register() {
        SCREENS_CLIENT.forEach { it.register() }
    }

    private infix fun <T : AbstractContainerMenu> RegistrySupplier<MenuType<T>>.withScreen(screen: SFactory<T>) =
        SCREENS_CLIENT.add(ClientScreenRegistar(this, screen))
}

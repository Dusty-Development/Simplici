package org.valkyrienskies.simplici.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T
}

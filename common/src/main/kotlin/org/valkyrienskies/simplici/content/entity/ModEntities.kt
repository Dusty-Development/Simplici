package org.valkyrienskies.simplici.content.entity

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntity
import org.valkyrienskies.simplici.content.entity.rope.RopeSegmentEntityRenderer
import org.valkyrienskies.simplici.registry.DeferredRegister
import org.valkyrienskies.simplici.registry.RegistrySupplier

private typealias EFactory<T> = (EntityType<T>, Level) -> T
private typealias RFactory<T> = EntityRendererProvider<T>

private data class ToRegEntityRenderer<T : Entity>(
    val type: RegistrySupplier<EntityType<T>>,
    val renderer: RFactory<in T>
) {
    fun register() =
        EntityRenderers.register(type.get(), renderer)
}

object ModEntities {
    private val ENTITIES = DeferredRegister.create(Simplici.MOD_ID, Registries.ENTITY_TYPE)
    private val ENTITY_RENDERERS = mutableListOf<ToRegEntityRenderer<*>>()

    val ROPE_SEGMENT = "rope_segment" withType ::RopeSegmentEntity registerRenderer ::RopeSegmentEntityRenderer

    fun register() { ENTITIES.applyAll() }
    @JvmStatic fun registerRenderers() = ENTITY_RENDERERS.forEach { it.register() }

    private infix fun <T: Entity>String.withType(entityConstructor: (type: EntityType<T>, level: Level) -> T): RegistrySupplier<EntityType<T>> {
        return ENTITIES.register(this) {
            EntityType.Builder.of(
                entityConstructor,
                MobCategory.MISC
            )
                .sized(.25f, .25f)
                .updateInterval(1)
                .fireImmune()
                .build(ResourceLocation(Simplici.MOD_ID, this).toString())
        }
    }

    private infix fun <T : Entity> RegistrySupplier<EntityType<T>>.registerRenderer(factory: RFactory<in T>) = this.apply { ENTITY_RENDERERS += ToRegEntityRenderer(this, factory) }

}
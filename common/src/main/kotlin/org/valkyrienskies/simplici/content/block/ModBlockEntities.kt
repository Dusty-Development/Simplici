package org.valkyrienskies.simplici.content.block

import net.minecraft.Util
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.block.control.driver_seat.DriverSeatBlockEntity
import org.valkyrienskies.simplici.content.block.engine.firework_thruster.FireworkThrusterBlockEntity
import org.valkyrienskies.simplici.content.block.engine.simple_propeller.SimplePropellerBlockEntity
import org.valkyrienskies.simplici.content.block.engine.blast_propeller.BlastPropellerBlockEntity
import org.valkyrienskies.simplici.content.block.engine.blast_propeller.BlastPropellerBlockEntityRenderer
import org.valkyrienskies.simplici.content.block.engine.simple_propeller.SimplePropellerBlockEntityRenderer
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.rotator.RotatorBlockEntity
import org.valkyrienskies.simplici.content.render.ModModels
import org.valkyrienskies.simplici.registry.DeferredRegister
import org.valkyrienskies.simplici.registry.RegistrySupplier

@Suppress("unused")
object ModBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(Simplici.MOD_ID, Registries.BLOCK_ENTITY_TYPE)
    private val BlockEntityRenderers = mutableListOf<RendererEntry<*>>()

    val DRIVER_SEAT : RegistrySupplier<BlockEntityType<DriverSeatBlockEntity>>

    val SIMPLE_PROPELLER : RegistrySupplier<BlockEntityType<SimplePropellerBlockEntity>>
    val BLAST_PROPELLER : RegistrySupplier<BlockEntityType<BlastPropellerBlockEntity>>
    val FIREWORK_THRUSTER : RegistrySupplier<BlockEntityType<FireworkThrusterBlockEntity>>

    val HINGE : RegistrySupplier<BlockEntityType<HingeBlockEntity>>
    val ROTATOR : RegistrySupplier<BlockEntityType<RotatorBlockEntity>>

    init {

        DRIVER_SEAT = ModBlocks.CONTROL_PANEL
            .withBE(::DriverSeatBlockEntity)
            .byName("driver_seat")

        SIMPLE_PROPELLER = ModBlocks.SIMPLE_PROPELLER
            .withBE(::SimplePropellerBlockEntity)
            .byName("simple_propeller")
            .withRenderer {
                SimplePropellerBlockEntityRenderer(
                    ModModels.PROPELLER
                )
            }
        BLAST_PROPELLER = ModBlocks.BLAST_PROPELLER
            .withBE(::BlastPropellerBlockEntity)
            .byName("blast_propeller")
            .withRenderer {
                BlastPropellerBlockEntityRenderer(
                    ModModels.PROPELLER
                )
            }
        FIREWORK_THRUSTER = ModBlocks.FIREWORK_THRUSTER
            .withBE(::FireworkThrusterBlockEntity)
            .byName("firework_thruster")

        HINGE = ModBlocks.HINGE
            .withBE(::HingeBlockEntity)
            .byName("hinge")
        ROTATOR = ModBlocks.ROTATOR
                .withBE(::RotatorBlockEntity)
            .byName("rotator")
    }

    fun register() {
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)

    private data class RendererEntry<T: BlockEntity>(
        val type: RegistrySupplier<BlockEntityType<T>>,
        val renderer: () -> Any
    )

    fun initClientRenderers(clientRenderers: Simplici.ClientRenderers) {
        BlockEntityRenderers.forEach { x ->
            val rp = BlockEntityRendererProvider {
                x.renderer() as BlockEntityRenderer<BlockEntity>
            }
            clientRenderers.registerBlockEntityRenderer(
                x.type.get() as BlockEntityType<BlockEntity>,
                rp
            )
        }
    }

    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }

    private infix fun <T : BlockEntity> RegistrySupplier<BlockEntityType<T>>.withRenderer(renderer: () -> Any) =
        this.also {
            BlockEntityRenderers += RendererEntry(it, renderer)
        }
}

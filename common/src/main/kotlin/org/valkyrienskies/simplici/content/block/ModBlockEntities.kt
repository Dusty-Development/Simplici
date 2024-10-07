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
import org.valkyrienskies.simplici.content.block.engine.car.race.RaceEngineBlockEntity
import org.valkyrienskies.simplici.content.block.engine.car.steam.SteamEngineBlockEntity
import org.valkyrienskies.simplici.content.block.engine.car.tractor.TractorEngineBlockEntity
import org.valkyrienskies.simplici.content.block.engine.thruster.firework_thruster.FireworkThrusterBlockEntity
import org.valkyrienskies.simplici.content.block.engine.thruster.propeller.PropellerBlockEntityRenderer
import org.valkyrienskies.simplici.content.block.engine.thruster.propeller.blast_propeller.BlastPropellerBlockEntity
import org.valkyrienskies.simplici.content.block.engine.thruster.propeller.simple_propeller.SimplePropellerBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.ball_hinge.BallHingeBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.head.MechanicalHeadBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.head.MechanicalHeadBlockEntityRenderer
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.hydraulic.HydraulicBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.rotator.RotatorBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntityRenderer
import org.valkyrienskies.simplici.content.block.mechanical.wheel.large_wheel.LargeWheelBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.wheel.medium_wheel.MediumWheelBlockEntity
import org.valkyrienskies.simplici.content.block.mechanical.wheel.small_wheel.SmallWheelBlockEntity
import org.valkyrienskies.simplici.content.block.tool.fuel_tank.FuelTankBlockEntity
import org.valkyrienskies.simplici.content.block.tool.rope_hook.handle.HandleBlockEntity
import org.valkyrienskies.simplici.content.block.tool.sensor.SensorBlockEntity
import org.valkyrienskies.simplici.content.render.ModModels
import org.valkyrienskies.simplici.registry.DeferredRegister
import org.valkyrienskies.simplici.registry.RegistrySupplier

@Suppress("unused")
object ModBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(Simplici.MOD_ID, Registries.BLOCK_ENTITY_TYPE)
    private val BlockEntityRenderers = mutableListOf<RendererEntry<*>>()

    val DRIVER_SEAT = ModBlocks.DRIVER_SEAT.withBE(::DriverSeatBlockEntity).byName("driver_seat")

    val SIMPLE_PROPELLER  = ModBlocks.SIMPLE_PROPELLER.withBE(::SimplePropellerBlockEntity).byName("simple_propeller").withRenderer { PropellerBlockEntityRenderer( ModModels.PROPELLER ) }
    val BLAST_PROPELLER  = ModBlocks.BLAST_PROPELLER.withBE(::BlastPropellerBlockEntity).byName("blast_propeller").withRenderer { PropellerBlockEntityRenderer( ModModels.PROPELLER ) }
    val FIREWORK_THRUSTER = ModBlocks.FIREWORK_THRUSTER.withBE(::FireworkThrusterBlockEntity).byName("firework_thruster")

    val SMALL_WHEEL = ModBlocks.SMALL_WHEEL.withBE(::SmallWheelBlockEntity).byName("small_wheel").withRenderer { WheelBlockEntityRenderer( ModModels.SMALL_WHEEL ) }
    val MEDIUM_WHEEL = ModBlocks.MEDIUM_WHEEL.withBE(::MediumWheelBlockEntity).byName("medium_wheel").withRenderer { WheelBlockEntityRenderer( ModModels.MEDIUM_WHEEL ) }
    val LARGE_WHEEL = ModBlocks.LARGE_WHEEL.withBE(::LargeWheelBlockEntity).byName("large_wheel").withRenderer { WheelBlockEntityRenderer( ModModels.LARGE_WHEEL ) }

//    val ELECTRIC_ENGINE = ModBlocks.ELECTRIC_ENGINE.withBE(::ElectricEngineBlockEntity).byName("electric_engine")
    val RACE_ENGINE = ModBlocks.RACE_ENGINE.withBE(::RaceEngineBlockEntity).byName("race_engine")
    val STEAM_ENGINE = ModBlocks.STEAM_ENGINE.withBE(::SteamEngineBlockEntity).byName("steam_engine")
    val TRACTOR_ENGINE = ModBlocks.TRACTOR_ENGINE.withBE(::TractorEngineBlockEntity).byName("tractor_engine")
//    val TRUCK_ENGINE = ModBlocks.TRUCK_ENGINE.withBE(::TruckEngineBlockEntity).byName("truck_engine")

    val MECHANICAL_HEAD = ModBlocks.MECHANICAL_HEAD.withBE(::MechanicalHeadBlockEntity).byName("mechanical_head").withRenderer { MechanicalHeadBlockEntityRenderer( ModModels.MECHANICAL_BEAM ) }
    val HYDRAULIC = ModBlocks.HYDRAULIC.withBE(::HydraulicBlockEntity).byName("hydraulic")
    val HINGE = ModBlocks.HINGE.withBE(::HingeBlockEntity).byName("hinge")
    val ROTATOR = ModBlocks.ROTATOR.withBE(::RotatorBlockEntity).byName("rotator")
    val BALL_HINGE = ModBlocks.BALL_HINGE.withBE(::BallHingeBlockEntity).byName("ball_hinge")

    val HANDLE = ModBlocks.HANDLE.withBE(::HandleBlockEntity).byName("handle") // TODO: add a rope knot renderer

    val SENSOR = ModBlocks.SENSOR.withBE(::SensorBlockEntity).byName("sensor")
    val FUEL_TANK = ModBlocks.FUEL_TANK.withBE(::FuelTankBlockEntity).byName("fuel_tank")

    fun register() = BLOCKENTITIES.applyAll()

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)
    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(setOf(this), blockEntity)
    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)

    // Block Entity renderers built off of vs-Tournament

    private data class RendererEntry<T: BlockEntity>(
        val type: RegistrySupplier<BlockEntityType<T>>,
        val renderer: () -> Any
    )

    fun initClientRenderers(clientRenderers: Simplici.ClientRenderers) {
        BlockEntityRenderers.forEach { blockEntityRendererEntry ->
            val rendererProvider = BlockEntityRendererProvider { blockEntityRendererEntry.renderer() as BlockEntityRenderer<BlockEntity> }
            clientRenderers.registerBlockEntityRenderer( blockEntityRendererEntry.type.get() as BlockEntityType<BlockEntity>, rendererProvider )
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

    private infix fun <T : BlockEntity> RegistrySupplier<BlockEntityType<T>>.withRenderer(renderer: () -> Any) = this.also { BlockEntityRenderers += RendererEntry(it, renderer) }
}

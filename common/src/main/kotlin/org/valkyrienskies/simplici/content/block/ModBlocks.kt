package org.valkyrienskies.simplici.content.block

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.block.control.driver_seat.DriverSeatBlock
import org.valkyrienskies.simplici.content.block.engine.car.race.RaceEngineBlock
import org.valkyrienskies.simplici.content.block.engine.car.tractor.TractorEngineBlock
import org.valkyrienskies.simplici.content.block.engine.propeller.blast_propeller.BlastPropellerBlock
import org.valkyrienskies.simplici.content.block.engine.firework_thruster.FireworkThrusterBlock
import org.valkyrienskies.simplici.content.block.engine.propeller.simple_propeller.SimplePropellerBlock
import org.valkyrienskies.simplici.content.block.mechanical.wheel.small_wheel.SmallWheelBlock
import org.valkyrienskies.simplici.content.block.logic.sensor.SensorBlock
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeBlock
import org.valkyrienskies.simplici.content.block.mechanical.MechanicalHeadBlock
import org.valkyrienskies.simplici.content.block.mechanical.rotator.RotatorBlock
import org.valkyrienskies.simplici.content.block.mechanical.wheel.large_wheel.LargeWheelBlock
import org.valkyrienskies.simplici.content.block.mechanical.wheel.medium_wheel.MediumWheelBlock
import org.valkyrienskies.simplici.registry.DeferredRegister

@Suppress("unused")
object ModBlocks {
    internal val BLOCKS = DeferredRegister.create(Simplici.MOD_ID, Registries.BLOCK)

    val DRIVER_SEAT = BLOCKS.register("driver_seat", ::DriverSeatBlock)
    val PILOT_SEAT = BLOCKS.register("pilot_seat", ::DriverSeatBlock)
    val PASSENGER_SEAT = BLOCKS.register("passenger_seat", ::DriverSeatBlock)

    val SIMPLE_PROPELLER = BLOCKS.register("simple_propeller", ::SimplePropellerBlock)
    val BLAST_PROPELLER = BLOCKS.register("blast_propeller", ::BlastPropellerBlock)
    val FIREWORK_THRUSTER = BLOCKS.register("firework_thruster", ::FireworkThrusterBlock)

    val SMALL_WHEEL = BLOCKS.register("small_wheel", ::SmallWheelBlock)
    val MEDIUM_WHEEL = BLOCKS.register("medium_wheel", ::MediumWheelBlock)
    val LARGE_WHEEL = BLOCKS.register("large_wheel", ::LargeWheelBlock)

    val TRACTOR_ENGINE = BLOCKS.register("tractor_engine", ::TractorEngineBlock)
    val RACE_ENGINE = BLOCKS.register("race_engine", ::RaceEngineBlock)

    val MECHANICAL_HEAD = BLOCKS.register("mechanical_head", ::MechanicalHeadBlock)
    val HINGE = BLOCKS.register("hinge", ::HingeBlock)
    val ROTATOR = BLOCKS.register("rotator", ::RotatorBlock)

    val SENSOR = BLOCKS.register("sensor", ::SensorBlock)

    fun register() {
        BLOCKS.applyAll()
    }

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        println(BLOCKS)
        BLOCKS.forEach {
            //so "it" is null?
//            if (it.get() !is NoBlockItem) {
//                if(it is NoCreativeTab) items.register(it.name) { NoTabBlockItem(it.get(), Item.Properties()) }
//                else items.register(it.name) { BlockItem(it.get(), Item.Properties()) }
//            }
            items.register(it.name) { BlockItem(it.get(), Item.Properties()) }
        }
    }
}

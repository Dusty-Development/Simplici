package org.valkyrienskies.simplici.content.block

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.content.block.control.driver_seat.DriverSeatBlock
import org.valkyrienskies.simplici.content.block.engine.blast_propeller.BlastPropellerBlock
import org.valkyrienskies.simplici.content.block.engine.firework_thruster.FireworkThrusterBlock
import org.valkyrienskies.simplici.content.block.engine.simple_propeller.SimplePropellerBlock
import org.valkyrienskies.simplici.content.block.mechanical.hinge.HingeBlock
import org.valkyrienskies.simplici.content.block.mechanical.rotator.RotatorBlock
import org.valkyrienskies.simplici.registry.DeferredRegister

@Suppress("unused")
object ModBlocks {
    internal val BLOCKS = DeferredRegister.create(Simplici.MOD_ID, Registries.BLOCK)

    val CONTROL_PANEL = BLOCKS.register("control_panel", ::DriverSeatBlock)

    val SIMPLE_PROPELLER = BLOCKS.register("simple_propeller", ::SimplePropellerBlock)
    val BLAST_PROPELLER = BLOCKS.register("blast_propeller", ::BlastPropellerBlock)
    val FIREWORK_THRUSTER = BLOCKS.register("firework_thruster", ::FireworkThrusterBlock)

    val HINGE = BLOCKS.register("hinge", ::HingeBlock)
    val ROTATOR = BLOCKS.register("rotator", ::RotatorBlock)

    fun register() {
        BLOCKS.applyAll()
    }

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties()) }
        }
    }
}

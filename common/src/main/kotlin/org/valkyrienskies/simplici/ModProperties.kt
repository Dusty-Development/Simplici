package org.valkyrienskies.simplici

import net.minecraft.world.level.block.state.properties.IntegerProperty

object ModProperties {
    val HEAT = IntegerProperty.create("heat", 0, 32)
    val FORCE = IntegerProperty.create("force", 0, 100)
}

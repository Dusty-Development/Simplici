package org.valkyrienskies.simplici.api.extension

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i

fun BlockPos.intPos(): Vec3i = Vec3i(x,y,z)
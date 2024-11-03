package org.valkyrienskies.simplici.content.network.s2c

import dev.architectury.networking.NetworkManager.PacketContext
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import org.valkyrienskies.simplici.content.block.mechanical.wheel.WheelBlockEntity
import java.util.function.Supplier


class WheelDataPacket(val pos: BlockPos, val wheelSteeringAngle: Double, val wheelRotSpeed: Double) {

    constructor(buf: FriendlyByteBuf) : this(buf.readBlockPos(), buf.readDouble(), buf.readDouble())

    fun encode(buf: FriendlyByteBuf?) {
        // Encode data into the buf
        buf?.writeBlockPos(pos)
        buf?.writeDouble(wheelSteeringAngle)
        buf?.writeDouble(wheelRotSpeed)
    }

    fun apply(contextSupplier: Supplier<PacketContext?>?) {
        // On receive

        val level:ClientLevel? = Minecraft.getInstance().level
        if (level == null) return

        val be:WheelBlockEntity = level.getBlockEntity(pos) as WheelBlockEntity
        be.wheelData.steeringAngle = wheelSteeringAngle
    }

}
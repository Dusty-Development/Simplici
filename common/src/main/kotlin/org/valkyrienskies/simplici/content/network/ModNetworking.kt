package org.valkyrienskies.simplici.content.network

import dev.architectury.networking.NetworkChannel
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.simplici.Simplici


object ModNetworking {

//    val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(Simplici.MOD_ID, "net_channel"))

    // use this: https://docs.architectury.dev/api/networking

    val WHEEL_DATA_S2C: ResourceLocation = ResourceLocation(Simplici.MOD_ID, "wheel_data_s2c")

    fun registerServer() {
        // Register C2S
    }

    fun registerClient() {
//        // Register S2C
//
//        // We are using S2C here for an example, use C2S instead if this is from the client to the server
//        registerReceiver(NetworkManager.Side.S2C, WHEEL_DATA_S2C) { buf, context ->
//            val player: Player = context.getPlayer()
//        }
//        CHANNEL.register( WheelDataPacket::class.java, WheelDataPacket::encode, { buffer -> WheelDataPacket(buffer) }, WheelDataPacket::apply )
    }
}
package org.valkyrienskies.simplici

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object ModConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client {
        @JsonSchema(description = "Speed a propeller will spin")
        val SIMPLE_PROPELLER_ROT_SPEED = 10.0

        @JsonSchema(description = "Speed a blast propeller will spin")
        val BLAST_PROPELLER_ROT_SPEED = 15.0
    }

    class Server {
    }
}

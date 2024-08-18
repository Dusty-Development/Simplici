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

        @JsonSchema(description = "Do rotators have a oposite and equal reaction")
        val NEWTONIAN_MOTORS = true

        @JsonSchema(description = "Do hinges drop their constraint when the block they are connected to is broken")
        val REJECT_FLOATING_HINGES = true

        @JsonSchema(description = "You can increse this if you need hinges to be stronger.. however this might cause jittering and / or flinging")
        val HINGE_COMPLIANCE = 1

        @JsonSchema(description = "You can increse this if you need hinges to be stronger.. however this might cause jittering and / or flinging")
        val MAX_SHIP_BLOCKS = 25000

        @JsonSchema(description = "")
        val SIMPLE_PROPELLER_FORCE = 100000.0

        @JsonSchema(description = "")
        val BLAST_PROPELLER_FORCE = 1000000.0

        @JsonSchema(description = "")
        val FIREWORK_THRUSTER_FORCE = 5000000.0
    }
}

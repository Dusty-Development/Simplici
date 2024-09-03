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

        @JsonSchema(description = "Do rotators have a opposite and equal reaction")
        val NewtonianMotors = true

        @JsonSchema(description = "You can increase this if you need hinges to be stronger.. however this might cause jittering and / or flinging")
        val HingeCompliance = 1

        @JsonSchema(description = "") val MaxShipAssemblyBlocks = 25000

        @JsonSchema(description = "Propellers") val SimplePropellerForce = 100000.0
        @JsonSchema(description = "") val BlastPropellerForce = 1000000.0
        @JsonSchema(description = "") val FireworkThrusterForce = 5000000.0

        @JsonSchema(description = "Rotator settings") val RotatorRPM = 128.0
        @JsonSchema(description = "") val RotatorTorque = 50000.0
        @JsonSchema(description = "") val RotatorFalloff = 8.0

        @JsonSchema(description = "Generic wheel settings")
        val SteeringAngle = 35.0

        @JsonSchema(description = "") val SteeringWheelsAwaysGrippy = true
        @JsonSchema(description = "") val SuspensionPullsToFloor = true
        @JsonSchema(description = "") val WheelSlideThreshold = 10.0
        @JsonSchema(description = "") val WheelGripForce = 3.0
        @JsonSchema(description = "") val WheelSlideForce = 1.0
        @JsonSchema(description = "") val WheelCastsResolution = 10
        @JsonSchema(description = "") val WheelSuspensionStiffness = 35.0
        @JsonSchema(description = "") val WheelSuspensionDamping = 10.0
        @JsonSchema(description = "") val WheelFreespinFriction = 0.3

        @JsonSchema(description = "specific wheel settings")
        val SmallWheelRestHeight = 0.75
        @JsonSchema(description = "") val SmallWheelMaxLength = 1.0

        @JsonSchema(description = "") val MediumWheelRestHeight = 1.0
        @JsonSchema(description = "") val MediumWheelMaxLength = 2.0

        @JsonSchema(description = "") val LargeWheelRestHeight = 1.5
        @JsonSchema(description = "") val LargeWheelMaxLength = 2.5
    }
}

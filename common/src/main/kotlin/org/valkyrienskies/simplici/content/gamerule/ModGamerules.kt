package org.valkyrienskies.simplici.content.gamerule

import net.minecraft.world.level.GameRules

object ModGamerules {

    // MECHANICAL PARTS \\
    val NEWTONIAN_ROTATORS: GameRules.Key<GameRules.BooleanValue> = GameRules.register("newtonianRotators", GameRules.Category.MISC, GameRules.BooleanValue.create(true))
    val NEWTONIAN_HYDRAULICS: GameRules.Key<GameRules.BooleanValue> = GameRules.register("newtonianHydraulics", GameRules.Category.MISC, GameRules.BooleanValue.create(true))

    val ROTATOR_RPM: GameRules.Key<GameRules.IntegerValue> = GameRules.register("rotatorRPM", GameRules.Category.MISC, GameRules.IntegerValue.create(64))
    val ROTATOR_TORQUE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("rotatorTorque", GameRules.Category.MISC, GameRules.IntegerValue.create(100000))
    val ROTATOR_FALL_OFF: GameRules.Key<GameRules.IntegerValue> = GameRules.register("rotatorFallOff", GameRules.Category.MISC, GameRules.IntegerValue.create(8_00)) // this value is 100 bigger

    val BALL_HINGE_MAX_ANGLE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("ballHingeMaxAngle", GameRules.Category.MISC, GameRules.IntegerValue.create(75))

    // ROPE \\
    val ROPE_MAX_TWIST: GameRules.Key<GameRules.IntegerValue> = GameRules.register("ropeMaxTwist", GameRules.Category.MISC, GameRules.IntegerValue.create(90))

    // THRUSTER \\
    val SIMPLE_PROPELLER_FORCE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("simplePropellerForce", GameRules.Category.MISC, GameRules.IntegerValue.create(100000))
    val BLAST_PROPELLER_FORCE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("blastPropellerForce", GameRules.Category.MISC, GameRules.IntegerValue.create(1000000))
    val FIREWORK_THRUSTER_FORCE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("fireworkThrusterForce", GameRules.Category.MISC, GameRules.IntegerValue.create(5000000))

    // ENGINES \\
    val ELECTRIC_ENGINE_MAX_POWER: GameRules.Key<GameRules.IntegerValue> = GameRules.register("electricEngineMaxPower", GameRules.Category.MISC, GameRules.IntegerValue.create(400000))
    val ELECTRIC_ENGINE_MAX_SPEED: GameRules.Key<GameRules.IntegerValue> = GameRules.register("electricEngineMaxSpeed", GameRules.Category.MISC, GameRules.IntegerValue.create(50))

    val RACE_ENGINE_MAX_POWER: GameRules.Key<GameRules.IntegerValue> = GameRules.register("raceEngineMaxPower", GameRules.Category.MISC, GameRules.IntegerValue.create(250000))
    val RACE_ENGINE_MAX_SPEED: GameRules.Key<GameRules.IntegerValue> = GameRules.register("raceEngineMaxSpeed", GameRules.Category.MISC, GameRules.IntegerValue.create(75))

    val STEAM_ENGINE_MAX_POWER: GameRules.Key<GameRules.IntegerValue> = GameRules.register("steamEngineMaxPower", GameRules.Category.MISC, GameRules.IntegerValue.create(175000))
    val STEAM_ENGINE_MAX_SPEED: GameRules.Key<GameRules.IntegerValue> = GameRules.register("steamEngineMaxSpeed", GameRules.Category.MISC, GameRules.IntegerValue.create(7))

    val TRACTOR_ENGINE_MAX_POWER: GameRules.Key<GameRules.IntegerValue> = GameRules.register("tractorEngineMaxPower", GameRules.Category.MISC, GameRules.IntegerValue.create(750000))
    val TRACTOR_ENGINE_MAX_SPEED: GameRules.Key<GameRules.IntegerValue> = GameRules.register("tractorEngineMaxSpeed", GameRules.Category.MISC, GameRules.IntegerValue.create(11))

    val TRUCK_ENGINE_MAX_POWER: GameRules.Key<GameRules.IntegerValue> = GameRules.register("truckEngineMaxPower", GameRules.Category.MISC, GameRules.IntegerValue.create(500000))
    val TRUCK_ENGINE_MAX_SPEED: GameRules.Key<GameRules.IntegerValue> = GameRules.register("truckEngineMaxSpeed", GameRules.Category.MISC, GameRules.IntegerValue.create(30))

    // WHEEL GENERICS \\
    val SHOULD_APPLY_FORCES_AT_FLOOR: GameRules.Key<GameRules.BooleanValue> = GameRules.register("shouldFrictionApplyAtFloor", GameRules.Category.MISC, GameRules.BooleanValue.create(false))
    val STEERING_WHEELS_ALWAYS_GRIP: GameRules.Key<GameRules.BooleanValue> = GameRules.register("steeringWheelsAlwaysGrip", GameRules.Category.MISC, GameRules.BooleanValue.create(false))

    val WHEEL_STEERING_ANGLE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelSteeringAngle", GameRules.Category.MISC, GameRules.IntegerValue.create(30_00)) // this value is 100 bigger
    val WHEEL_SLIDE_THRESHOLD: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelSlideThreshold", GameRules.Category.MISC, GameRules.IntegerValue.create(10_00)) // this value is 100 bigger

    val WHEEL_GRIP_FORCE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelGripForce", GameRules.Category.MISC, GameRules.IntegerValue.create(3_00)) // this value is 100 bigger
    val WHEEL_SLIDE_FORCE: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelSlideForce", GameRules.Category.MISC, GameRules.IntegerValue.create(2_50)) // this value is 100 bigger

    val WHEEL_CAST_RESOLUTION: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelCastsResolution", GameRules.Category.MISC, GameRules.IntegerValue.create(10))
    val WHEEL_SUSPENSION_STIFFNESS: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelSuspensionStiffness", GameRules.Category.MISC, GameRules.IntegerValue.create(35_00)) // this value is 100 bigger
    val WHEEL_SUSPENSION_DAMPING: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelSuspensionDamping", GameRules.Category.MISC, GameRules.IntegerValue.create(10_00)) // this value is 100 bigger
    val WHEEL_FREESPIN_FRICTION: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelFreespinFriction", GameRules.Category.MISC, GameRules.IntegerValue.create(30)) // this value is 100 bigger
    val WHEEL_LOCKED_FRICTION: GameRules.Key<GameRules.IntegerValue> = GameRules.register("wheelLockedFriction", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger

    // WHEEL SPECIFICS \\
    val LARGE_WHEEL_REST_HEIGHT: GameRules.Key<GameRules.IntegerValue> = GameRules.register("largeWheelRestHeight", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger
    val MEDIUM_WHEEL_REST_HEIGHT: GameRules.Key<GameRules.IntegerValue> = GameRules.register("mediumWheelRestHeight", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger
    val SMALL_WHEEL_REST_HEIGHT: GameRules.Key<GameRules.IntegerValue> = GameRules.register("smallWheelRestHeight", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger

    val LARGE_WHEEL_MAX_LENGTH: GameRules.Key<GameRules.IntegerValue> = GameRules.register("largeWheelMaxLength", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger
    val MEDIUM_WHEEL_MAX_LENGTH: GameRules.Key<GameRules.IntegerValue> = GameRules.register("mediumWheelMaxLength", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger
    val SMALL_WHEEL_MAX_LENGTH: GameRules.Key<GameRules.IntegerValue> = GameRules.register("smallWheelMaxLength", GameRules.Category.MISC, GameRules.IntegerValue.create(1_00)) // this value is 100 bigger


    fun register() {

    }
}
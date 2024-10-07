package org.valkyrienskies.simplici.content.block.engine.thruster.propeller.blast_propeller

import net.minecraft.core.BlockPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlockEntity
import org.valkyrienskies.simplici.content.gamerule.ModGamerules

class BlastPropellerBlockEntity(pos: BlockPos, state: BlockState)
    : ThrusterBlockEntity(ModBlockEntities.BLAST_PROPELLER.get(), pos, state)
{

    override val thrusterForceKey: GameRules.Key<GameRules.IntegerValue> = ModGamerules.BLAST_PROPELLER_FORCE
    override val thrusterMaxSpeed: Double = 128.0
    override val thrusterSoftMaxSpeed: Double = 96.0

    override val fuelDefaultName: String = "blast_propeller"

}

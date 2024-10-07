package org.valkyrienskies.simplici.content.block.engine.car.steam

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.api.util.RotShape
import org.valkyrienskies.simplici.api.util.RotShapes
import org.valkyrienskies.simplici.content.block.engine.car.EngineBlock

class SteamEngineBlock : EngineBlock(
    Properties.of().sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    override val SHAPE: RotShape = RotShapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SteamEngineBlockEntity(pos, state)

}
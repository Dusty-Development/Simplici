package org.valkyrienskies.simplici.content.block.mechanical.head

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.simplici.content.block.ModBlockEntities

class MechanicalHeadBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(ModBlockEntities.MECHANICAL_HEAD.get(), pos, state)
{

    var parentBlockPos:BlockPos = BlockPos.ZERO
    var shouldDrawBeam = false

    override fun load(compoundTag: CompoundTag) {
        parentBlockPos = BlockPos.of(compoundTag.getLong(ParentPosNBTString))
        super.load(compoundTag)
    }

    override fun saveAdditional(compoundTag: CompoundTag) {
        compoundTag.putLong(ParentPosNBTString, parentBlockPos.asLong())
        super.saveAdditional(compoundTag)
    }

    fun tick() {
        if(level?.isClientSide == true) return
        level?.updateNeighborsAt(blockPos, blockState.block)
    }

    companion object {
        const val ParentPosNBTString = "ParentPos"
    }

}
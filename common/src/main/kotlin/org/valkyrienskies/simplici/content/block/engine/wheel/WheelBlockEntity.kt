package org.valkyrienskies.simplici.content.block.engine.wheel

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.*
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.mod.common.yRange
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.simplici.api.extension.intPos
import org.valkyrienskies.simplici.content.block.ModBlocks
import kotlin.math.abs

abstract class WheelBlockEntity(blockEntityType: BlockEntityType<*>, pos: BlockPos, state: BlockState)
    : BlockEntity(blockEntityType, pos, state)
{

    var isConstrained:Boolean = false
    var mechanicalHeadBlockPos: BlockPos? = null
    var isLoading:Boolean = false


    open fun tick() {
        if(level!!.isClientSide) return

        if (isLoading) {
            loadConstraints()
            return
        }
    }

    // SAVE DATA \\

    override fun load(tag: CompoundTag) {
        super.load(tag)
        isLoading = true
//        mechanicalHeadBlockPos = BlockPos.of(tag.getLong("head_pos"))
    }

    fun loadConstraints() {
        if (level!!.isClientSide()) return
        isConstrained = (mechanicalHeadBlockPos != null)
//        applyConstraints()
        isLoading = false
    }

    override fun saveAdditional(tag: CompoundTag) {
        if (level!!.isClientSide() && isConstrained) return
//        mechanicalHeadBlockPos?.let { tag.putLong("head_pos", it.asLong()) }
        super.saveAdditional(tag)
    }
}
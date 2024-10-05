package org.valkyrienskies.simplici.content.block.tool.rope_hook.handle

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.simplici.content.block.ModBlockEntities
import org.valkyrienskies.simplici.content.block.tool.rope_hook.RopeHookBlockEntity
import org.valkyrienskies.simplici.content.ship.modules.util.HandleControlModule
import org.valkyrienskies.simplici.content.ship.modules.util.HandleForcesData

class HandleBlockEntity(pos: BlockPos, state: BlockState) : RopeHookBlockEntity(ModBlockEntities.HANDLE.get(), pos, state) {

    var holdingPlayer:Player? = null
    var holdOffset = Vector3d()
    var holdDistance = 0.0

    private fun drop() {
        holdingPlayer = null
        holdOffset = Vector3d()

        val ship = level.getShipObjectManagingPos(blockPos)
        if(level?.isClientSide == false) HandleControlModule.getOrCreate(ship as ServerShip).removeHolder(blockPos)
    }

    private fun grab(player: Player, hand: InteractionHand, hit: BlockHitResult) {
        val ship = level.getShipObjectManagingPos(blockPos)
        val worldLocation = ship?.transform?.shipToWorld?.transformPosition(hit.location.toJOML())

        holdingPlayer = player
        holdOffset = Vector3d()//hit.location.toJOML().sub(blockPos.center.toJOML()) // for now this is just the hit pos
        holdDistance = player.eyePosition.distanceTo(worldLocation!!.toMinecraft())
    }

    fun getData() : HandleForcesData {
        val data = HandleForcesData(state = blockState)

        data.offset = holdOffset

        val playerEyePos = holdingPlayer!!.eyePosition.toJOML()
        val playerForward = holdingPlayer!!.forward.toJOML().mul(holdDistance)
        data.target = playerEyePos.add(playerForward)
        data.targetDirection = playerForward

        data.isCreative = holdingPlayer!!.isCreative

        return data
    }

    override fun tick() {
        if(holdingPlayer != null) {
            val ship = level.getShipObjectManagingPos(blockPos)
            val data = getData()
            if(level?.isClientSide == false && ship != null && ship is ServerShip) HandleControlModule.getOrCreate(ship as ServerShip).addHolder(blockPos, data)

            val worldLocation = ship?.transform?.shipToWorld?.transformPosition(blockPos.toJOMLD().add(holdOffset))
            if(holdingPlayer!!.eyePosition.distanceTo(worldLocation!!.toMinecraft()) > 4.75 && !holdingPlayer!!.isCreative) { drop() }
        }
        super.tick()
    }

    override fun onUse(player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        if(level.getShipObjectManagingPos(blockPos) == null) return super.onUse(player, hand, hit)

        if(holdingPlayer == null) grab(player, hand, hit) else drop()
        return super.onUse(player, hand, hit)
    }

    companion object {
    }

}
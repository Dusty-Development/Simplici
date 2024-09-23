package org.valkyrienskies.simplici.content.block.mechanical.hydraulic

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
import org.valkyrienskies.simplici.api.extension.pose
import org.valkyrienskies.simplici.content.render.ModModels

class HydraulicBlockEntityRenderer (
    val model: ModModels.Model
): BlockEntityRenderer<HydraulicBlockEntity> {

    override fun render(
        be: HydraulicBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            translate(0.5, 0.5, 0.5)
            mulPose(be.blockState.getValue(DirectionalBlock.FACING).rotation)
            translate(-0.5, 0.5 - (1.0/16.0), -0.5)
            if((be.level?.getBestNeighborSignal(be.blockPos) ?: 0) > 0) {
                model.renderer.render(
                    pose,
                    be,
                    bufferSource,
                    packedLight,
                    packedOverlay
                )
            }
        }
    }

}
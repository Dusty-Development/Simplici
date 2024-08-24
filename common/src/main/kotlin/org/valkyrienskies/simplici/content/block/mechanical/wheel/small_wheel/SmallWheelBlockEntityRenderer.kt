package org.valkyrienskies.simplici.content.block.mechanical.wheel.small_wheel

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.AxisAngle4f
import org.joml.Math
import org.joml.Quaternionf
import org.joml.Vector3f
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.extension.pose
import org.valkyrienskies.simplici.content.render.ModModels

class SmallWheelBlockEntityRenderer (
    val model: ModModels.Model
): BlockEntityRenderer<SmallWheelBlockEntity> {

    override fun render(
        be: SmallWheelBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            var dist = be.currentDist //Math.lerp(be.lastDist, be.currentDist, (Minecraft.getInstance().deltaFrameTime) * 20.0)

            translate(0.5, 0.5, 0.5)
            translate(0.0, -dist, 0.0)

            mulPose(be.blockState.getValue(DirectionalBlock.FACING).rotation)
            pose.mulPose(Quaternionf(AxisAngle4f(0f, 0f, 1f, 0f)))

            translate(-0.5, 0.0, -0.5)

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
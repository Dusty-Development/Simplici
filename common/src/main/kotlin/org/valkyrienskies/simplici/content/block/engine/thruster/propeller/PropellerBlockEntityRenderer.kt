package org.valkyrienskies.simplici.content.block.engine.thruster.propeller

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.valkyrienskies.simplici.ModConfig
import org.valkyrienskies.simplici.api.extension.pose
import org.valkyrienskies.simplici.content.block.engine.thruster.ThrusterBlockEntity
import org.valkyrienskies.simplici.content.render.ModModels

class PropellerBlockEntityRenderer (
    val model: ModModels.Model
): BlockEntityRenderer<ThrusterBlockEntity> {

    override fun render(
        be: ThrusterBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            translate(0.5, 0.5, 0.5)
            mulPose(be.blockState.getValue(DirectionalBlock.FACING).rotation)
            be.animationAngle += be.blockState.getValue(BlockStateProperties.POWER).toDouble()*2 * ModConfig.CLIENT.SIMPLE_PROPELLER_ROT_SPEED * (Minecraft.getInstance().deltaFrameTime)
            if(be.animationAngle >= 360.0) be.animationAngle -= 360.0
            pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians( be.animationAngle ).toFloat(), 0f, 1f, 0f)))
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
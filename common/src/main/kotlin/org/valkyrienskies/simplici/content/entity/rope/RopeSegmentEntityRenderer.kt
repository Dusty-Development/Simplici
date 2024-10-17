package org.valkyrienskies.simplici.content.entity.rope

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3f
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorld
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.simplici.Simplici


class RopeSegmentEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<RopeSegmentEntity>(context) {

    override fun render(
        entity: RopeSegmentEntity,
        yaw: Float,
        tickDelta: Float,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        light: Int,
    ) {
        super.render(entity, yaw, tickDelta, poseStack, multiBufferSource, light)
        poseStack.pushPose()

        val vertexConsumer = multiBufferSource.getBuffer(RenderType.leash())
        val positionMatrix = poseStack.last().pose()

        drawLineInDir(Vector3d(1.0,0.0,0.0), RopeSegmentEntity.halfLength, Vector3f(0.5f, 0.4f, 0.3f), entity, vertexConsumer, positionMatrix)
        drawLineInDir(Vector3d(1.0,0.0,0.0), -RopeSegmentEntity.halfLength, Vector3f(0.5f,0.4f, 0.3f).mul(0.7f), entity, vertexConsumer, positionMatrix)

        poseStack.popPose()
    }

    fun drawLineInDir(dir:Vector3d, length:Double, color: Vector3f, entity: RopeSegmentEntity, vertexConsumer: VertexConsumer, positionMatrix:Matrix4f) {
        val renderTransform = entity.getRenderTransform(entity.level().shipObjectWorld as ShipObjectClientWorld)

        val eyePos = entity.eyePosition.toJOML()
        val blockPos = BlockPos(eyePos.x.toInt(), eyePos.y.toInt(), eyePos.z.toInt())

        val blockLight = getBlockLightLevel(entity, blockPos)
        val skyLight = getSkyLightLevel(entity, blockPos)
        val totalLight = LightTexture.pack(blockLight, skyLight)

        var direction = renderTransform?.transformDirectionNoScalingFromShipToWorld(dir, Vector3d())
        if(direction == null) direction = dir

        val start = direction.mul(length, Vector3d())
        val end = direction.mul(0.0, Vector3d())
        val normal = Vector3d(start.sub(end, Vector3d()).normalize())

        vertexConsumer.vertex(positionMatrix, start.x.toFloat(), start.y.toFloat(), start.z.toFloat()).color(color.x, color.y, color.z, 1.0f).uv2(totalLight).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()
        vertexConsumer.vertex(positionMatrix, end.x.toFloat(), end.y.toFloat(), end.z.toFloat()).color(color.x, color.y, color.z, 1.0f).uv2(totalLight).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()
    }

    private fun renderSegment(
        vertexConsumer: VertexConsumer,
        positionMatrix: Matrix4f,
        x: Float,
        y: Float,
        z: Float,
        blockLight: Int,
        skyLight: Int,
        m: Float,
        n: Float,
        o: Float,
        p: Float,
        pieceIndex: Int,
        isReturning: Boolean,
    ) {
        val delta = pieceIndex.toFloat() * 0.5f
        val totalLight = LightTexture.pack(blockLight, skyLight)

        val darknessMultiplier = if (pieceIndex % 2 == (if (isReturning) 1 else 0)) 0.7f else 1.0f;

        val red = 0.5F * darknessMultiplier
        val green = 0.4F * darknessMultiplier
        val blue = 0.3F * darknessMultiplier

        val localX = x * delta
        val localY = if (y > 0.0f) y * delta * delta else y - y * (1.0f - delta) * (1.0f - delta);
        val localZ = z * delta
        vertexConsumer.vertex(positionMatrix, localX - o, localY - n, localZ + p).color(red, green, blue, 1.0f).uv2(totalLight).endVertex()
        vertexConsumer.vertex(positionMatrix, localX + o, localY + m - n, localZ - p).color(red, green, blue, 1.0f).uv2(totalLight).endVertex()
    }

    override fun getTextureLocation(entity: RopeSegmentEntity): ResourceLocation = ResourceLocation(Simplici.MOD_ID, "textures/entity/rope_segment/rope_segment.png");
}
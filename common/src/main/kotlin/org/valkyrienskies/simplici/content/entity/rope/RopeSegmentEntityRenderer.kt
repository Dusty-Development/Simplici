package org.valkyrienskies.simplici.content.entity.rope

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.joml.Vector3d
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorld
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.simplici.Simplici
import java.awt.Color


class RopeSegmentEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<RopeSegmentEntity>(context) {

    override fun render(
        entity: RopeSegmentEntity,
        f: Float,
        g: Float,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        i: Int
    ) {
        poseStack.pushPose()

        val vertexConsumer = multiBufferSource.getBuffer(RenderType.lines())
        val positionMatrix = poseStack.last().pose()

        drawLineInDir(Vector3d(1.0,0.0,0.0), RopeSegmentEntity.length + RopeSegmentEntity.radius, Color.RED, entity, vertexConsumer, positionMatrix)
        drawLineInDir(Vector3d(0.0,1.0,0.0), RopeSegmentEntity.radius, Color.GREEN, entity, vertexConsumer, positionMatrix)
        drawLineInDir(Vector3d(0.0,0.0,1.0), RopeSegmentEntity.radius, Color.BLUE, entity, vertexConsumer, positionMatrix)

        poseStack.popPose()
        super.render(entity, f, g, poseStack, multiBufferSource, i)
    }

    fun drawLineInDir(dir:Vector3d, length:Double, color: Color, entity: RopeSegmentEntity, vertexConsumer: VertexConsumer, positionMatrix:Matrix4f) {
        val renderTransform = entity.getRenderTransform(entity.level().shipObjectWorld as ShipObjectClientWorld)

        var direction = renderTransform?.transformDirectionNoScalingFromShipToWorld(dir, Vector3d())
        if(direction == null) direction = dir

        val start = direction.mul(length, Vector3d())
        val end = direction.mul(-length, Vector3d())
        val normal = Vector3d(start.sub(end, Vector3d()).normalize())

        vertexConsumer.vertex(positionMatrix, start.x.toFloat(), start.y.toFloat(), start.z.toFloat()).color(color.red, color.green, color.blue, color.alpha).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()
        vertexConsumer.vertex(positionMatrix, end.x.toFloat(), end.y.toFloat(), end.z.toFloat()).color(color.red, color.green, color.blue, color.alpha).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()

    }

    override fun getTextureLocation(entity: RopeSegmentEntity): ResourceLocation = ResourceLocation(Simplici.MOD_ID, "textures/entity/rope_segment/rope_segment.png");
}
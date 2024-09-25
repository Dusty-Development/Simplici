package org.valkyrienskies.simplici.content.block.mechanical.wheel

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.DirectionalBlock
import org.joml.AxisAngle4f
import org.joml.Math
import org.joml.Math.lerp
import org.joml.Quaternionf
import org.joml.Vector3d
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.simplici.api.extension.getVelAtPos
import org.valkyrienskies.simplici.api.extension.pose
import org.valkyrienskies.simplici.content.render.ModModels
import org.valkyrienskies.simplici.content.ship.modules.wheel.WheelForcesData

class WheelBlockEntityRenderer (
    val model: ModModels.Model
): BlockEntityRenderer<WheelBlockEntity> {

    override fun render(
        be: WheelBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            val dt = (Minecraft.getInstance().deltaFrameTime)
            val dist = be.currentDist //Math.lerp(be.lastDist, be.currentDist, dt * 20.0)

            translate(0.5, 0.5, 0.5)
            translate(0.0, -dist, 0.0)

            val ship = be.level.getShipManagingPos(be.blockPos)
            if(ship != null) {
                val worldBlockPos = ship.transform.shipToWorld.transformPosition(be.blockPos.center.toJOML())

                val direction = be.blockState.getValue(DirectionalBlock.FACING).normal.toJOMLD()
                val globalDir = ship.transform.transformDirectionNoScalingFromShipToWorld(direction.rotateY(java.lang.Math.toRadians(be.steeringAngle)), Vector3d()).normalize()

                val velocity = be.pointVelocity(ship, worldBlockPos)
                var floorVelocity = be.wheelData.floorVel
                if(be.wheelData.floorVel == null) floorVelocity = Vector3d()
                val localVelocity = velocity.sub(floorVelocity, Vector3d()).dot(globalDir)

                val rotationAngle = Math.toDegrees(((localVelocity / (2 * Math.PI * be.wheelRadius)) / Math.PI))
                be.lastDrivingVel = if(be.wheelData.colliding) rotationAngle else lerp(be.lastDrivingVel, 0.0, Math.clamp(0.0, 1.0, dt * 0.35))

                be.drivingAngle += be.lastDrivingVel * dt
                if(be.drivingAngle >= 360) be.drivingAngle -= 360

            }

            mulPose(be.blockState.getValue(DirectionalBlock.FACING).rotation.rotateX(Math.toRadians(be.drivingAngle).toFloat()).rotateY(Math.toRadians(be.steeringAngle).toFloat()))
            pose.mulPose(Quaternionf(AxisAngle4f(0f, 0f, 1f, 0f)))

            translate(-0.5, -0.5, -0.5)

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
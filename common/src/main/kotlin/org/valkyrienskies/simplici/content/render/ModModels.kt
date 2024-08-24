package org.valkyrienskies.simplici.content.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import org.valkyrienskies.simplici.Simplici
import org.valkyrienskies.simplici.api.services.ModPlatformHelper

object ModModels {
    val MODELS = mutableSetOf<ResourceLocation>()

    val PROPELLER = model("block/engine/propeller")
    val MECHANICAL_BEAM = model("block/mechanical/mechanical_beam")
    val SMALL_WHEEL = model("block/mechanical/wheel/small_tyre")

    private fun getModel(rl: ResourceLocation): BakedModel {
        val model = ModPlatformHelper
            .get()
            .loadBakedModel(rl)

        if (model == null) {
            println("${Simplici.MOD_ID} Failed to load model $rl")
            return Minecraft.getInstance().modelManager.missingModel
        }

        return model
    }


    interface Renderer {
        fun render(
            matrixStack: PoseStack,
            blockEntity: BlockEntity,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
        )
    }

    data class Model(
        val resourceLocation: ResourceLocation,
        val checkSides: Boolean = true,
        val useAO: Boolean = false,
    ) {
        val bakedModel: BakedModel by lazy {
            getModel(resourceLocation)
        }

//        fun renderNow(matrixStack: PoseStack, blockEntity: BlockEntity, packedOverlay: Int) {
//            val level = blockEntity.level ?: return
//            val modRend = Minecraft.getInstance().blockRenderer.modelRenderer
//
//            val buf = DefinitelyNotCopiedFromCreateSuperRenderTypeBuffer.getInstance()
//            modRend.tesselateWithAO(
//                level,
//                bakedModel,
//                blockEntity.blockState,
//                blockEntity.blockPos,
//                matrixStack,
//                buf.getBuffer(RenderType.cutout()),
//                checkSides,
//                level.random,
//                42L, // Used in ModelBlockRenderer.class in renderModel, not sure what the right number is but this seems to work
//                packedOverlay
//            )
//            buf.draw()
//        }

        val renderer = object : Renderer {
            override fun render(
                matrixStack: PoseStack,
                blockEntity: BlockEntity,
                bufferSource: MultiBufferSource,
                packedLight: Int,
                packedOverlay: Int
            ) {
                val level = blockEntity.level ?: return

                val modRend = Minecraft.getInstance().blockRenderer.modelRenderer
                val fn = if (useAO) modRend::tesselateWithAO else modRend::tesselateWithoutAO
                fn(
                    level,
                    bakedModel,
                    blockEntity.blockState,
                    blockEntity.blockPos,
                    matrixStack,
                    bufferSource.getBuffer(RenderType.cutout()),
                    checkSides,
                    level.random,
                    42L, // Used in ModelBlockRenderer.class in renderModel, not sure what the right number is but this seems to work
                    packedOverlay
                )
            }
        }
    }

    private fun model(name: String, checkSides: Boolean = true, useAO: Boolean = false): Model {
        val rl = ResourceLocation(Simplici.MOD_ID, name)
        MODELS += rl
        return Model(rl, checkSides, useAO)
    }

}
package org.valkyrienskies.simplici.content.gui.fuel.tank

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.valkyrienskies.simplici.Simplici

class FuelTankScreen(handler: FuelTankMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<FuelTankMenu>(handler, playerInventory, text) {

    // The texture is 512 so every coord is 2 pixels big
    override fun renderBg(guiGraphics: GuiGraphics, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val xP = (width - imageWidth) / 2
        val yP = (height - imageHeight) / 2

        menu as FuelTankMenu

        guiGraphics.pose().pushPose()
        // This matrix magic is bcs the texture is 512x512 and is 256x256 mc classic (mojank)
        guiGraphics.pose().translate(xP.toDouble(), yP.toDouble(), 0.0)
        guiGraphics.pose().scale(2f, 2f, 2F)

        // Draw the container background
        val (containerX, containerY) = Pair(CONTAINER_X, CONTAINER_Y)

        guiGraphics.blit(TEXTURE, FIRE_HOLE_X, FIRE_HOLE_Y, containerX, containerY, FIRE_HOLE_WIDTH, FIRE_HOLE_HEIGHT)

        // Draw the glass background
        val (glassX, glassY) = Pair(CONTAINER_X, CONTAINER_Y)

        guiGraphics.blit(TEXTURE, FIRE_HOLE_X, FIRE_HOLE_Y, glassX, glassY, FIRE_HOLE_WIDTH, FIRE_HOLE_HEIGHT)

        // Draw the inventory
        guiGraphics.blit(TEXTURE, 0, 0, 0, 0, imageWidth / 2, imageHeight / 2)
        guiGraphics.pose().popPose()
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        // super.renderLabels(poseStack, mouseX, mouseY)
    }

    companion object { // TEXTURE DATA
        internal val TEXTURE = ResourceLocation(Simplici.MOD_ID, "textures/gui/fuel_tank.png")

        private const val FIRE_HOLE_X = 10 / 2
        private const val FIRE_HOLE_Y = 8 / 2

        private const val FIRE_HOLE_WIDTH = 156 / 2
        private const val FIRE_HOLE_HEIGHT = 68 / 2

        private const val CONTAINER_X = 10 / 2
        private const val CONTAINER_Y = 318 / 2

    }
}
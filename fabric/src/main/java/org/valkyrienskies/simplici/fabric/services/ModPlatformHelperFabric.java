package org.valkyrienskies.simplici.fabric.services;

import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.simplici.api.services.ModPlatformHelper;

public class ModPlatformHelperFabric implements ModPlatformHelper {

    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        return Minecraft.getInstance()
                .getModelManager()
                .getModel((ModelResourceLocation) modelLocation);
    }
}

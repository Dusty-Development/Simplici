package org.valkyrienskies.simplici.forge.services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.simplici.api.services.ModPlatformHelper;

public class ModPlatformHelperForge implements ModPlatformHelper {

    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        ModelBakery mb = Minecraft.getInstance().getModelManager().getModelBakery();
        return mb.getBakedTopLevelModels()
                .getOrDefault(
                        modelLocation,
                        null
                );
    }
}

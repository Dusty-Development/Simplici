package org.valkyrienskies.simplici.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.simplici.content.render.ModModels;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.simplici.content.item.ModItems;
import org.valkyrienskies.simplici.Simplici;
import org.valkyrienskies.simplici.registry.CreativeTabs;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class SimpliciModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before simplici
        new ValkyrienSkiesModFabric().onInitialize();

        Simplici.init();

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ModItems.INSTANCE.getTAB(),
            CreativeTabs.INSTANCE.create()
        );
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            Simplici.initClient();
            Simplici.initClientRenderers(new ClientRenderersFabric());

            ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) ->
                    ModModels.INSTANCE.getMODELS().forEach(out));
        }

        private static class ClientRenderersFabric implements Simplici.ClientRenderers {
            @Override
            public <T extends BlockEntity> void registerBlockEntityRenderer(
                    @NotNull BlockEntityType<T> t,
                    @NotNull BlockEntityRendererProvider<T> r) {
                BlockEntityRendererRegistry.register(t, r);
            }
        }
    }

}

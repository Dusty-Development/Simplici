package org.valkyrienskies.simplici.forge

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import org.valkyrienskies.simplici.content.block.ModBlocks
import java.util.concurrent.CompletableFuture

class SimpliciBlockTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    modId: String,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output, lookupProvider, modId, existingFileHelper) {
    override fun addTags(arg: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.CONTROL_PANEL.get())
    }
}

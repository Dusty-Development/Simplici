package org.valkyrienskies.simplici.registry

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block

// These are here to make blocks / items that dont appear in the creative tabs

interface NoBlockItem

interface NoCreativeTab
class NoTabBlockItem(block: Block, properties: Properties) : BlockItem(block, properties), NoCreativeTab
BLOCKREGISTRY ← "main/kotlin/org/valkyrienskies/simplici/content/block/ModBlocks.kt"
REGISTRYFORMAT ← $"val _ = BLOCKS.register(\"_\", ::_)"
LOOTTABLEDIR ← "main/resources/data/simplici/loot_tables/blocks"
LOOTTABLEFORMAT ← $"{\"pools\":[{\"rolls\": 1,\"entries\": [{\"type\": \"minecraft:item\",\"name\": \"simplici:_\"}]}]}"

&fras BLOCKREGISTRY
⊜□≠@\n.
⍚(▽\↥≠@ .)
⋅⊙◌⍚⍣(°REGISTRYFORMAT|"""""")
▽±≡◇⧻.
&cd LOOTTABLEDIR
⍚(&fwa$"_.json"⟜LOOTTABLEFORMAT)

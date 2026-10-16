# Compilation triage — Pass 16 (log logs_105332793041.zip)

## Observed result
- Build reaches `compileJava` and stops at **80 errors / 20 warnings**.
- Previous build stopped at 92 errors. These are counts from separate GitHub Actions runs, not locally verified fixes.
- Most errors stem from the migration to Minecraft 26.2 and NeoForge's transfer system.

## Changes applied to source
1. `ItemStackTagAdapter`: `TagValueInput.create(...)` returns the `ValueInput` interface in 26.2, so the declared variable now uses that return type. This keeps the existing `ItemStack.CODEC` persistence path.
2. `PlaneArmorItem`: removed two obsolete `Item` overrides (`isEnchantable` and `getEnchantmentValue`), preserved protection-enchantment checks using `Holder#is(key)`.
3. `SimplePlanesItems`: set the existing armor item's enchantability to `9` using `Item.Properties.enchantable(9)` (data component), retaining the intended value.
4. `PlaneWorkbenchBlock` / `PlaneWorkbenchBlockEntity`: removed the obsolete block `onRemove` override and moved inventory export to `BlockEntity#preRemoveSideEffects(BlockPos, BlockState)`, which runs before block entity removal. Both stored ingredient slots are dropped on the server. Test block breaking and piston replacement in a disposable world.

## High priority remaining work
- Upgrade capability modernization: `Capabilities.ItemHandler`, `Capabilities.FluidHandler`, and `Capabilities.EnergyStorage` have been replaced with `Capabilities.Item`, `Capabilities.Fluid`, and `Capabilities.Energy`. **The associated interfaces changed too**, so naive class/constant substitution would break fluid and item transfer, and was intentionally not done.
- Upgrade-screen 3D preview: render-state-based extraction and submission.
- `PlaneItemColors`: migrate legacy item tint handlers/model data to the modern model system.
- Aircraft entity override signatures, interaction and collision validation.
- Workbench recipes, crafting remainder and engine fuel, HUD/camera and networking.
- Other compiler issues detailed in the GitHub Actions log. Some engine/inventory/shooter errors remain intentionally untouched pending careful compatibility work.

## Validation
Source changes inspected, ZIP integrity verified. This environment has Java 21, but the 26.2 development toolchain requires Java 25. **This archive is not a successful build** and should not be installed on the live Youer server.

## Sources consulted
- https://github.com/neoforged/Documentation/blob/main/docs/items/index.md
- https://github.com/neoforged/.github/blob/main/primers/1.21.2/index.md
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/level/storage/tagvalueinput
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/level/block/barrelblock
- https://neoforged.net/news/21.9-transfer-rework/

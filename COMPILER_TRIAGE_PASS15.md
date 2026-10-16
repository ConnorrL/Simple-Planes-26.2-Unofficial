# Pass 15 — GitHub Actions build diagnostics

The submitted GitHub Actions log `logs_105329657896.zip` reports **92 errors** and fails `:compileJava`. The prior run reported 107 errors. These numbers are from separate builds; Pass 15 still needs to be built.

## This pass

- Updated `Minecraft.screen` to `Minecraft.gui.screen()` and the overlay message location to `Minecraft.gui.hud.setOverlayMessage()`.
- Replaced the removed `Items.WHITE_BANNER` constant with the Minecraft registry lookup.
- Introduced `ItemStackTagAdapter` for banner and jukebox upgrade data, using `TagValueOutput`, `TagValueInput`, and `ItemStack.CODEC`.
- Updated the GUI fluid sprite draw submission path away from immediate-mode `BufferBuilder`/`RenderSystem` calls. Existing sprite sampling still needs attention.
- Replaced the removed `Level.getTimeOfDay(float)` usage with the angular equivalent from `Level.getSunAngle(float)`.

## Known critical limitations

- **This patch is not yet compiled or gameplay tested.** Java 25/Minecraft dependencies are unavailable locally; run GitHub Actions.
- `PlaneItemColors` and its registration need a complete codec-backed `ItemTintSource` migration.
- The workbench, aircraft entity, 3D inventory preview, and native NeoForge 26.2 transfer capabilities still require updates.
- Rendering upgrades, banners, special effects, and fluid sprites still require visual validation.
- `ItemStackTagAdapter` saves the new `stack` field. It does **not** yet implement a legacy 1.21.1 NBT fallback. Old saves must be migrated before any world upgrade. Never test first on a production world.
- Shooter upgrade and projectile implementation were not modified in this pass.
- Compile errors often cascade. Only the next GitHub Actions run can tell us the new error count.

# Port Pass 13: Source migration notes

Source: user-uploaded GitHub Actions log `logs_105282130834.zip`, based on Pass 12.

## Verified compile diagnostics
- `compileJava` failed, with Gradle reporting 135 errors and 20 warnings (below the configured 250-error limit).
- GUI/HUD code used the obsolete six-argument `GuiGraphicsExtractor.blit(Identifier, x, y, u, v, width, height)`.
- Seven upgrade models still implemented the former six-parameter `setupAnim(PlaneEntity, ...)` method.
- `Gui.rightHeight` is no longer available.

## Changes this pass
- Migrate 19 old HUD/inventory texture draws to `RenderPipelines.GUI_TEXTURED` with verified 256x256 source textures.
- Migrate seven engine/booster upgrade model animation method signatures, preserving geometry.
- Stop referencing the removed HUD side-offset field; stack aircraft health rows locally.

## Still not migrated / not validated
- New item tint-source registration and item color texture sampling.
- The remaining 135 errors are not all covered by this pass; check the next CI build for a new count.
- Passenger render-state handling.
- Existing upgrade gameplay and capability APIs (items, fluids, energy).
- GUI aircraft preview; entity API overrides; workbench and recipe access; advanced GUI rendering.
- The port has not compiled successfully or been tested on NeoForge 26.2 or Youer.

The archive is source-only; do not install it as a gameplay mod. Use GitHub Actions and attach the next complete build log. Verify all features on a test world before trying on a live server.

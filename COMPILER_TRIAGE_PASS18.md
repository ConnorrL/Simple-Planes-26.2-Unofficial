# Simple Planes 26.2 — compiler triage / Pass 18

Input: `logs_105343277220.zip`, GitHub Actions build on October 16, 2026.
Previous Pass 17 log: 75 errors. Current Pass 17 build: **56 errors** (reported total; before these edits).

## Fixes in this patch (targeting three concrete errors)

1. `ClientUtil` now retrieves a fluid atlas sprite via `GuiGraphicsExtractor.getSprite(SpriteId)`, keeping the existing tiled-sprite rendering and fluid texture selection. This replaces the removed `Minecraft#getTextureAtlas` accessor.
2. `ClientEventHandler` uses the GUI screen-null check rather than the removed `Minecraft#getOverlay()` accessor for inventory hotkeys. Verify keyboard handling during loading overlays.
3. `SolarPanelUpgrade` replaces the removed `Level#getSunAngle` with the legacy vanilla day-time interpolation based on the world's default clock time (`Level#getDefaultClockTime`). Its original rain/thunder attenuation remains. This is an approximation for 26.2's newer environment attributes and requires gameplay checks on custom dimensions.

## Not changed in this pass

- Old `RegisterColorHandlersEvent.Item` and `PlaneItemColors` rely on a removed item-color/model API. They need coordinated `ItemTintSource` and client item JSON migration to preserve material tinting.
- `RenderLivingEvent` now carries a render state and three type parameters, not an entity. Rider rotation must be extracted through a safe render-state/mixin path; deleting code would silently lose functionality.
- Transfer capability providers and consumers for item/fluid/energy must be rewritten together to NeoForge's `ResourceHandler` and `EnergyHandler` APIs; blind constant renames would be incorrect.
- Aircraft entity overrides, workbench recipe synchronization, payload entity data and shooter upgrade are unresolved in the latest log. Do not remove features to force compilation.

## Validation

Exact replacements and ZIP integrity checked. **Not compiled successfully.** The available environment has Java 21, while Minecraft 26.2 development requires Java 25. Run GitHub Actions and inspect the next log. Do not install on a live Youer server.

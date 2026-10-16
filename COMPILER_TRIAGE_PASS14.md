# Simple Planes 26.2 — Port Pass 14

Target: Minecraft 26.2 / NeoForge 26.2.0.88 / Youer testing later.

## Diagnostic baseline

The supplied `logs_105327593055.zip` reports 107 compiler errors and 20 warnings (the log repeats the compiler output three times). The previous user-supplied build showed 135 errors. **These counts describe the uploaded build, not a build of Pass 14.**

## Edited in Pass 14

- `ArmorUpgrade`: resolve the Protection enchantment through the 26.2 registry lookup; extract the byte from its optional value with a default.
- `FurnaceEngineUpgrade`: use `GuiGraphicsExtractor.item()` and `.itemDecorations()` for fuel icons.
- `LiquidEngineUpgrade` and `ElectricEngineUpgrade`: submit tooltips via `setTooltipForNextFrame()`.
- `MovingSound`: adapt the JukeboxSong single-argument lookup; display the record title as a HUD overlay (the exact former now-playing visual effect may differ).
- `ChestUpgrade`, `SupplyCrateUpgrade`, `LiquidEngineUpgrade`, and `JukeboxUpgrade`: spawn dropped items only with a ServerLevel, using the required spawnAtLocation overload. Drops remain server authoritative.

No deliberate changes to inventory data layout, aircraft physics or flight controls.

## Known blockers

Major outstanding compiler and gameplay migration work includes: new NeoForge resource/energy/fluid capabilities, fluid/container persistence, 26.2 item tint sources, aircraft render-state previews, legacy client rendering and mixins, custom workbench/recipe API, and entity overrides. Shooter and payload functionality has not been migrated here.

**Not compile verified:** this environment has Java 21, while the supplied Gradle project requires Java 25. The next GitHub Actions run is needed to confirm each API call and enumerate errors.

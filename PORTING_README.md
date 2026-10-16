# Simple Planes 26.2: Port Pass 2 (uncompiled / not playable yet)

Starting point: Port Pass 1 source and the third GitHub Actions compile log.
Target: Minecraft 26.2 / NeoForge 26.2.0.88 / Java 25.

## Included this pass
- Fix `Level.isClientSide` private field access by using `isClientSide()`.
- Migrate direct vanilla and custom registry value lookups to `getValue(Identifier)`.
- Migrate common `CompoundTag` primitive/compound reads to optional-safe getters.
- Migrate saving/loading for planes, cargo planes, parachutes, workbench, and charging station to `ValueInput`/`ValueOutput`.
- Preserve the existing inventory/upgrade NBT keys, and explicitly retain cargo plane large upgrades in the aircraft's item component.

## Still unresolved
- This is a **source migration pass**, not an independently compiled release.
- The GitHub build still has many more issues, especially rendering, model layers,
  networking, recipes, and optional JEI integration.
- No actual client or server gameplay tests have been performed. No claim that
  the resulting project builds successfully.
- Protect your real game worlds with backups. After a successful build, test on
  a separate NeoForge test server before trying the Youer hybrid server.

## How to continue
Upload the contents of this ZIP into your GitHub repository (replace matching
files) and run Actions > Build Simple Planes 26.2 Port. Upload the new logs ZIP.

References:
https://docs.neoforged.net/primer/docs/1.21.5/
https://docs.neoforged.net/primer/docs/1.21.6/
https://docs.neoforged.net/docs/1.21.8/datastorage/valueio/

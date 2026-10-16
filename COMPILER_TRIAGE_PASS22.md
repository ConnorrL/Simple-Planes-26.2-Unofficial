# Simple Planes 26.2 — Port Pass 22

**Build 22 incoming log:** 40 Java compilation errors and 20 warnings. This is an exact count from the GitHub Actions `compileJava` output, not a local rebuild.

## Source updates in this archive

- `EnergyStorageWithSet`: migrated storage to NeoForge's 26.2 `SimpleEnergyHandler`, retaining the internal legacy-style `receiveEnergy`, `extractEnergy`, `setEnergy`, and stored-energy getters. External capability users can access `EnergyHandler`. The integer NBT save format remains unchanged.
- Charging station: upgraded the entity lookup to `Capabilities.Energy.ENTITY` and wrapped external `EnergyHandler`s through NeoForge's documented legacy compatibility adapter for transfer.
- Electric engine and mod registration: switched energy capabilities to `Capabilities.Energy.BLOCK` / `Capabilities.Energy.ENTITY` with the new handler interface.
- Plane workbench: read custom recipes from the **server** recipe manager via `ServerLevel#recipeAccess()`, and added empty/index-boundary handling. The server continues syncing its result slot to the client.
- Plane landing: changed the fall-damage override to the current `double`-distance signature while preserving the crash calculation.

## Validation and constraints

- ZIP files verified for integrity; source modifications and syntax patterns checked, but **no successful Java 25 / Minecraft 26.2 compilation has been performed here**. This update requires a GitHub Actions build.
- Check charging-station energy transfer, aircraft battery use, and persistence in a copied world before using any live server data.
- The old `ItemHandler` / `FluidHandler` capability paths are still unported and will continue causing compilation errors.
- Rendering event updates, fluid textures, payload entity loading, and projectile-related errors remain in the incoming log. They were not changed in this pass.
- Java 21 is available locally, but the target requires Java 25. No runtime test was performed.

## Incoming compiler errors grouped by file (39 parsed source locations; build reports 40 errors)

- 12: `xyz/przemyk/simpleplanes/upgrades/shooter/ShooterUpgrade.java`
- 8: `xyz/przemyk/simpleplanes/SimplePlanesMod.java`
- 4: `xyz/przemyk/simpleplanes/client/ClientEventHandler.java`
- 2: `xyz/przemyk/simpleplanes/upgrades/engines/liquid/LiquidEngineUpgrade.java`
- 2: `xyz/przemyk/simpleplanes/upgrades/payload/PayloadUpgrade.java`
- 1: `xyz/przemyk/simpleplanes/entities/PlaneEntity.java`
- 1: `xyz/przemyk/simpleplanes/container/PlaneWorkbenchContainer.java`
- 1: `xyz/przemyk/simpleplanes/blocks/ChargingStationBlockEntity.java`
- 1: `xyz/przemyk/simpleplanes/client/ClientUtil.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/engines/furnace/FurnaceEngineUpgrade.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/engines/electric/ElectricEngineUpgrade.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/storage/ChestUpgrade.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/supplycrate/SupplyCrateUpgrade.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/shooter/ShooterModel.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/shooter/LargeShooterModel.java`
- 1: `xyz/przemyk/simpleplanes/upgrades/shooter/HeliShooterModel.java`

## Next build

Push these project files to GitHub, run the existing Actions build with Java 25, and share the new `logs_*.zip`. **Do not use this source on the production Youer server.**

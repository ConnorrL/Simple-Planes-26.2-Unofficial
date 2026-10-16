# Simple Planes Minecraft 26.2 — Port Pass 23

## Input build

GitHub Actions run `logs_105638834316.zip` failed in `compileJava` with **33 Java errors** (down from 40 in the previous log). Warnings about deprecated legacy item handlers remain.

## Changes in this pass

- Added `porting/LegacyInventoryBridge.java` to present existing `ItemStackHandler` contents as a vanilla `Container` through NeoForge's `VanillaContainerWrapper.of(Container)`, which yields the 26.2 `ResourceHandler<ItemResource>` capability.
- Registered `Capabilities.Item.BLOCK` for the workbench and `Capabilities.Item.ENTITY` for the plane entity types.
- Updated the furnace-engine, chest, and supply-crate upgrades to expose their existing inventory handler through the bridge.
- Kept the underlying `ItemStackHandler` instances and established NBT serialization unchanged. This intentionally does **not** rewrite legacy inventory save data or menu handling.

## Validation

- Source-level checks verified intended replacements and preserved files.
- Complete source project is archived with ZIP integrity check.
- **No successful Java 25 / NeoForge compile is asserted**. Run GitHub Actions with this archive to validate the bridge method signatures.
- **Do not put this version on a live server.** Once compilation works, transaction and persistence checks are essential: insertion/extraction, full inventory, recovery after world reload, server restart and simultaneous multi-player access.

## Open issues

The new bridge only migrates item inventory exposure. Fluid capabilities and fluid-containing items still reference `Capabilities.FluidHandler` and old fluid interfaces. Rider rotation, GUI fluid-sprite resolution, aircraft payload serialization and other game-specific interactions still need proper updates. The built-in shooter module also retains outdated 26.2 calls and model methods; it is **unchanged** in this pass.

Do not replace these unresolved features with no-op implementations merely to obtain a build; preserve behavior and test before release.

## Incoming errors by file

| File | Number of errors |
|---|---:|
| `ShooterUpgrade.java` | 13 |
| `SimplePlanesMod.java` | 5 |
| `ClientEventHandler.java` | 4 |
| `LiquidEngineUpgrade.java` | 2 |
| `PayloadUpgrade.java` | 2 |
| `ClientUtil.java` | 1 |
| `FurnaceEngineUpgrade.java` | 1 |
| `ChestUpgrade.java` | 1 |
| `SupplyCrateUpgrade.java` | 1 |
| `ShooterModel.java` | 1 |
| `LargeShooterModel.java` | 1 |
| `HeliShooterModel.java` | 1 |

## Commands

Commit this tree and run the existing GitHub Actions workflow. Upload the generated log ZIP for the next compiler triage. After a successful compile, run separate client and dedicated-server startup tests and use a copy of the world for transfer and storage testing.

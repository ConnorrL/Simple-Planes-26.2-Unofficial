# Compiler triage — Pass 19 (Minecraft 26.2)

Input: `logs_105346665028.zip`. GitHub Actions reported **54 compiler errors and 20 warnings**, down from 56 Java errors in the preceding build. The table below groups 53 distinct file/line/message combinations; multiple errors at the same location may be grouped together. The input is Pass 18, not the untested Pass 19.

## Source edits in this pass

- `PlaneEntity`: restore pick-block behavior using `getPickResult()` in place of removed `getPickedResult(HitResult)`.
- `PlaneEntity`: migrate the aircraft position update method from `absMoveTo` to `absSnapTo`, including the local invocation. The original coordinate and rotation handling is retained.
- `build.gradle`: enable `-Xdiags:verbose` to surface exact signatures and mismatches in subsequent GitHub Actions compiler logs.

These changes are source edits only. JDK 25 and the 26.2 libraries are not available locally; GitHub Actions compilation and gameplay still need verification.

## Java files with compiler errors in input build

| File | Reported errors |
|---|---:|
| `xyz/przemyk/simpleplanes/upgrades/shooter/ShooterUpgrade.java` | 12 |
| `xyz/przemyk/simpleplanes/SimplePlanesMod.java` | 8 |
| `xyz/przemyk/simpleplanes/entities/PlaneEntity.java` | 7 |
| `xyz/przemyk/simpleplanes/client/ClientEventHandler.java` | 4 |
| `xyz/przemyk/simpleplanes/client/render/PlaneItemColors.java` | 4 |
| `xyz/przemyk/simpleplanes/upgrades/engines/liquid/LiquidEngineUpgrade.java` | 4 |
| `xyz/przemyk/simpleplanes/client/ClientUtil.java` | 2 |
| `xyz/przemyk/simpleplanes/upgrades/payload/PayloadUpgrade.java` | 2 |
| `xyz/przemyk/simpleplanes/client/ModBusClientEventHandler.java` | 1 |
| `xyz/przemyk/simpleplanes/container/PlaneWorkbenchContainer.java` | 1 |
| `xyz/przemyk/simpleplanes/blocks/ChargingStationBlockEntity.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/engines/furnace/FurnaceEngineUpgrade.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/engines/electric/ElectricEngineUpgrade.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/storage/ChestUpgrade.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/supplycrate/SupplyCrateUpgrade.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/shooter/ShooterModel.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/shooter/LargeShooterModel.java` | 1 |
| `xyz/przemyk/simpleplanes/upgrades/shooter/HeliShooterModel.java` | 1 |

## Deferred work

- Old NeoForge capability constants and handler interfaces cannot be renamed in isolation. Keep energy, fluids, and inventory interop for a coordinated migration to `ResourceHandler`/`EnergyHandler`, including transactions.
- Client living-entity rendering needs proper state extraction rather than reading the original entity from render callbacks.
- Tint registration requires `ItemTintSource` and appropriate item model resources; do not disable material tinting just to compile.
- Aircraft entity overrides involving damage, seating, falling and collision need direct API signature verification to avoid breaking vehicle behavior.
- Custom workbench recipes are server authoritative; a client recipe-list sync will be necessary.
- Preserve existing data formats; inventory and tank load/save must be tested in a separate world before release.

## Next steps

Commit this source, run the GitHub Actions build, and upload the compilation log. Do not put this unfinished build on a live Youer world.

## Public reference documentation

- [NeoForge 26.2 entity API documentation](https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/client/player/abstractclientplayer)
- [NeoForge current recipe API documentation](https://docs.neoforged.net/docs/resources/server/recipes/)
- [NeoForge transfer rework](https://neoforged.net/news/21.9-transfer-rework/)
- [NeoForge 26.2 rendering event API](https://aldak.netlify.app/javadoc/26.2.x/net/neoforged/neoforge/client/event/renderlivingevent)

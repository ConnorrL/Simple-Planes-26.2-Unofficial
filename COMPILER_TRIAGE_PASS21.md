# Simple Planes 26.2 — Build 21 / Port Pass 21

Input: `logs_105353543975.zip` (Actions `build/5_Compile.txt`).

**Build 20 reported 46 Java errors and 20 warnings. It did not compile.**

## Changes in this source pass

- `PlaneEntity.interact` now accepts hit location (`Vec3`). Its mounting, wrench, and upgrade logic was preserved.
- `PlaneEntity.canBeCollidedWith` now accepts the other entity.
- Preserve the aircraft-specific `isInvulnerableTo` logic as a private method called by `hurtServer`, delegating to `isInvulnerableToBase` for vanilla rules. This avoids overriding the removed `Entity` method.
- Update `IClientFluidTypeExtensions.getStillTexture` to pass the `FluidStack`.
- Add `FluidTankTagAdapter` to serialize the liquid tank using `TagValueInput`/`TagValueOutput` without stripping the original engine-upgrade compound tag envelope.

## Unresolved important work

- Old `Capabilities.ItemHandler`, `FluidHandler`, and `EnergyStorage` are gone. Migrate with `ResourceHandler<ItemResource>`, `ResourceHandler<FluidResource>`, and `EnergyHandler` wrappers rather than blind renames. NeoForge 26.2 registers capability types whose return values differ from the legacy handlers.
- Passenger rotation in `ClientEventHandler` uses the old `RenderLivingEvent.getEntity()` method; the modern event exposes a render state and needs a new association / render-state design.
- Workbench recipe list is no longer obtained via `Level.getRecipeManager()`: require a client/server-safe recipe synchronization design.
- Other entity behavior and existing payload restoration still need adaptation.
- Legacy fluid-tank records from Minecraft 1.21.1 might have a different structure. Test load/restart on a copied world before trusting persistence.
- Other upgrade and renderer errors still need migration; do not silently disable gameplay to force compilation.

## Error count by source file in incoming log (not a post-patch count)

| Java file | Compiler errors |
|---|---:|
| `ShooterUpgrade.java` | 13 |
| `SimplePlanesMod.java` | 8 |
| `PlaneEntity.java` | 5 |
| `ClientEventHandler.java` | 4 |
| `LiquidEngineUpgrade.java` | 4 |
| `PayloadUpgrade.java` | 2 |
| `PlaneWorkbenchContainer.java` | 1 |
| `ChargingStationBlockEntity.java` | 1 |
| `ClientUtil.java` | 1 |
| `FurnaceEngineUpgrade.java` | 1 |
| `ElectricEngineUpgrade.java` | 1 |
| `ChestUpgrade.java` | 1 |
| `SupplyCrateUpgrade.java` | 1 |
| `ShooterModel.java` | 1 |
| `LargeShooterModel.java` | 1 |
| `HeliShooterModel.java` | 1 |

## Validation status

- Static replacements checked against expected source strings.
- Source copied from Pass 20.
- ZIP integrity checked.
- **No Java 25 / NeoForge 26.2 compilation performed locally**.
- Next action: GitHub Actions build with the changed project files.
- Do not load on the production Youer world.

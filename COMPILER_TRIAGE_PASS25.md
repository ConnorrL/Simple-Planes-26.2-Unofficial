# Pass 25 — GitHub compiler triage

## Incoming build
- GitHub Actions log: `logs_105648621229.zip`
- `compileJava`: **25 errors, 20 warnings** (previous run: 27 errors).
- Source base: Pass 24.

## Changes in this pass
- Updated both entity fluid capability registrations/queries to `Capabilities.Fluid.ENTITY`.
- Added `LegacyFluidBridge`, a transaction-snapshot adapter for the existing `FluidTank`. This shares the same tank used by the save format and HUD, rather than registering the old `IFluidHandler` directly as a new resource handler.
- Replaced the removed item capability query with NeoForge's supported legacy adapter `FluidUtil.getFluidHandler(itemStack)` for single-item containers.
- Updated the liquid HUD sprite to resolve conventional `<namespace>:block/<fluid>_still` textures. This is a **temporary visual fallback**. Nonstandard custom-fluid sprites may not render correctly until the full 26.2 fluid model API is integrated.

## Still unresolved
- `ClientEventHandler` still uses the pre-render-state API to access passenger entities. Minecraft 26.2 `RenderLivingEvent` carries a render state, not the live entity. Replacing this code without extracting appropriate per-entity data risks breaking rider orientation.
- The projectile/shooter upgrade and three of its model classes need dedicated 26.2 migration.
- Fluid tank and container behavior, transaction rollback, fill/drain rules, and custom-fluid GUI appearance are **untested**.
- This ZIP is not compile-verified (local toolchain provides Java 21; GitHub uses Java 25).

## Follow-up tests once compilation succeeds
1. Verify liquid tank fill, drain, transaction rollback, and conversion of buckets and modded fluid items.
2. Check fluid contents persist after restart, and fluid item remainders are not lost.
3. Test passenger rotation with several players and with the camera in first and third person.
4. Test all previously documented features on a **copy** of the world before Youer deployment.

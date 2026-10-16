# Minecraft 26.2 / NeoForge 26.2.0.88 — compiler triage pass 24

## Incoming build

- GitHub Actions log: `logs_105640510367.zip`, compile step `build/5_Compile.txt`.
- **27 compiler errors** (previous build: 33). The compiler diagnostics are duplicated in the expanded stacktrace; the unique compiler block contains 27 errors.
- 9 affected source files: `ShooterUpgrade.java` (13), `ClientEventHandler.java` (4), `SimplePlanesMod.java` (2), `LiquidEngineUpgrade.java` (2), `PayloadUpgrade.java` (2), `ClientUtil.java` (1), and 3 model classes (1 each).

## Changes in this source archive

1. In `ClientUtil.renderLiquidEngineFluid`, use the supported zero-argument `IClientFluidTypeExtensions.getStillTexture()` method. In the incoming log, `getStillTexture(FluidStack)` does not exist for the resolved API.
2. In `PayloadUpgrade.dropAsPayload`, use `EntityType.loadEntityRecursive(EntityType, CompoundTag, Level, EntitySpawnReason, EntityProcessor)` instead of the removed `EntityType.create(Level)` and `Entity.load(CompoundTag)`. Keep payload NBT, and spawn only on the server, with null guards. The reason `EntitySpawnReason.COMMAND` is used to make the deserialization pathway explicit; assess any specialized spawn behavior in gameplay tests.

## Remaining work

The projectile/attachment model code still uses 1.21.1 source signatures. Some fluid capability registration and item conversion code uses `Capabilities.FluidHandler`, removed in 26.2. Player passenger rendering still references `RenderLivingEvent#getEntity`, removed with rendering state migration; this needs a state-aware rewrite rather than dropping the rider rotation effect. After compilation, verify all payload entity types, world data persistence, fluid transfer compatibility, passenger rendering, and upgrade controls on a disposable world before trying Youer.

### Validation status

- Static source replacement assertions and ZIP CRC/integrity checks only.
- No successful `compileJava` verified here; the container has Java 21, while this project requires Java 25, so use GitHub Actions for compilation.
- No gameplay/server compatibility testing.

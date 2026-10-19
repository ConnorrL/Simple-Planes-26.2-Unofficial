# Simple Planes 26.2 — Port Pass 29: black screen/model reload fix

## User-reported symptom
Client turns black after Mojang loading screen. `latest.log` includes:

```
Failed to create model for simpleplanes:parachute
No model for layer simpleplanes:parachute#main
```

The crash occurs during EntityRenderDispatcher resource reload while registering the parachute entity renderer.

## Source change
`src/main/java/xyz/przemyk/simpleplanes/client/PlanesModelLayers.java`

The five entity renderer factory lambdas used `Minecraft.getInstance().getEntityModels()` captured during `RegisterRenderers`. That model set can be stale when entity renderers are rebuilt during resource reload.

Removed the early model-set capture and use `context.bakeLayer(...)` supplied to each factory. This affects 13 layer bakes in the plane, large plane, cargo plane, helicopter, and parachute renderers.

All model layer declarations and registrations remain unchanged; the `AddLayers` handler still receives its appropriate event model set. The original model geometry/assets are unchanged.

## Next checks
1. Build on GitHub Actions.
2. Launch in a fresh test instance with NeoForge 26.2.0.90 and only Simple Planes (avoid interference from other rendering mods initially).
3. Confirm the main menu appears.
4. Test a disposable world and spawn one plane and parachute.
5. If the black screen persists, upload the newest `latest.log`, especially new `Failed to create model` exceptions.

## Validation
Source-level replacement and ZIP archive integrity checked. Not compiled or game-tested here; Java 25 and Minecraft build dependencies are not available in this environment.

# Minecraft 26.2 runtime startup fix — Pass 28

## Incoming crash

`Mixin [simpleplanes.mixins.json:CameraMixin] FAILED during APPLY` caused by
`@Shadow field partialTickTime was not located in the target class net.minecraft.client.Camera`.

## Patch

- Removed all three fragile `@Shadow` references in `CameraMixin.java`:
  `partialTickTime`, `eyeHeight`, and `eyeHeightOld`.
- The camera-position interception now uses the vanilla interpolated
  `setPosition(x, y, z)` values as a baseline and adjusts them using the
  plane's orientation and the camera entity's public eye-height API.
- Added `require = 0` to the `@WrapOperation` injection, so a future internal
  change to `Camera.setup` will not alone prevent the game from launching.
- Preserved the player-on-plane and first-person checks; other cameras call
  the vanilla operation unchanged.

## Limitations and testing

This is a compatibility-first change. First-person camera movement might not
match the original implementation exactly, particularly during crouch/pose
transitions and sharp rotations. If the injection point no longer exists in
26.2, the vanilla camera will be used until a proper replacement hook is
added.

Source/ZIP validation only. **No local Java 25 + NeoForge 26.2 compilation or
runtime launch was performed.** Rebuild in GitHub Actions before testing.

Suggested next test: singleplayer isolated instance with the compiled JAR and
matching NeoForge 26.2 version; then enter and fly a plane in first person and
third person. If a different crash occurs, inspect the new crash report.

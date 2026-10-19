# Pass 30 — Plane passenger visual pitch/roll sync (Minecraft 26.2)

## Symptom

The game launches and the aircraft/upgrades work, but a seated player's third-person avatar stays upright as the plane pitches up/down (and may also fail to follow its bank/roll).

## Root cause identified in the source

The 1.21.1 version applied the plane's interpolated quaternion in `RenderLivingEvent.Pre` and restored the matrix in `RenderLivingEvent.Post`. In 26.2, player/avatar rendering has its own sibling `RenderPlayerEvent.Pre` and `RenderPlayerEvent.Post` classes; those do not extend the corresponding `RenderLivingEvent.Pre/Post` classes. Pass 26's generic living-entity rendering hooks therefore did not explicitly handle players.

Also, NeoForge 26.2 provides `registerAvatarEntityModifier` specifically for `AvatarRenderer` player render-state extraction. Previous registration covered only the generic `LivingEntityRenderer` path. The new avatar-specific registration makes the player render-state snapshot explicit.

## Changes

- Added `RenderPlayerEvent.Pre<?>` and `.Post<?>` handlers in `ClientEventHandler.java` to push/pop the exact same interpolated plane quaternion transform used for other living passengers.
- Factored the shared push/pop into helper methods, to keep the player's pose and other passengers consistent.
- Added `AvatarRenderStateModifier` in `ModBusClientEventHandler.java`; it captures the plane quaternion, vehicle yaw, and upside-down head adjustment during avatar render-state extraction.
- Retained the original non-avatar living-entity modifier for seated mobs.
- Clears old render-state data when a passenger dismounts; no flight controls, physics, inventories, or save formats were changed.

## Testing plan

1. Compile with GitHub Actions and replace the previously compiled Simple Planes JAR (only one version in the mods folder).
2. In third-person view, fly level, pitch nose up, pitch down, and bank/roll. Observe avatar's full body, not only its head or camera view.
3. Test a passenger in another seat, another player watching in multiplayer, and a non-player passenger if supported.
4. Dismount and check that body orientation returns to upright; reenter and repeat. Compare first-person camera and third-person controls.
5. Confirm no new render errors in `latest.log` and test with only NeoForge + Simple Planes before testing with other player-rendering mods, especially Vivecraft.

## Validation

Source-only patch; not compiled or tested in the runtime environment that contains Java 25 and NeoForge 26.2.0.90. Next GitHub Actions run must confirm compilation and gameplay behavior.

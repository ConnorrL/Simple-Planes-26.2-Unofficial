# Pass 31 — passenger orientation and seat anchoring (experimental)

## Reported problem
The rider pitches/rolls more slowly than the airplane, and slides off-center while the aircraft changes orientation. The aircraft and upgrades already load in Minecraft 26.2.

## Changes
- `ClientEventHandler`: removed the old 0.375-block rider rotation pivot. The avatar now rotates about its seat/feet origin to avoid a sideways translation while banking. Keeps the same mirrored plane quaternion used by earlier passes.
- `ModBusClientEventHandler`: uses the avatar's actual interpolated `bodyRot` (the vanilla renderer's body yaw), not a separately interpolated aircraft yaw, for yaw compensation. Both visual rotations derive from the same plane quaternion as `PlaneRenderer.extractRenderState`.
- `PlaneEntity`: extracted the *existing* three seat offsets into a shared helper (no change to original tick-time positions) and added interpolated seat world coordinates, using the same slerped quaternion as the plane render state.
- `ModBusClientEventHandler`: sets `state.passengerOffset` to correct the difference between the linearly interpolated rider world position and its curved/rotating seat position at each render frame. Does not move entities, change packets, or alter persistent data.

## Caveats
- This is a source-level, uncompiled patch. GitHub Actions must verify compilation; third-person tests must verify actual alignment.
- Multiplayer interpolation and modded avatar renderers (such as Vivecraft) may override rotations and require additional compatibility work.
- The plane's interpolated seat position follows the mod's original transformPos Euler conversion; extreme spins should be tested.

## Test plan
1. Compile Pass 31 on GitHub Actions; use only one Simple Planes JAR in a separate test client.
2. In third person, pitch gently upward and downward, then bank both directions, then do a fast sustained turn. Check that feet remain at the seat and body tracks the plane with no lag.
3. Test each available seat on a multi-seat plane with a second player watching. Compare first person and third person.
4. Exit the plane and ensure body orientation resets normally. Test with Vivecraft disabled first if behavior differs by modpack.
5. If a render glitch or crash occurs, send latest.log and a short video of the pose/seat offset.

# Simple Planes 26.2 – Experimental Takeoff Patch

**Status:** source-level fix, not compiled or gameplay-verified in this environment.

## Problem being investigated

The plane accelerates on the ground but never climbs. The existing port inherited a motion block which calls `setOnGround(true)` when the pilot is pitching or the plane is moving quickly. On a newer engine this can cause the craft to remain in ground state. The port also relies entirely on velocity reorientation to transition from taxiing into climb, which can fail to produce positive vertical velocity when grounded.

## Changes

`PlaneEntity.java` only:

1. Remove the unconditional pre-movement `setOnGround(true)` call when speed or pitch is high; use actual ground collision state and clear ground hysteresis once moving up without ground collision.
2. Add a small, **speed-gated** takeoff lift to `tickMotion` when the pilot holds pitch-up, the aircraft is powered, and its horizontal speed exceeds the existing `takeOffSpeed` setting. Caps positive takeoff velocity at 0.09 blocks/tick to avoid a sudden leap. Does not change wood color rendering, recipes, or workbench handling.

## Usage and tests

1. Build using JDK 25 with `gradlew.bat build` on Windows or `./gradlew build` on Linux, with game/NeoForge dependencies available.
2. Put the **same newly built JAR** on the client and test Youer 26.2 server; remove the old Simple Planes JAR first. Back up your world.
3. Install a powered engine and fuel/charge. With a flat, unobstructed runway, tap **Up Arrow** until throttle is high, accelerate, then **hold W** to pitch up; S is pitch-down. Check flight in both Creative and Survival.
4. Also test landing, banking, and that different plank types still preserve their appearance. If the plane accelerates but does not pitch its nose upward at all, look for keybinding conflicts or pitch network packet issues; this patch assumes pitch input arrives on the server.

## Verification and limitations

- The patch was generated from the previously supplied **Simple Planes 26.2 Workbench Fixed Source** and retains its workbench changes.
- Applied narrow textual/static checks of the modified source, but **no compilation or live test** has been performed: the required JDK 25 and project dependencies are not installed in this environment.
- This is a hypothesis-driven compatibility patch, not a confirmed diagnosis without a gameplay log/video.

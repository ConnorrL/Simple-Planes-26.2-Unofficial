# Simple Planes Minecraft 26.2 — Port Pass 26

Incoming run: `logs_105650723052.zip` (`compileJava`: **20 errors, 20 warnings**; previous run 25 errors).

## Source edits

- ShooterUpgrade: moved projectile imports to Minecraft's 26.2 subpackages, replaced obsolete `Util.make`, accessed entity PRNG through getter, adapted eye target from block position to Vec3, and migrated the GUI draw call.
- ShooterUpgrade: routed single-slot inventory serialization through the existing `InventoryTagAdapter`; exposed storage via `Capabilities.Item.ENTITY` and `LegacyInventoryBridge`; made item drop server-only.
- ShooterModel / LargeShooterModel / HeliShooterModel: updated `setupAnim` to `PlaneRenderState` single-argument override, matching the migrated entity models.
- ClientEventHandler / ModBusClientEventHandler: replaced old living-entity render hooks with a NeoForge render-state modifier, recording plane quaternion/yaw onto an extension ContextKey. Pre/post hooks now read that state and push/pop the pose stack. This preserves the intent of passenger plane roll rendering without mutating the live passenger entity during rendering.

## Important test points

This archive passed ZIP/regex sanity checks only; it was not built against the Minecraft 26.2 Java 25 toolchain here. Run the GitHub Actions workflow, and provide its next compiler log. If it compiles, test the shooter inventory save/reload, controls, projectiles, firework/ender eye visuals, inverted-passenger orientation, and server-only loading on a copied world before Youer deployment. Check render hooks in first/third-person and with non-player plane passengers. Existing legacy save data must be tested for compatibility.

Minecraft 26.2 Java/API references: NeoForge 26.2 RenderLivingEvent, RegisterRenderStateModifiersEvent, SmallFireball, and NeoForge documentation's entity renderer state modifier section.

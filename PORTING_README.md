# Simple Planes 26.2: Port Pass 3 — not compiled, not playable yet

Target: Minecraft 26.2 / NeoForge 26.2.0.88 / Java 25 / eventual Youer.

Changes against Port Pass 2:
- Migrate plane/parachute item use() from InteractionResultHolder to InteractionResult.
- Migrate plane and description item tooltip signatures to TooltipDisplay/Consumer.
- Update PlaneWorkbenchContainer.clicked to ContainerInput and onCraftedBy signatures.
- Update plane-workbench custom Recipe interface signatures and codec result accessor.
- Temporarily exclude the optional JEI integration classes from Java compilation (their source remains in the archive). JEI recipe browsing is unavailable until that integration is ported.

IMPORTANT:
- This archive was edited and syntax/ZIP-inspected, but has NOT passed a Java 25 NeoForge 26.2 compile or runtime test.
- Major remaining work: plane and parachute renderers (the old buffer API was removed), entity saving/interaction, recipe synchronization between server/client, NeoForge capability migration, networking and datapack reloads.
- The workbench still calls the removed level.getRecipeManager() method and needs a proper modern server/client recipe-sync design; avoid replacing it with a server-only call that leaves the client without recipes.
- Use a separate test world after the build eventually succeeds; do not install into the production Youer server yet.

Next: commit the contents to GitHub, run Actions, upload the latest logs ZIP.

API references:
https://docs.neoforged.net/docs/items/interactions/
https://docs.neoforged.net/docs/resources/server/recipes/custom/
https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/item/crafting/recipe


## Port pass 4 (based on logs_105253683562.zip)

- Removed the obsolete `bus` property of all three `@EventBusSubscriber` usages; 26.2 auto-detects event bus.
- Adapted the workbench recipe serializer to Minecraft 26.2's `RecipeSerializer` record, retaining the existing JSON and network codecs.
- Registered a typed `KeyMapping.Category` for aircraft key bindings.
- Corrected the moved `ShulkerModel` imports and non-generic type references.

**Still blocked:** Aircraft rendering uses the obsolete immediate rendering pipeline and must be redesigned for modern entity render states/submission; the energy, item and fluid transfer/capability API has changed, networking and save/load remain incomplete. No successful build or in-game test has been established.

API references:
- https://docs.neoforged.net/docs/concepts/events/
- https://docs.neoforged.net/docs/resources/server/recipes/custom/
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/item/crafting/recipeserializer
- https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/client/keymapping.category


## Port pass 5 (based on logs_105255533209.zip)

- Updated `SimplePlanesDatapack` from the removed `AddReloadListenerEvent` to `AddServerReloadListenersEvent`. Its underlying data reload listeners still require migration (old `SimpleJsonResourceReloadListener` API).
- Converted the `Quaternionfc` networking codec to the mutable `Quaternionf` used by rotation packets.
- Migrated `PlaneEntity.hurt` to server-only `hurtServer`, including the corresponding server-aware invulnerability override.
- Updated the aircraft's entity drop gamerule lookup, server-only kill/drop APIs, null-safe dropped item handling, and `ValueInput.getFloatOr` for speed serialization.
- Added `ParachuteEntity.hurtServer` (non-damageable, matching its prior absence of custom damage behavior) and guarded parachute item drops against client-side execution.

**No successful compilation or runtime test yet.** The 250-error reporting limit was reached before all errors could be shown. Rendering, GUI, data reload listeners, capabilities, and networking remain major tasks. Port pass 5 does not constitute a working mod.

References:
- https://docs.neoforged.net/docs/entities/
- https://docs.neoforged.net/docs/resources/reloadlisteners/
- https://aldak.netlify.app/javadoc/26.2.x/net/neoforged/neoforge/event/addserverreloadlistenersevent

- Updated 11 client UI/input serverbound packet calls to `ClientPacketDistributor.sendToServer`, which replaced the serverbound method on the common `PacketDistributor`. Common entity-side calls still require client-safe reorganization.


## Port pass 6 (based on user GitHub Actions build log `logs_105257299732.zip`)

The latest build still fails during `compileJava` at the 250-error reporting limit.
Changes in this pass:

- Adapt both datapack listeners to the 26.2 typed `SimpleJsonResourceReloadListener<JsonElement>` API. A passthrough codec keeps existing JSON-entry parsing and paths intact.
- Replace all 16 removed `Items.*_BANNER` constant lookups with `Blocks.*_BANNER.asItem()`.
- Align `PlaneEntity.Q` with the serializer's `Quaternionfc` type. Copy assigned quaternions to avoid mutable synced-data aliasing.
- Implement an entity-side local-pilot test via the first passenger's `Player.isLocalPlayer()`, without Minecraft client-only class references.

**The source has been statically checked but is NOT compiled or runtime-tested.** The local runtime has Java 21 and no Gradle cache for Minecraft 26.2, while the GitHub build needs Java 25.

### Major remaining work

- Migrate entity and upgrade rendering to 26.2's render-state pipeline and rewrite old model overrides.
- Migrate `Capabilities.ItemHandler/FluidHandler/EnergyStorage` to the new transactional resource and energy handlers (do **not** blindly rename the fields; old and new return types differ).
- Migrate item storage persistence and screen/menu rendering while preserving aircraft inventories.
- Fix remaining gameplay API changes and test a normal NeoForge server/client before Youer.

Use GitHub Actions for compile diagnostics; do not install this as a functional server mod yet.


## Port pass 7 (2026-10-15)

Source: GitHub Actions log `logs_105263020737.zip`, failing at `:compileJava` with the compiler stopping at `-Xmaxerrs 250`.

Targeted, documented changes (not compiled in this environment):

- Replaced 16 removed `Blocks.*_BANNER` fields with item registry lookups by stable `minecraft:<color>_banner` identifiers. 26.2 consolidated the color fields into collections.
- Updated the banner render block's default state lookup using `minecraft:black_banner` (the renderer itself **still** requires migration).
- Changed `TagParser.parseTag` to `TagParser.parseCompoundFully` in the payload reload listener.
- Changed plane placement from `EntityType#create(Level)` to `EntityType#create(Level, EntitySpawnReason.SPAWN_ITEM_USE)`, with a null guard.
- Moved client-to-server packet sending for plane entities into `client/ClientPlanePackets` using NeoForge's `ClientPacketDistributor` (client-only bridge). Confirm dedicated-server classloading safety at runtime.

### Still required

- Complete redesign of the aircraft rendering submission path (the legacy `MultiBufferSource` and `renderToBuffer` patterns were removed/changed).
- Migrate all item/fluid/energy capability code to 26.2 resource/transaction handlers, preserving saved inventories and energy.
- Update entities, GUI screens, recipe manager, and all other compile errors; the next build may show further errors.
- Validate gameplay on standard NeoForge client + server **before** testing Youer. Do not add this uncompiled code to the production server.

This pass only makes specific verified API replacements; it is **not** a build-tested or functioning port.


## Port pass 8 — GUI extraction and base aircraft models

Source: `logs_105264392878.zip`; `compileJava` failed at the `-Xmaxerrs 250` compiler limit.

This pass begins the documented 26.2 renderer migration while preserving original assets:

- Updated **13 core aircraft/parachute model classes** from `EntityModel<Entity>` to `EntityModel<EntityRenderState>` subclasses, passing baked roots to constructors. Removed overrides of the now-final `renderToBuffer`; base models render all baked root parts.
- Added `client/render/state/PlaneRenderState` and `ParachuteRenderState` as basic rendering snapshots. Plane state provides a field for future propeller animation extraction.
- Updated the four primary inventory/workbench/upgrade/chest screen classes for `extractBackground` and rendering pipelines; migrated most old `render`/`renderBg` and HUD-screen tooltip calls to 26.2 API.
- Kept all plane geometry, texture assets, inventory backend and game logic.

**Not complete:**

- `PlaneRenderer` and `ParachuteRenderer` still use the old direct rendering API. Their old generics will not accept the new model state classes until renderer submission is rewritten; `PlaneRenderState.propellerRotation` has not yet been populated. Models cannot display correctly yet.
- `ModifyUpgradesScreen.renderEntityInInventory` uses a removed direct entity dispatcher path. It needs a `GuiGraphicsExtractor#entity` render-state-based implementation. The method is intentionally preserved for this next stage, not silently disabled.
- Upgrade model classes, tint source registration, capability APIs, fuel logic, workbench and other entity methods still need migration.
- Compilation **not verified**; the working environment only provides Java 21, while the 26.2 Gradle build needs Java 25. Rerun GitHub Actions with this source and upload the result.
- Do not install this uncompiled version on the production Youer server.

References: https://docs.neoforged.net/docs/entities/renderer/ and https://docs.neoforged.net/docs/rendering/screens/ .


## Port pass 9 (build log `logs_105266693928.zip`)

The build reached `compileJava` but stopped at the compiler's 250-error display cap.
The 26.2 render-state conversion is NOT complete. This is not a working binary.

Changed in this pass:
- Fixed `extractBackground` access in four container screens (the 26.2 compiler
  requires public visibility for these overrides).
- Replaced two unbound variables in `PlaneInventoryScreen` with `mouseX`/`mouseY`.
- Migrated parachute and plane-workbench stored `ItemStackHandler` contents to
  `serialize(ValueOutput.child(...))` / `deserialize(ValueInput.childOrEmpty(...))`.
  Existing parent keys `items` and `input` remain unchanged.
- Added `porting.InventoryTagAdapter`, a bridge from the 26.2 `ValueInput` API
  back to `CompoundTag` for legacy upgrade save methods. Applied it to chest,
  supply crate, furnace and liquid engine inventory payloads. Their wrapping
  upgrade tags/keys remain unchanged.
- Updated furnace fuel checks to pass the `Level.fuelValues()` argument; the
  fuel slot now retains its `Level` for this purpose.
- Corrected the furnace engine's item drop to supply a `ServerLevel`.

Remaining major work:
- Rendering: entity `EntityRenderState` extraction/rendering, `Model` layering,
  old `MultiBufferSource`, `RenderType`, item foil buffers, GUI aircraft preview.
- Full migration of the NeoForge item/fluid/energy capabilities, and any final
  deprecated handler replacements.
- Custom recipes and recipe manager access (do not drop crafting behavior).
- Other entity, block and item 26.2 overrides, datapack registrations, HUD
  blit calls, and network synchronization.
- World-save roundtrip tests for cargo planes, upgrades, and parachutes.

Validation: static source replacements and ZIP CRC check only. This environment
has Java 21, not the required Java 25, and lacks the NeoForge build cache, so a
successful compile has NOT been established. Run the existing GitHub Actions
workflow and send its logs.

Relevant public API references:
https://aldak.netlify.app/javadoc/26.2.x/net/neoforged/neoforge/items/itemstackhandler
https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/level/storage/valueoutput
https://aldak.netlify.app/javadoc/26.2.x/net/minecraft/world/level/storage/tagvalueinput
https://docs.neoforged.net/docs/rendering/screens/
https://github.com/neoforged/NeoForge/blob/26.2.x/src/main/java/net/neoforged/neoforge/common/extensions/IItemStackExtension.java


## Port Pass 10 (2026-10-15)

Based on `logs_105274094375.zip`. Fixed registration API usage for data components, entity types, block entities, creative tab material lookups, custom recipe result codec, and server datapack reload listeners. See `COMPILER_TRIAGE_PASS10.md` for the grouped compiler error summary. **Not compile tested; more errors are expected.**


## Pass 11 — Model API migration (UNTESTED)

- Migrated 39 upgrade model classes to `EntityModel<PlaneRenderState>` with `super(root)` and single-argument `setupAnim`. Removed overrides of final `Model.renderToBuffer`; the baked root now draws all children.
- Migrated the core plane meshes (hull, propeller and metal) and parachute cloth mesh to 26.2 `EntityRenderer#extractRenderState` and `#submit` with `SubmitNodeCollector#submitModel`.
- Kept original asset files and Java flight/gameplay logic.
- **Incomplete:** Upgrade overlays, cargo barrels, animated equipment and banners are NOT submitted by the new renderer, so they may be invisible even if this branch eventually compiles. Non-plank aircraft material texture lookups currently use oak as fallback. Restore proper dynamic sprite lookup before release.
- NeoForge item/fluid/energy capabilities, workbench recipes, GUI and tint sources still need migrating. Expect many compile errors. No successful build or playtest has been performed.
- No live server testing: test with an isolated 26.2 NeoForge client and server first.


## Pass 12: upgrade render-state preparation (not compiled)

* Copies attached upgrade IDs in `PlaneRenderer.extractRenderState`, keeping entity access outside `submit`.
* Uses `SubmitNodeCollector.submitModel` for the existing upgrade models, plus seats and wooden components, with variant-specific textures and null guards.
* Archives the 12 legacy immediate-render overrides and old BannerModel source in `PORTING_LEGACY_RENDERING/`; their corresponding incompatible 1.21.1 source definitions are removed from compilation. Gameplay methods, inventories, packets and recipes are preserved.
* Dynamic banners, folding parts, cargo block models, armor-window tint, and other specialized visual features **still require a correct render-state rewrite** and are not currently submitted.
* No build or runtime verification was possible locally (JDK 21 here; project targets JDK 25). GitHub Actions is needed to validate the compiler result. The 250-error limit may hide other problems.


## Pass 13 — HUD and engine model compatibility

The 2026-10-16 uploaded GitHub Actions log reports 135 Java compilation errors and 20 warnings, rather than 250. This pass migrates 19 legacy HUD and inventory texture draws to `RenderPipelines.GUI_TEXTURED` and fixes seven legacy engine/booster `setupAnim` declarations. The aircraft health overlay no longer uses the removed `Gui.rightHeight` field. Original 256x256 textures and model geometry are retained. This source remains uncompiled and is not a gameplay release. See `COMPILER_TRIAGE_PASS13.md`.


See `COMPILER_TRIAGE_PASS14.md` for the latest log summary and changes.

## Port Pass 15 (2026-10-16)

See `COMPILER_TRIAGE_PASS15.md`. This is a source-only migration pass, not a successfully built JAR.


## Pass 16 — 80-error build, 2026-10-16
See `COMPILER_TRIAGE_PASS16.md`. The four targeted changes address item save typing, armor item enchantability, and the workbench block-entity removal lifecycle. Compilation and gameplay are not yet verified. Do not use on a live server.


## Pass 17 (Oct 16, 2026)
See `COMPILER_TRIAGE_PASS17.md` for source edits, outstanding API migrations, and validation status.


## Pass 18 — October 16, 2026
See `COMPILER_TRIAGE_PASS18.md`. Latest input build had 56 errors; these fixes have not yet been compiled.


## Pass 19 — October 16, 2026
Input build: 54 errors. Fixed aircraft picking and absolute movement mapping; enabled verbose javac diagnostics. See `COMPILER_TRIAGE_PASS19.md`. No successful compile yet.


## Pass 20
Migrated plane-item tints and 26.2 item definitions (all existing item model names) and fluid texture atlas access; see `COMPILER_TRIAGE_PASS20.md`. Source changes have not been compiled here.


## Port pass 21 (build log `logs_105353543975.zip`)

Build reported 46 errors, 20 warnings. This is a precompilation API pass; changes are documented in `COMPILER_TRIAGE_PASS21.md`. It has **not** compiled. Test new fluid tank serialization with both new and copied old saves before considering server use.


## Port Pass 22

See `COMPILER_TRIAGE_PASS22.md`. Incoming build: 40 errors, 20 warnings. Energy storage, workbench server recipes, and plane landing API migrated; no successful compilation or gameplay testing yet.


## Pass 23 (2026-10-16)

Item inventory capability migration starts with a `Container`-backed `ResourceHandler` adapter that preserves existing `ItemStackHandler` save data and menu slots. See `COMPILER_TRIAGE_PASS23.md`. Requires compilation and transaction tests; fluid and rendering work remain.

# Pass 27 — client registration crash fix

First successful compilation (Pass 26) exposed an early loader crash in the Minecraft 26.2 NeoForge client:

- `NullPointerException: Block id not set`
- `NullPointerException: Item id not set`
- `Trying to access unbound value: ResourceKey[minecraft:block / simpleplanes:plane_workbench]`

## Changes

- `SimplePlanesBlocks` supplies `ResourceKey<Block>` to `BlockBehaviour.Properties.setId` before construction for both the plane workbench and charging station.
- `SimplePlanesItems` supplies `ResourceKey<Item>` to `Item.Properties.setId` before constructing all 21 registered items (including both `BlockItem`s and aircraft/upgrade items).
- Both block items use `useBlockDescriptionPrefix()` for block display-name compatibility.
- No gameplay data, entity physics, textures, inventory formats, or event lifecycle code changed.

## Validation and next checks

- Source replacements and archive integrity have been checked, **but this revision has not been Java-25 compiled** in this environment.
- Commit the updated project, run GitHub Actions, and extract the new JAR. Remove the previous Simple Planes JAR before loading the test client.
- Try an isolated NeoForge 26.2.0.88 client instance; send the full crash report or latest.log if errors persist.
- An ordinary successful `gradle build` does not execute mod initialization, so it cannot detect these registry crashes.
- Do not add the mod to an existing Youer world until singleplayer and a separate NeoForge dedicated server work.

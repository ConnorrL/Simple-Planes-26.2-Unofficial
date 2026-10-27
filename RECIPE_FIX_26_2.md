# Simple Planes 26.2: crafting recipe loading fix

## Problem and fix

All 22 recipe JSON files were still using pre-1.21.2 Minecraft ingredient
objects (`{"item": "minecraft:..."}` and `{"tag": "c:..."}`). Minecraft
26.2 needs **string ingredients** (`"minecraft:..."` and `"#c:..."`).
Those old JSON ingredient objects cause the recipe parser to reject recipes,
so the crafting recipes do not appear or work at the crafting table or in the
Plane Workbench.

Fixed all 60 ingredient definitions across:

- 18 vanilla crafting table recipes (including both healing variations)
- 4 custom `simpleplanes:plane_workbench` recipes (plane, large plane,
  cargo plane, helicopter)

Retained original patterns, ingredient tags and amounts, output item IDs, and
output stack counts. Existing `PlaneWorkbenchRecipeSerializer` already uses
`Ingredient.CODEC`, so no changes to Java or registration code are needed for
this ingredient format fix.

## Validate JSON without Minecraft

With Python 3 installed, run from the project's root directory:

```sh
python validate_recipes.py
```

This checks JSON parsing, 26.2 ingredient syntax, shaped/shapeless recipe
structure, workbench recipe fields, and registered Simple Planes output IDs.
It is a static check, **not** an in-game or Java integration test.

## Build and install

Compile the source with **JDK 25**:

Windows:
```bat
gradlew.bat build
```

Linux/macOS:
```sh
./gradlew build
```

After a successful build, install the new mod JAR from `build/libs/` on the
Minecraft 26.2 NeoForge client and on the server, replacing the old Simple
Planes JAR (don't keep two versions). Restart your game/server to reload the
recipes. If on a hybrid Youer server, test recipes on a backup first; this
patch addresses recipe JSON formats, not all hybrid-platform compatibility.

## Survival mode sanity test

1. Craft `simpleplanes:propeller` using iron ingots.
2. Craft `simpleplanes:plane_workbench` using its recipe, then place it.
3. In that workbench, try one propeller plus five supported plank/log blocks
   to make `simpleplanes:plane`.
4. Try one upgrade recipe such as `simpleplanes:wrench` (iron ingots).

If recipes are still absent, check `latest.log` for errors parsing recipe
JSONs or for missing item tags: data packs and server plugins can override
recipes after the mod's files load.

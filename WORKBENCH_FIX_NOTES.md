# Simple Planes 26.2 — Plane Workbench crafting fix

## Changes

- **Recipe recognition:** `PlaneWorkbenchRecipe.matches` and `assemble` no longer unconditionally reject the recipe. Wood material validation includes the Simple Planes `plane_materials` block tag plus vanilla planks/logs as a fallback on hybrid servers.
- **Auto-select:** if the saved/selected plane recipe doesn't match the two input stacks, the menu finds a recipe that does. Players can still choose among valid outputs using the workbench arrows.
- **Empty recipe-map fallback:** if the server exposes no workbench recipes, the menu can use the four bundled default recipes (plane, large plane, cargo plane, helicopter). It logs a warning indicating that recipes weren't available from the server; custom datapack recipes are honored when the server provides them.
- **Result syncing:** uses normal menu `broadcastChanges()` synchronization instead of manually sending a container slot packet with state ID 0.
- **Taking the result:** the server validates the ingredients before allowing output. Crafting consumes the required ingredient and material stacks even after the output slot is cleared. Shift-click returns the moved item correctly and no longer drops a leftover output.
- **Inventory persistence:** updates to workbench input slots mark its block entity as changed, allowing the inputs to save.

## Default recipes — left slot / right slot

| Output | Left input | Right input |
| --- | --- | --- |
| Plane | 1 propeller | 5 planks or logs |
| Large plane | 1 propeller | 10 planks or logs |
| Cargo plane | 4 propellers | 64 planks or logs |
| Helicopter | 2 propellers | 7 planks or logs |

A single material stack must contain the required number of matching blocks. The two workbench slots are not a normal 3×3 crafting grid. Regular plane upgrades are made in the standard crafting table, then applied to planes separately.

## Build

Requires JDK 25 and Minecraft/NeoForge 26.2 build dependencies:

- Windows: `gradlew.bat build`
- Linux/macOS: `./gradlew build`

If building on GitHub, the included `.github/workflows` already has a JDK 25 build workflow. When successful, find the compiled mod at `build/libs/`. Replace the existing Simple Planes JAR on **both client and server**; do not install two versions at once. Back up the server world and test in a disposable Youer 26.2 instance first.

## Validation performed

- `python validate_recipes.py`: all 22 existing JSON recipes pass.
- `python validate_workbench_patch.py`: validates four recipe fallbacks against the JSON files and checks major source changes and matching scenarios.

**Limitations:** This environment does not have JDK 25 or access to Minecraft/NeoForge/Gradle downloads; the updated source has not been compiled or tested in Minecraft. If a workbench remains empty after building this patch, check the server's latest.log for `Simple Planes:` recipe-map warnings and recipe parse errors, and share that log with the build error or gameplay observation.

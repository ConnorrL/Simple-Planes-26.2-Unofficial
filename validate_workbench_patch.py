#!/usr/bin/env python3
"""Static validation of the Simple Planes plane workbench patch (not a Java/gameplay test)."""
import json
import pathlib
import re

root = pathlib.Path(__file__).resolve().parent
src = root / 'src/main/java/xyz/przemyk/simpleplanes'
menu = (src / 'container/PlaneWorkbenchContainer.java').read_text(encoding='utf-8')
recipe = (src / 'recipes/PlaneWorkbenchRecipe.java').read_text(encoding='utf-8')
result_slot = (src / 'container/PlaneCraftingResultSlot.java').read_text(encoding='utf-8')
tile = (src / 'blocks/PlaneWorkbenchBlockEntity.java').read_text(encoding='utf-8')

fallbacks = {
    name: (int(a), int(b))
    for name, a, b in re.findall(r'defaultWorkbenchRecipe\("([a-z_]+)",\s*(\d+),\s*(\d+),', menu)
}
expected = {'plane', 'large_plane', 'cargo_plane', 'helicopter'}
assert fallbacks.keys() == expected, (fallbacks, expected)
for name, counts in fallbacks.items():
    data = json.loads((root / f'src/main/resources/data/simpleplanes/recipe/{name}.json').read_text())
    assert data['type'] == 'simpleplanes:plane_workbench'
    assert data['ingredient'] == 'simpleplanes:propeller'
    assert (data['ingredient_amount'], data['material_amount']) == counts, (name, data, counts)
    assert data['result']['id'] == f'simpleplanes:{name}'

materials = json.loads((root / 'src/main/resources/data/simpleplanes/tags/block/plane_materials.json').read_text())
assert '#minecraft:planks' in materials['values']
assert '#minecraft:logs' in materials['values']
assert 'firstCraftable' in menu and 'selectedRecipe.set(firstCraftable)' in menu
assert 'broadcastChanges();' in menu and 'ClientboundContainerSetSlotPacket' not in menu
assert 'onQuickCraft(stack, original)' in menu
assert 'return original;' in menu and 'playerIn.drop(' not in menu
assert 'hasValidIngredients()' in menu and 'canTakeCraftingResult()' in result_slot
assert 'protected void onContentsChanged(int slot)' in tile
assert 'return false;' not in recipe[recipe.index('public boolean matches('):recipe.index('public ItemStack assemble(')]
assert 'PlaneWorkbenchRecipe.PLANE_MATERIALS_TAG' in menu
assert 'PlaneWorkbenchContainer.PLANE_MATERIALS_TAG' in (src / 'setup/SimplePlanesItems.java').read_text()

# Simulate the selection policy for the input quantities described in the JSON.
ordered = sorted(fallbacks)
def pick(previous, propellers, blocks):
    if previous in fallbacks and propellers >= fallbacks[previous][0] and blocks >= fallbacks[previous][1]:
        return previous
    return next((name for name in ordered if propellers >= fallbacks[name][0]
                 and blocks >= fallbacks[name][1]), None)
assert pick('cargo_plane', 1, 5) == 'plane'
assert pick('cargo_plane', 1, 10) == 'large_plane'
assert pick('cargo_plane', 2, 7) == 'helicopter'
assert pick('cargo_plane', 4, 64) == 'cargo_plane'
assert pick('cargo_plane', 1, 4) is None
print('PASS: four workbench recipe fallback definitions match bundled recipe JSONs.')
print('PASS: source checks for auto-selection, result sync, item consumption, shift-click and inventory save.')
print('PASS: input-quantity selection scenarios. Minecraft runtime/build not executed.')

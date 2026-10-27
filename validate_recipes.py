#!/usr/bin/env python3
"""Validate Simple Planes Minecraft 26.2 recipe JSON files without Gradle.

The 1.21.2+ Minecraft ingredient codec accepts strings like 'minecraft:iron_ingot'
for items or '#c:ingots/iron' for tags. Legacy {'item': ...} / {'tag': ...}
ingredient objects cause every affected recipe to fail parsing.
"""

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent
RECIPE_DIR = ROOT / 'src/main/resources/data/simpleplanes/recipe'
ITEMS_SOURCE = ROOT / 'src/main/java/xyz/przemyk/simpleplanes/setup/SimplePlanesItems.java'
ITEM_REGISTRATION = re.compile(r'ITEMS\.register\("([a-z0-9_]+)"')
IDENTIFIER = re.compile(r'^#?[a-z0-9_.-]+:[a-z0-9_./-]+$')


def check_ingredient(ingredient: object, context: str) -> None:
    if isinstance(ingredient, str):
        assert IDENTIFIER.fullmatch(ingredient), f'{context}: malformed item/tag identifier {ingredient!r}'
        return
    if isinstance(ingredient, list):
        assert ingredient, f'{context}: ingredient alternatives cannot be empty'
        for alt in ingredient:
            check_ingredient(alt, context)
        return
    raise AssertionError(f'{context}: legacy or invalid ingredient {ingredient!r}')


def validate() -> None:
    recipe_paths = sorted(RECIPE_DIR.glob('*.json'))
    assert len(recipe_paths) >= 22, f'Expected 22 original recipes; found {len(recipe_paths)}'
    item_ids = set(ITEM_REGISTRATION.findall(ITEMS_SOURCE.read_text(encoding='utf8')))
    assert item_ids, 'Unable to locate item registrations'

    vanilla_count = workbench_count = 0
    for path in recipe_paths:
        data = json.loads(path.read_text(encoding='utf8'))
        kind = data['type']
        result = data['result']
        assert result['id'].startswith('simpleplanes:'), f'{path}: unexpected output {result!r}'
        item_name = result['id'].split(':', 1)[1]
        assert item_name in item_ids, f'{path}: unregistered output item {item_name}'
        assert 1 <= result.get('count', 1) <= 64, f'{path}: invalid output count'

        if kind == 'minecraft:crafting_shaped':
            vanilla_count += 1
            pattern = data['pattern']
            key = data['key']
            assert 1 <= len(pattern) <= 3, f'{path}: incorrect pattern height'
            assert 1 <= len(pattern[0]) <= 3, f'{path}: incorrect pattern width'
            assert all(len(row) == len(pattern[0]) for row in pattern), f'{path}: uneven pattern rows'
            used = set(''.join(pattern)) - {' '}
            assert used == set(key), f'{path}: missing or unused key definitions {used ^ set(key)}'
            for symbol, ingredient in key.items():
                check_ingredient(ingredient, f'{path.name} key {symbol}')
        elif kind == 'minecraft:crafting_shapeless':
            vanilla_count += 1
            ingredients = data['ingredients']
            assert 1 <= len(ingredients) <= 9, f'{path}: invalid ingredient count'
            for i, ingredient in enumerate(ingredients):
                check_ingredient(ingredient, f'{path.name} ingredient {i}')
        elif kind == 'simpleplanes:plane_workbench':
            workbench_count += 1
            check_ingredient(data['ingredient'], f'{path.name} workbench input')
            assert data['ingredient_amount'] > 0 and data['material_amount'] > 0, f'{path}: invalid workbench amounts'
        else:
            raise AssertionError(f'{path}: unexpected recipe type {kind}')

    assert workbench_count == 4, f'Expected 4 plane workbench recipes, found {workbench_count}'
    print(f'PASS: {len(recipe_paths)} Minecraft 26.2 recipe JSON files ('
          f'{vanilla_count} vanilla crafting, {workbench_count} plane workbench).')
    print('Ingredient syntax, crafting patterns, output IDs, and workbench amounts all validated.')


if __name__ == '__main__':
    validate()

# Pass 9 → Pass 10 compiler triage

Source: uploaded GitHub Actions log `logs_105274094375.zip`.

The Pass 9 build **failed** during `compileJava` at the compiler's maximum error-display limit of 250. There may be additional unreported errors. The source log repeated the error output multiple times; counts below are de-duplicated.

## Fixed in Pass 10 (not yet compile-tested)

1. Data component registration explicitly specifies `Registries.DATA_COMPONENT_TYPE`.
2. Datapack listener registrations supply two stable `Identifier` names, as required by 26.2.
3. Custom recipe result uses `ItemStack.CODEC`, replacing removed `STRICT_CODEC`.
4. Block entity registrations use modern varargs `BlockEntityType(factory, validBlocks...)`.
5. Creative tab material variants now look up blocks through `parameters.holders()`.
6. Plane, cargo plane, helicopter and parachute registrations use NeoForge `DeferredRegister.Entities.registerEntityType(...)` instead of a removed raw `EntityType` constructor, preserving sizes and tracking range.

## Build limitations and remaining priorities

- All Pass 10 changes are *source patches only*, not a successful compilation or gameplay test.
- The render-state architecture, GUI submission, and renderer/upgrade models remain major blockers.
- Some old capability registration code and fuel/energy systems still require rework.
- Tests on standard NeoForge and a clean backed-up Youer world are necessary before live deployment.

## First compiler errors by file (Pass 9, deduplicated)

| File | Errors shown | First error |
|---|---:|---|
| `PlaneRenderer.java` | 23 | `8: cannot find symbol` |
| `FurnaceEngineUpgrade.java` | 13 | `49: cannot find symbol` |
| `Upgrade.java` | 12 | `6: cannot find symbol` |
| `ModifyUpgradesScreen.java` | 12 | `76: cannot find symbol` |
| `ParachuteRenderer.java` | 9 | `6: cannot find symbol` |
| `ModBusClientEventHandler.java` | 8 | `41: cannot find symbol` |
| `SimplePlanesMod.java` | 8 | `88: cannot find symbol` |
| `ClientEventHandler.java` | 7 | `41: wrong number of type arguments; required 3` |
| `PlaneEntity.java` | 7 | `197: method does not override or implement a method from a supertype` |
| `ClientUtil.java` | 6 | `17: cannot find symbol` |
| `PlanesModelLayers.java` | 5 | `188: incompatible types: PlaneMetalModel cannot be converted to EntityModel<PlaneEntity>` |
| `PlaneItemColors.java` | 4 | `5: cannot find symbol` |
| `SeatsUpgrade.java` | 4 | `5: cannot find symbol` |
| `ArmorUpgrade.java` | 4 | `5: cannot find symbol` |
| `SolarPanelUpgrade.java` | 4 | `5: cannot find symbol` |
| `UpgradesModels.java` | 4 | `28: type argument PlaneEntity is not within bounds of type-variable T` |
| `SeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `LargeSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `CargoSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `HeliSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `WoodenSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `WoodenHeliSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `WoodenCargoSeatsModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `WoodenCargoFloatingModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `ArmorWindowModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `CargoFurnaceEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `FurnaceEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `LargeFurnaceEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `HeliFurnaceEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `LiquidEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `HeliLiquidEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `LargeLiquidEngineModel.java` | 4 | `11: type argument PlaneEntity is not within bounds of type-variable T` |
| `LiquidEngineUpgrade.java` | 4 | `61: cannot find symbol` |
| `ChestUpgrade.java` | 3 | `8: cannot find symbol` |
| `JukeboxUpgrade.java` | 3 | `6: cannot find symbol` |
| `SupplyCrateUpgrade.java` | 3 | `8: cannot find symbol` |
| `PayloadUpgrade.java` | 3 | `6: cannot find symbol` |
| `FoldingUpgrade.java` | 3 | `6: cannot find symbol` |
| `BannerUpgrade.java` | 2 | `4: cannot find symbol` |
| `BannerModel.java` | 2 | `9: cannot find symbol` |
| `FloatingUpgrade.java` | 2 | `4: cannot find symbol` |
| `HealingUpgrade.java` | 2 | `4: cannot find symbol` |
| `PlaneWorkbenchBlock.java` | 2 | `49: method does not override or implement a method from a supertype` |
| `PlaneArmorItem.java` | 2 | `14: method does not override or implement a method from a supertype` |
| `MovingSound.java` | 2 | `41: method fromStack in record JukeboxSong cannot be applied to given types;` |
| `SimplePlanesBlocks.java` | 2 | `32: cannot infer type arguments for BlockEntityType<>` |
| `SimplePlanesDatapack.java` | 2 | `15: method addListener in class SortedReloadListenerEvent cannot be applied to given types;` |
| `ShooterUpgrade.java` | 1 | `3: cannot find symbol` |
| `PlaneWorkbenchContainer.java` | 1 | `54: cannot find symbol` |
| `ChargingStationBlockEntity.java` | 1 | `27: cannot find symbol` |
| `CameraMixin.java` | 1 | `34: cannot find symbol` |
| `PlaneWorkbenchRecipeSerializer.java` | 1 | `20: cannot find symbol` |
| `SimplePlanesEntities.java` | 1 | `34: no suitable constructor found for EntityType(EntityFactory<T>,MobCategory,boolean,boolean,boolean,boolean,ImmutableSet<Object>,EntityDimensions,float,int,int,FeatureFlagSet)` |
| `SimplePlanesComponents.java` | 1 | `14: method createDataComponents in class DeferredRegister<T> cannot be applied to given types;` |
| `SimplePlanesItems.java` | 1 | `93: cannot find symbol` |

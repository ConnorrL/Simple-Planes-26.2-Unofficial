package xyz.przemyk.simpleplanes.container;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.Identifier;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import xyz.przemyk.simpleplanes.network.CycleItemsPacket;
import xyz.przemyk.simpleplanes.recipes.PlaneWorkbenchRecipe;
import xyz.przemyk.simpleplanes.setup.SimplePlanesBlocks;
import xyz.przemyk.simpleplanes.setup.SimplePlanesComponents;
import xyz.przemyk.simpleplanes.setup.SimplePlanesItems;
import xyz.przemyk.simpleplanes.setup.SimplePlanesContainers;
import xyz.przemyk.simpleplanes.setup.SimplePlanesRecipes;

import java.util.Comparator;
import java.util.List;

public class PlaneWorkbenchContainer extends AbstractContainerMenu {

    private static final Logger LOGGER = LogManager.getLogger();

    // Kept as a public alias for creative-tab items and optional JEI integration.
    public static final Identifier PLANE_MATERIALS = Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "plane_materials");
    public static final TagKey<Block> PLANE_MATERIALS_TAG = PlaneWorkbenchRecipe.PLANE_MATERIALS_TAG;

    private final ItemStackHandler itemHandler;
    private final ItemStackHandler resultItemHandler = new ItemStackHandler();
    private final ContainerLevelAccess usabilityTest;
    private final Player player;
    private final DataSlot selectedRecipe;

    private final List<RecipeHolder<PlaneWorkbenchRecipe>> recipeList;

    public PlaneWorkbenchContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, BlockPos.ZERO, new ItemStackHandler(2), DataSlot.standalone());
    }

    public PlaneWorkbenchContainer(int id, Inventory playerInventory, BlockPos blockPos, ItemStackHandler itemHandler, DataSlot selectedRecipe) {
        super(SimplePlanesContainers.PLANE_WORKBENCH.get(), id);
        this.player = playerInventory.player;
        this.itemHandler = itemHandler;
        this.usabilityTest = ContainerLevelAccess.create(player.level(), blockPos);
        List<RecipeHolder<PlaneWorkbenchRecipe>> loadedRecipes = player.level() instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().recipeMap()
                    .byType(SimplePlanesRecipes.PLANE_WORKBENCH_RECIPE_TYPE.get()).stream()
                    .filter(holder -> holder.value() instanceof PlaneWorkbenchRecipe)
                    .map(holder -> new RecipeHolder<>(holder.id(), (PlaneWorkbenchRecipe) holder.value()))
                    .sorted(Comparator.comparing(holder -> holder.id().toString()))
                    .toList()
                : List.of();
        if (loadedRecipes.isEmpty() && player.level() instanceof ServerLevel) {
            // A few hybrid servers omit custom NeoForge recipe types from their
            // recipe map. Only use bundled defaults when all recipes are absent.
            LOGGER.warn("Simple Planes: no plane workbench recipes found in the server recipe map; "
                + "using the four built-in defaults. Check latest.log for recipe load errors.");
            loadedRecipes = defaultWorkbenchRecipes();
        }
        this.recipeList = loadedRecipes;
        // The server owns recipe definitions; AbstractContainerMenu broadcasts
        // output and recipe selection changes to the client.

        this.selectedRecipe = selectedRecipe;

        addSlot(new SlotItemHandler(itemHandler, 0, 28, 47));
        addSlot(new SlotItemHandler(itemHandler, 1, 75, 47));
        addSlot(new PlaneCraftingResultSlot(player, this, resultItemHandler, 0, 134, 47));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        addDataSlot(selectedRecipe);
        updateCraftingResult();
    }

    private static List<RecipeHolder<PlaneWorkbenchRecipe>> defaultWorkbenchRecipes() {
        return List.of(
            defaultWorkbenchRecipe("cargo_plane", 4, 64, SimplePlanesItems.CARGO_PLANE_ITEM.get()),
            defaultWorkbenchRecipe("helicopter", 2, 7, SimplePlanesItems.HELICOPTER_ITEM.get()),
            defaultWorkbenchRecipe("large_plane", 1, 10, SimplePlanesItems.LARGE_PLANE_ITEM.get()),
            defaultWorkbenchRecipe("plane", 1, 5, SimplePlanesItems.PLANE_ITEM.get())
        );
    }

    private static RecipeHolder<PlaneWorkbenchRecipe> defaultWorkbenchRecipe(String name, int propellers, int materials, Item outputItem) {
        return new RecipeHolder<>(
            ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, name)),
            new PlaneWorkbenchRecipe(Ingredient.of(SimplePlanesItems.PROPELLER.get()), propellers, materials, new ItemStack(outputItem))
        );
    }

    public void cycleItems(CycleItemsPacket.Direction direction) {
        if (recipeList.isEmpty()) return;
        if (selectedRecipe.get() < 0 || selectedRecipe.get() >= recipeList.size()) selectedRecipe.set(0);
        int prevSelectedRecipe = selectedRecipe.get();
        ItemStack ingredient = itemHandler.getStackInSlot(0);
        ItemStack material = itemHandler.getStackInSlot(1);

        switch (direction) {
            case CRAFTING_LEFT -> {
                do {
                    if (selectedRecipe.get() == 0) {
                        selectedRecipe.set(recipeList.size() - 1);
                    } else {
                        selectedRecipe.set(selectedRecipe.get() - 1);
                    }
                } while (selectedRecipe.get() != prevSelectedRecipe && !recipeList.get(selectedRecipe.get()).value().canCraft(ingredient, material));
            }
            case CRAFTING_RIGHT -> {
                do {
                    if (selectedRecipe.get() == recipeList.size() - 1) {
                        selectedRecipe.set(0);
                    } else {
                        selectedRecipe.set(selectedRecipe.get() + 1);
                    }
                } while (selectedRecipe.get() != prevSelectedRecipe && !recipeList.get(selectedRecipe.get()).value().canCraft(ingredient, material));
            }
        }

        updateCraftingResult();
    }

    private boolean hasValidIngredients() {
        int index = selectedRecipe.get();
        return !player.level().isClientSide()
            && index >= 0 && index < recipeList.size()
            && recipeList.get(index).value().canCraft(itemHandler.getStackInSlot(0), itemHandler.getStackInSlot(1));
    }

    public boolean canTakeCraftingResult() {
        return hasValidIngredients() && !resultItemHandler.getStackInSlot(0).isEmpty();
    }

    public void onCrafting() {
        // Vanilla removes the result stack before Slot#onTake is invoked,
        // so checking the output slot here would skip ingredient consumption.
        if (hasValidIngredients()) {
            PlaneWorkbenchRecipe recipe = recipeList.get(selectedRecipe.get()).value();
            itemHandler.extractItem(0, recipe.ingredientAmount(), false);
            itemHandler.extractItem(1, recipe.materialAmount(), false);
        }
        updateCraftingResult();
    }

    protected void updateCraftingResult() {
        if (player.level().isClientSide()) {
            return;
        }

        ItemStack ingredientStack = itemHandler.getStackInSlot(0);
        ItemStack materialStack = itemHandler.getStackInSlot(1);
        int index = selectedRecipe.get();

        // The saved choice can refer to a different recipe, or no longer match
        // after the input stacks change. Find a craftable recipe automatically.
        if (index < 0 || index >= recipeList.size()
                || !recipeList.get(index).value().canCraft(ingredientStack, materialStack)) {
            int firstCraftable = -1;
            for (int i = 0; i < recipeList.size(); ++i) {
                if (recipeList.get(i).value().canCraft(ingredientStack, materialStack)) {
                    firstCraftable = i;
                    break;
                }
            }
            if (firstCraftable >= 0) {
                selectedRecipe.set(firstCraftable);
                index = firstCraftable;
            } else if (index < 0 || index >= recipeList.size()) {
                selectedRecipe.set(0);
                index = 0;
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if (index >= 0 && index < recipeList.size()) {
            PlaneWorkbenchRecipe recipe = recipeList.get(index).value();
            if (recipe.canCraft(ingredientStack, materialStack)) {
                result = recipe.result().copy();
                BlockItem blockItem = (BlockItem) materialStack.getItem();
                CompoundTag materialTag = new CompoundTag();
                materialTag.putString("material", BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString());
                result.set(SimplePlanesComponents.ENTITY_TAG, materialTag);
            }
        }

        resultItemHandler.setStackInSlot(0, result);
        // Let the menu synchronize the correct slot and data-slot state IDs.
        // Sending an ad-hoc packet with state ID 0 can race normal inventory sync.
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(usabilityTest, playerIn, SimplePlanesBlocks.PLANE_WORKBENCH_BLOCK.get());
    }

    @Override
    public void clicked(int slotId, int dragType, ContainerInput containerInput, Player player) {
        super.clicked(slotId, dragType, containerInput, player);
        updateCraftingResult();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == 2) {
            // Do not dispense an output unless its ingredients are still valid.
            if (!canTakeCraftingResult() || !moveItemStackTo(stack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (index >= 3 && index < 39) {
            if (!moveItemStackTo(stack, 0, 2, false)) {
                if (index < 30) {
                    if (!moveItemStackTo(stack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(stack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!moveItemStackTo(stack, 3, 39, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(playerIn, stack);
        updateCraftingResult();
        return original;
    }
}

package xyz.przemyk.simpleplanes.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xyz.przemyk.simpleplanes.setup.SimplePlanesRecipes;

public record PlaneWorkbenchRecipe(Ingredient ingredient, int ingredientAmount,
                                   int materialAmount,
                                   ItemStack result) implements Recipe<RecipeInput> {

    public boolean canCraft(ItemStack ingredientStack, ItemStack materialStack) {
        return ingredientStack.getCount() >= ingredientAmount && materialStack.getCount() >= materialAmount && ingredient.test(ingredientStack);
    }

    @Override
    public RecipeSerializer<PlaneWorkbenchRecipe> getSerializer() {
        return SimplePlanesRecipes.PLANE_WORKBENCH_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<PlaneWorkbenchRecipe> getType() {
        return SimplePlanesRecipes.PLANE_WORKBENCH_RECIPE_TYPE.get();
    }

    @Override
    public boolean matches(RecipeInput p_77569_1_, Level p_77569_2_) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    // The plane workbench handles crafting using its own two-slot UI, not the vanilla recipe book.
    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return false;
    }
}

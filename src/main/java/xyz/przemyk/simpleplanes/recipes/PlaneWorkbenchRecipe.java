package xyz.przemyk.simpleplanes.recipes;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xyz.przemyk.simpleplanes.setup.SimplePlanesRecipes;

public record PlaneWorkbenchRecipe(Ingredient ingredient, int ingredientAmount,
                                   int materialAmount,
                                   ItemStack result) implements Recipe<RecipeInput> {

    public static final TagKey<Block> PLANE_MATERIALS_TAG = BlockTags.create(
        Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "plane_materials")
    );

    public boolean canCraft(ItemStack ingredientStack, ItemStack materialStack) {
        if (ingredientStack.isEmpty() || materialStack.isEmpty()
                || ingredientStack.getCount() < ingredientAmount
                || materialStack.getCount() < materialAmount
                || !ingredient.test(ingredientStack)) {
            return false;
        }
        if (!(materialStack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        // Respect the mod's material tag, but also support vanilla planks/logs
        // when a hybrid server does not correctly expose the custom tag.
        var materialBlock = blockItem.getBlock().builtInRegistryHolder();
        return materialBlock.is(PLANE_MATERIALS_TAG)
            || materialBlock.is(BlockTags.PLANKS)
            || materialBlock.is(BlockTags.LOGS);
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
    public boolean matches(RecipeInput input, Level level) {
        return input.size() >= 2 && canCraft(input.getItem(0), input.getItem(1));
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return input.size() >= 2 && canCraft(input.getItem(0), input.getItem(1))
            ? result.copy() : ItemStack.EMPTY;
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

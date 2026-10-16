package xyz.przemyk.simpleplanes.container.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FuelSlot extends SlotItemHandler {

    private final Level level;

    public FuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level level) {
        super(itemHandler, index, xPosition, yPosition);
        this.level = level;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
    }
}

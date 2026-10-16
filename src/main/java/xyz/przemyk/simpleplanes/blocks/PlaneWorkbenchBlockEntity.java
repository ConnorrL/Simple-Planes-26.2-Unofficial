package xyz.przemyk.simpleplanes.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import xyz.przemyk.simpleplanes.setup.SimplePlanesBlocks;

public class PlaneWorkbenchBlockEntity extends BlockEntity {

    public final ItemStackHandler itemStackHandler = new ItemStackHandler(2);
    public final DataSlot selectedRecipe = DataSlot.standalone();

    public PlaneWorkbenchBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SimplePlanesBlocks.PLANE_WORKBENCH_TILE.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("input", CompoundTag.CODEC, itemStackHandler.serializeNBT(level.registryAccess()));
        output.putInt("selected_recipe", selectedRecipe.get());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemStackHandler.deserializeNBT(level.registryAccess(),
            input.read("input", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        selectedRecipe.set(input.getIntOr("selected_recipe", 0));
    }
}

package xyz.przemyk.simpleplanes.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
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

    // On 26.2, inventory exports must happen before the block entity is removed.
    // The former BlockBehaviour#onRemove callback no longer provides that guarantee.
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverLevel) {
            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), itemStackHandler.getStackInSlot(0));
            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), itemStackHandler.getStackInSlot(1));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemStackHandler.serialize(output.child("input"));
        output.putInt("selected_recipe", selectedRecipe.get());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemStackHandler.deserialize(input.childOrEmpty("input"));
        selectedRecipe.set(input.getIntOr("selected_recipe", 0));
    }
}

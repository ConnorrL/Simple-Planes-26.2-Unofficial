package xyz.przemyk.simpleplanes.porting;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Compatibility bridge for upgrade classes that retain CompoundTag as their
 * serialized format. ItemStackHandler itself now speaks ValueInput/ValueOutput.
 *
 * TODO: migrate the upgrade interface to Value I/O directly later.
 */
public final class InventoryTagAdapter {
    private InventoryTagAdapter() {}

    public static CompoundTag save(ItemStackHandler handler, HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        handler.serialize(output);
        return output.buildResult();
    }

    public static void load(ItemStackHandler handler, HolderLookup.Provider registries, CompoundTag data) {
        handler.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, registries, data));
    }
}

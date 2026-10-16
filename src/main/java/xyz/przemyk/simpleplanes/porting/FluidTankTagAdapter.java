package xyz.przemyk.simpleplanes.porting;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * Value I/O bridge for the deprecated FluidTank compatibility type.
 * Keeps the surrounding CompoundTag format used by upgrade serialization.
 * Existing 1.21.1 fluid contents require migration testing in a copied world.
 */
public final class FluidTankTagAdapter {
    private FluidTankTagAdapter() {}

    public static CompoundTag save(FluidTank tank, HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        tank.serialize(output);
        return output.buildResult();
    }

    public static void load(FluidTank tank, HolderLookup.Provider registries, CompoundTag data) {
        tank.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, registries, data));
    }
}

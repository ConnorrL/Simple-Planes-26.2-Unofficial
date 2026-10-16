package xyz.przemyk.simpleplanes.porting;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

/**
 * A single ItemStack encoded through Minecraft 26.2 Value I/O, rather than
 * the removed ItemStack.save / ItemStack.parseOptional NBT methods.
 * The stack's components and count are preserved by ItemStack.CODEC.
 */
public final class ItemStackTagAdapter {
    private static final String KEY = "stack";
    private ItemStackTagAdapter() {}

    public static CompoundTag save(ItemStack stack, HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        if (!stack.isEmpty()) {
            output.store(KEY, ItemStack.CODEC, stack);
        }
        return output.buildResult();
    }

    public static ItemStack load(CompoundTag nbt, HolderLookup.Provider registries) {
        TagValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, registries, nbt);
        return input.read(KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}

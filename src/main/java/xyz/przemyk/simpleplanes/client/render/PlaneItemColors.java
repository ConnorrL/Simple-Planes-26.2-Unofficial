package xyz.przemyk.simpleplanes.client.render;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.przemyk.simpleplanes.setup.SimplePlanesComponents;

import java.util.HashMap;

/**
 * Minecraft 26.2 replaced the immediate item color handler with model tint
 * sources. Use the material block's map color as a deterministic approximation
 * until the legacy block-texture average can be restored using the new atlas API.
 * Unlike a constant tint, this preserves distinct colors for the wood materials.
 */
public final class PlaneItemColors {
    private PlaneItemColors() {}

    public static final HashMap<Block, Integer> cachedColors = new HashMap<>();
    public static final int DEFAULT_COLOR = 0xFFB28F55;

    public static void clearCache() {
        cachedColors.clear();
    }

    public static int getColor(ItemStack itemStack) {
        CompoundTag entityTag = itemStack.get(SimplePlanesComponents.ENTITY_TAG);
        if (entityTag == null || !entityTag.contains("material")) {
            return DEFAULT_COLOR;
        }

        Identifier identifier = Identifier.tryParse(entityTag.getStringOr("material", ""));
        if (identifier == null || !BuiltInRegistries.BLOCK.containsKey(identifier)) {
            return DEFAULT_COLOR;
        }
        Block block = BuiltInRegistries.BLOCK.getValue(identifier);
        if (block == null) {
            return DEFAULT_COLOR;
        }

        return cachedColors.computeIfAbsent(block, value -> {
            int rgb = value.defaultMapColor().col & 0xFFFFFF;
            return rgb == 0 ? DEFAULT_COLOR : 0xFF000000 | rgb;
        });
    }
}

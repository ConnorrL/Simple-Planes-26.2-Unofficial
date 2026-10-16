package xyz.przemyk.simpleplanes.compat.quark;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;

public class QuarkCompat {

    private static void registerChest(Item chestItem) {
        if (chestItem != Items.AIR) {
            SimplePlanesUpgrades.registerLargeUpgradeItem(chestItem, SimplePlanesUpgrades.CHEST.get());
        }
    }

    public static void registerUpgradeItems() {
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "azalea_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "oak_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "spruce_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "birch_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "jungle_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "acacia_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "dark_oak_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "crimson_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "warped_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "mangrove_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "nether_brick_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "purpur_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "prismarine_chest")));
        registerChest(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("quark", "blossom_chest")));
    }
}

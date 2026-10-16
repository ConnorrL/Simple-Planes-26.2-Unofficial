package xyz.przemyk.simpleplanes;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import xyz.przemyk.simpleplanes.compat.ironchest.IronChestsCompat;
import xyz.przemyk.simpleplanes.compat.quark.QuarkCompat;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.network.SimplePlanesNetworking;
import xyz.przemyk.simpleplanes.setup.*;

@Mod(SimplePlanesMod.MODID)
public class SimplePlanesMod {

    public static final String MODID = "simpleplanes";

    public SimplePlanesMod(IEventBus bus, ModContainer modContainer) {
        SimplePlanesConfig.init(modContainer);
        SimplePlanesEntities.init(bus);
        SimplePlanesBlocks.init(bus);
        SimplePlanesContainers.init(bus);
        SimplePlanesUpgrades.init(bus);
        SimplePlanesSounds.init(bus);
        SimplePlanesItems.init(bus);
        SimplePlanesRecipes.init(bus);
        SimplePlanesDatapack.init();
        SimplePlanesComponents.init(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::registerCapabilities);
        bus.addListener(SimplePlanesNetworking::register);
//        ModList.get().getModContainerById("cgm").ifPresent(cgm -> MinecraftForge.EVENT_BUS.register(MrCrayfishGunCompat.class));
    }

    public static Identifier texture(String filename) {
        return Identifier.fromNamespaceAndPath(MODID, "textures/plane_upgrades/" + filename);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.FLOATY_BEDDING.get(), SimplePlanesUpgrades.FLOATY_BEDDING.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.BOOSTER.get(), SimplePlanesUpgrades.BOOSTER.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.HEALING.get(), SimplePlanesUpgrades.HEALING.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.ARMOR.get(), SimplePlanesUpgrades.ARMOR.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.SOLAR_PANEL.get(), SimplePlanesUpgrades.SOLAR_PANEL.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.FOLDING.get(), SimplePlanesUpgrades.FOLDING.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.SEATS.get(), SimplePlanesUpgrades.SEATS.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.SHOOTER.get(), SimplePlanesUpgrades.SHOOTER.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.FURNACE_ENGINE.get(), SimplePlanesUpgrades.FURNACE_ENGINE.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.ELECTRIC_ENGINE.get(), SimplePlanesUpgrades.ELECTRIC_ENGINE.get());
            SimplePlanesUpgrades.registerUpgradeItem(SimplePlanesItems.LIQUID_ENGINE.get(), SimplePlanesUpgrades.LIQUID_ENGINE.get());

            SimplePlanesUpgrades.registerUpgradeItem(Blocks.WHITE_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.ORANGE_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.MAGENTA_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.LIGHT_BLUE_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.YELLOW_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.LIME_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.PINK_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.GRAY_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.LIGHT_GRAY_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.CYAN_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.PURPLE_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.BLUE_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.BROWN_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.GREEN_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.RED_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());
            SimplePlanesUpgrades.registerUpgradeItem(Blocks.BLACK_BANNER.asItem(), SimplePlanesUpgrades.BANNER.get());

            SimplePlanesUpgrades.registerLargeUpgradeItem(Items.CHEST, SimplePlanesUpgrades.CHEST.get());
            SimplePlanesUpgrades.registerLargeUpgradeItem(SimplePlanesItems.SUPPLY_CRATE.get(), SimplePlanesUpgrades.SUPPLY_CRATE.get());
            SimplePlanesUpgrades.registerLargeUpgradeItem(Items.JUKEBOX, SimplePlanesUpgrades.JUKEBOX.get());

            IronChestsCompat.registerUpgradeItems();
            QuarkCompat.registerUpgradeItems();
        });
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                SimplePlanesBlocks.CHARGING_STATION_TILE.get(),
                (blockEntity, side) -> blockEntity.energyStorage
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                SimplePlanesBlocks.PLANE_WORKBENCH_TILE.get(),
                (blockEntity, side) -> blockEntity.itemStackHandler
        );

        registerPlaneEntityCaps(event, SimplePlanesEntities.PLANE.get());
        registerPlaneEntityCaps(event, SimplePlanesEntities.LARGE_PLANE.get());
        registerPlaneEntityCaps(event, SimplePlanesEntities.CARGO_PLANE.get());
        registerPlaneEntityCaps(event, SimplePlanesEntities.HELICOPTER.get());
    }

    private static <T extends PlaneEntity> void registerPlaneEntityCaps(
            RegisterCapabilitiesEvent event,
            EntityType<T> type
    ) {
        event.registerEntity(Capabilities.ItemHandler.ENTITY, type,
                (entity, ctx) -> entity.getCap(Capabilities.ItemHandler.ENTITY)
        );
        event.registerEntity(Capabilities.FluidHandler.ENTITY, type,
                (entity, ctx) -> entity.getCap(Capabilities.FluidHandler.ENTITY)
        );
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, type,
                (entity, ctx) -> entity.getCap(Capabilities.EnergyStorage.ENTITY)
        );
    }
}

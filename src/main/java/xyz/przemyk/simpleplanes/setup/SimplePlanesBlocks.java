package xyz.przemyk.simpleplanes.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.blocks.ChargingStationBlock;
import xyz.przemyk.simpleplanes.blocks.ChargingStationBlockEntity;
import xyz.przemyk.simpleplanes.blocks.PlaneWorkbenchBlock;
import xyz.przemyk.simpleplanes.blocks.PlaneWorkbenchBlockEntity;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class SimplePlanesBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, SimplePlanesMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SimplePlanesMod.MODID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        TILES.register(bus);
    }

    // Minecraft 26.2 requires the registry key on the Block.Properties *before*
    // constructing the Block. The deferred registry name alone does not set it.
    private static BlockBehaviour.Properties blockProperties(String name, Block original) {
        return BlockBehaviour.Properties.ofFullCopy(original).setId(
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, name)));
    }

    public static final Supplier<PlaneWorkbenchBlock> PLANE_WORKBENCH_BLOCK = BLOCKS.register("plane_workbench", () -> new PlaneWorkbenchBlock(blockProperties("plane_workbench", Blocks.CRAFTING_TABLE)));
    public static final Supplier<ChargingStationBlock> CHARGING_STATION_BLOCK = BLOCKS.register("charging_station", () -> new ChargingStationBlock(blockProperties("charging_station", Blocks.COBBLESTONE)));

    public static final Supplier<BlockEntityType<PlaneWorkbenchBlockEntity>> PLANE_WORKBENCH_TILE = TILES.register("plane_workbench", () -> new BlockEntityType<>(PlaneWorkbenchBlockEntity::new, PLANE_WORKBENCH_BLOCK.get()));
    public static final Supplier<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_TILE = TILES.register("charging_station", () -> new BlockEntityType<>(ChargingStationBlockEntity::new, CHARGING_STATION_BLOCK.get()));
}

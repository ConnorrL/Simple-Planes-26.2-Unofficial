package xyz.przemyk.simpleplanes.setup;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.entities.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class SimplePlanesEntities {
    // EntityType's long direct constructor changed in Minecraft 26.2.
    // NeoForge's registerEntityType builder also creates the proper registry keys.
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(SimplePlanesMod.MODID);

    public static void init(IEventBus bus) {
        ENTITIES.register(bus);
    }

    public static final Supplier<EntityType<PlaneEntity>> PLANE = ENTITIES.registerEntityType(
        "plane", PlaneEntity::new, MobCategory.MISC,
        b -> b.sized(2.5F, 1.8F).clientTrackingRange(5).updateInterval(3).canSpawnFarFromPlayer());

    public static final Supplier<EntityType<LargePlaneEntity>> LARGE_PLANE = ENTITIES.registerEntityType(
        "large_plane", LargePlaneEntity::new, MobCategory.MISC,
        b -> b.sized(3.0F, 2.3F).clientTrackingRange(5).updateInterval(3).canSpawnFarFromPlayer());

    public static final Supplier<EntityType<CargoPlaneEntity>> CARGO_PLANE = ENTITIES.registerEntityType(
        "cargo_plane", CargoPlaneEntity::new, MobCategory.MISC,
        b -> b.sized(3.0F, 2.3F).clientTrackingRange(5).updateInterval(3).canSpawnFarFromPlayer());

    public static final Supplier<EntityType<HelicopterEntity>> HELICOPTER = ENTITIES.registerEntityType(
        "helicopter", HelicopterEntity::new, MobCategory.MISC,
        b -> b.sized(2.5F, 2.2F).clientTrackingRange(5).updateInterval(3).canSpawnFarFromPlayer());

    public static final Supplier<EntityType<ParachuteEntity>> PARACHUTE = ENTITIES.registerEntityType(
        "parachute", ParachuteEntity::new, MobCategory.MISC,
        b -> b.sized(1.0F, 1.0F).clientTrackingRange(5).updateInterval(3).canSpawnFarFromPlayer());
}

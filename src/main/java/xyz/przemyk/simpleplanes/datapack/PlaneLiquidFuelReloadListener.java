package xyz.przemyk.simpleplanes.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PlaneLiquidFuelReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {

    // Pass raw JSON into the existing per-entry parser. The 26.2 reload API
    // requires a Codec<T> and FileToIdConverter rather than Gson + folder string.
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Fluid, Integer> fuelMap = new HashMap<>();

    public PlaneLiquidFuelReloadListener() {
        super(JSON_ELEMENT_CODEC, FileToIdConverter.json("plane_liquid_fuels"));
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        fuelMap.clear();
        for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
            try {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                Fluid fluidType = BuiltInRegistries.FLUID.getValue(Identifier.parse(jsonObject.get("fluid").getAsString()));
                if (fluidType == null) {
                    continue;
                }
                int fuelPerMb = jsonObject.get("burn_time_per_mb").getAsInt();

                fuelMap.put(fluidType, fuelPerMb);
            } catch (Exception e) {
                LOGGER.error("Parsing error loading plane liquid fuel {}", entry.getKey(), e);
            }
        }
    }
}

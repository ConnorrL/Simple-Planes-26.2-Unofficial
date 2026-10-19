package xyz.przemyk.simpleplanes.client;

import com.google.common.reflect.TypeToken;
import com.mojang.math.Axis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.joml.Quaternionf;
import xyz.przemyk.simpleplanes.misc.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.lwjgl.glfw.GLFW;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.client.gui.ModifyUpgradesScreen;
import xyz.przemyk.simpleplanes.client.gui.PlaneInventoryScreen;
import xyz.przemyk.simpleplanes.client.gui.PlaneWorkbenchScreen;
import xyz.przemyk.simpleplanes.client.gui.StorageScreen;
import xyz.przemyk.simpleplanes.client.render.PlaneItemColors;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesContainers;
import xyz.przemyk.simpleplanes.upgrades.booster.BoosterUpgrade;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModBusClientEventHandler {

    private static final KeyMapping.Category PLANES_KEY_CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "planes"));

    public static final Identifier HUD_TEXTURE = Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "textures/gui/plane_hud.png");

    /** Capture the vehicle before rendering begins; RenderLivingEvent now exposes a state, not the entity. */
    @SubscribeEvent
    public static void registerPassengerPlaneRenderState(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (livingEntity, state) -> {
                    Entity vehicle = livingEntity.getRootVehicle();
                    if (!(vehicle instanceof PlaneEntity plane)) {
                        state.setRenderData(ClientEventHandler.PLANE_PASSENGER_TRANSFORM, null);
                        return;
                    }
                    float partialTicks = state.partialTick;
                    Quaternionf rotation = MathUtil.lerpQ(partialTicks, plane.getQ_Prev(), plane.getQ_Client());
                    rotation.set(rotation.x(), -rotation.y(), -rotation.z(), rotation.w());
                    float vehicleYaw = MathUtil.lerpAngle(partialTicks, vehicle.yRotO, vehicle.getYRot());
                    state.setRenderData(ClientEventHandler.PLANE_PASSENGER_TRANSFORM,
                            new ClientEventHandler.PassengerPlaneTransform(rotation, vehicleYaw));
                    // Mirror the head direction for an inverted plane, using render-state data only.
                    if (MathUtil.degreesDifferenceAbs(plane.rotationRoll, 0) > 90) {
                        state.yRot = plane.getYRot() * 2 - state.yRot;
                    }
                });
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SimplePlanesContainers.PLANE_WORKBENCH.get(), PlaneWorkbenchScreen::new);
        event.register(SimplePlanesContainers.UPGRADES_REMOVAL.get(), ModifyUpgradesScreen::new);
        event.register(SimplePlanesContainers.STORAGE.get(), StorageScreen::new);
        event.register(SimplePlanesContainers.PLANE_INVENTORY.get(), PlaneInventoryScreen::new);
    }

    @SubscribeEvent
    public static void registerPlaneItemTints(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "plane_material"),
                xyz.przemyk.simpleplanes.client.render.PlaneMaterialTint.MAP_CODEC);
    }

    @SubscribeEvent
    public static void reloadTextures(TextureAtlasStitchedEvent event) {
        PlaneItemColors.clearCache();
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(PLANES_KEY_CATEGORY);
        ClientEventHandler.moveHeliUpKey = new KeyMapping("key.move_heli_up.desc", GLFW.GLFW_KEY_SPACE, PLANES_KEY_CATEGORY);
        ClientEventHandler.openPlaneInventoryKey = new KeyMapping("key.plane_inventory_open.desc", GLFW.GLFW_KEY_X, PLANES_KEY_CATEGORY);
        ClientEventHandler.dropPayloadKey = new KeyMapping("key.plane_drop_payload.desc", GLFW.GLFW_KEY_C, PLANES_KEY_CATEGORY);
        ClientEventHandler.throttleUp = new KeyMapping("key.plane_throttle_up.desc", GLFW.GLFW_KEY_UP, PLANES_KEY_CATEGORY);
        ClientEventHandler.throttleDown = new KeyMapping("key.plane_throttle_down.desc", GLFW.GLFW_KEY_DOWN, PLANES_KEY_CATEGORY);
        ClientEventHandler.pitchUp = new KeyMapping("key.plane_pitch_up.desc", GLFW.GLFW_KEY_W, PLANES_KEY_CATEGORY);
        ClientEventHandler.pitchDown = new KeyMapping("key.plane_pitch_down.desc", GLFW.GLFW_KEY_S, PLANES_KEY_CATEGORY);
        ClientEventHandler.yawRight = new KeyMapping("key.plane_yaw_right.desc", GLFW.GLFW_KEY_RIGHT, PLANES_KEY_CATEGORY);
        ClientEventHandler.yawLeft = new KeyMapping("key.plane_yaw_left.desc", GLFW.GLFW_KEY_LEFT, PLANES_KEY_CATEGORY);
        event.register(ClientEventHandler.moveHeliUpKey);
        event.register(ClientEventHandler.openPlaneInventoryKey);
        event.register(ClientEventHandler.dropPayloadKey);
        event.register(ClientEventHandler.throttleUp);
        event.register(ClientEventHandler.throttleDown);
        event.register(ClientEventHandler.pitchUp);
        event.register(ClientEventHandler.pitchDown);
        event.register(ClientEventHandler.yawRight);
        event.register(ClientEventHandler.yawLeft);
    }

    @SubscribeEvent
    public static void registerHUDOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "plane_hud"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();

            int scaledWidth = mc.getWindow().getGuiScaledWidth();
            int scaledHeight = mc.getWindow().getGuiScaledHeight();

            if (mc.player.getVehicle() instanceof PlaneEntity planeEntity) {
                int left_align = scaledWidth / 2 + 91;

                int health = planeEntity.getHealth();
                float healthMax = planeEntity.getMaxHealth();
                int hearts = (int) (healthMax);

                if (hearts > 10) hearts = 10;

                final int FULL = 0;
                final int EMPTY = 16;
                final int GOLD = 32;
                int max_row_size = 5;

                for (int heart = 0; hearts > 0; heart += max_row_size) {
                    int top = scaledHeight - 49 - (heart / max_row_size) * 10;

                    int rowCount = Math.min(hearts, max_row_size);
                    hearts -= rowCount;

                    for (int i = 0; i < rowCount; ++i) {
                        int x = left_align - i * 16 - 16;
                        int vOffset = 35;
                        if (i + heart + 10 < health)
                            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE, x, top, GOLD, vOffset, 16, 9, 256, 256);
                        else if (i + heart < health)
                            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE, x, top, FULL, vOffset, 16, 9, 256, 256);
                        else
                            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE, x, top, EMPTY, vOffset, 16, 9, 256, 256);
                    }
                    // Layout locally: Gui.rightHeight was removed in 26.2.

                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE, scaledWidth - 24, scaledHeight - 42, 0, 84, 22, 40, 256, 256);
                    int throttle = planeEntity.getThrottle();
                    if (throttle > 0) {
                        int throttleScaled = throttle * 28 / BoosterUpgrade.MAX_THROTTLE;
                        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HUD_TEXTURE, scaledWidth - 24 + 10, scaledHeight - 42 + 6 + 28 - throttleScaled, 22, 90 + 28 - throttleScaled, 2, throttleScaled, 256, 256);
                    }

                    if (planeEntity.engineUpgrade != null) {
                        ItemStack offhandStack = mc.player.getOffhandItem();
                        HumanoidArm primaryHand = mc.player.getMainArm();
                        planeEntity.engineUpgrade.renderPowerHUD(guiGraphics, (primaryHand == HumanoidArm.LEFT || offhandStack.isEmpty()) ? HumanoidArm.LEFT : HumanoidArm.RIGHT, scaledWidth, scaledHeight, deltaTracker.getGameTimeDeltaPartialTick(false));
                    }
                }
            }
        });
    }
}

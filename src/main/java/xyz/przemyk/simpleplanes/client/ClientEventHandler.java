package xyz.przemyk.simpleplanes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.minecraft.util.context.ContextKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.joml.Quaternionf;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.misc.MathUtil;
import xyz.przemyk.simpleplanes.network.*;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    public static KeyMapping moveHeliUpKey;
    public static KeyMapping openPlaneInventoryKey;
    public static KeyMapping dropPayloadKey;
    public static KeyMapping throttleUp;
    public static KeyMapping throttleDown;
    public static KeyMapping pitchUp;
    public static KeyMapping pitchDown;
    public static KeyMapping yawRight;
    public static KeyMapping yawLeft;

    /** Snapshot written by the render-state modifier, avoiding mutable Entity access during submission. */
    public static final ContextKey<PassengerPlaneTransform> PLANE_PASSENGER_TRANSFORM =
            new ContextKey<>(Identifier.fromNamespaceAndPath("simpleplanes", "passenger_plane_transform"));

    public record PassengerPlaneTransform(Quaternionf rotation, float bodyYaw) {}

    /**
     * Ordinary living-entity render hooks. Player render hooks are separate event
     * classes in 26.2; handling RenderLivingEvent.Pre alone does NOT rotate avatars.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPre(RenderLivingEvent.Pre<?, ?, ?> event) {
        pushPassengerTransform(event.getPoseStack(),
                event.getRenderState().getRenderData(PLANE_PASSENGER_TRANSFORM));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPost(RenderLivingEvent.Post<?, ?, ?> event) {
        popPassengerTransform(event.getPoseStack(),
                event.getRenderState().getRenderData(PLANE_PASSENGER_TRANSFORM));
    }

    /** Player/avatar render events do not extend RenderLivingEvent.Pre/Post. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre<?> event) {
        pushPassengerTransform(event.getPoseStack(),
                event.getRenderState().getRenderData(PLANE_PASSENGER_TRANSFORM));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRenderPost(RenderPlayerEvent.Post<?> event) {
        popPassengerTransform(event.getPoseStack(),
                event.getRenderState().getRenderData(PLANE_PASSENGER_TRANSFORM));
    }

    private static void pushPassengerTransform(PoseStack matrixStack, PassengerPlaneTransform transform) {
        if (transform == null) return;
        matrixStack.pushPose();
        // The rider's render origin is the seat/feet. Rotating around the old
        // +0.375 pivot moves the whole avatar sideways while banking/pitching.
        // Use the same interpolated plane quaternion as PlaneRenderer; cancel
        // vanilla's body yaw with the yaw *it actually renders*, not a second
        // interpolated yaw from the aircraft.
        matrixStack.mulPose(new Quaternionf(transform.rotation()));
        matrixStack.mulPose(Axis.YP.rotationDegrees(transform.bodyYaw()));
    }

    private static void popPassengerTransform(PoseStack matrixStack, PassengerPlaneTransform transform) {
        if (transform != null) matrixStack.popPose();
    }

    private static boolean oldMoveHeliUpState = false;
    private static boolean oldPitchUpState = false;
    private static boolean oldPitchDownState = false;
    private static boolean oldYawRightState = false;
    private static boolean oldYawLeftState = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        final Player player = event.getEntity();
        if (player instanceof LocalPlayer) {
            if (player.getVehicle() instanceof PlaneEntity planeEntity) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
                    planeEntity.applyYawToEntity(player);
                }

                if (mc.gui.screen() == null && openPlaneInventoryKey.consumeClick()) {
                    ClientPacketDistributor.sendToServer(new OpenPlaneInventoryPacket());
                } else if (dropPayloadKey.consumeClick()) {
                    planeEntity.dropPayload();
                }

                if (throttleUp.consumeClick()) {
                    ClientPacketDistributor.sendToServer(new ChangeThrottlePacket(ChangeThrottlePacket.Direction.UP));
                } else if (throttleDown.consumeClick()) {
                    ClientPacketDistributor.sendToServer(new ChangeThrottlePacket(ChangeThrottlePacket.Direction.DOWN));
                }

                boolean isMoveHeliUp = moveHeliUpKey.isDown();
                boolean isPitchUp = pitchUp.isDown();
                boolean isPitchDown = pitchDown.isDown();
                boolean isYawRight = yawRight.isDown();
                boolean isYawLeft = yawLeft.isDown();

                if (isMoveHeliUp != oldMoveHeliUpState) {
                    ClientPacketDistributor.sendToServer(new MoveHeliUpPacket(isMoveHeliUp));
                }

                if (isPitchUp != oldPitchUpState || isPitchDown != oldPitchDownState) {
                    ClientPacketDistributor.sendToServer(new PitchPacket((byte) Boolean.compare(isPitchUp, isPitchDown)));
                }

                if (isYawRight != oldYawRightState || isYawLeft != oldYawLeftState) {
                    ClientPacketDistributor.sendToServer(new YawPacket((byte) Boolean.compare(isYawRight, isYawLeft)));
                }

                oldMoveHeliUpState = isMoveHeliUp;
                oldPitchUpState = isPitchUp;
                oldPitchDownState = isPitchDown;
                oldYawRightState = isYawRight;
                oldYawLeftState = isYawLeft;
            } else {
                oldMoveHeliUpState = false;
                oldPitchUpState = false;
                oldPitchDownState = false;
                oldYawRightState = false;
                oldYawLeftState = false;
            }
        }
    }

    //TODO: make it so player rotation variables correspond to what he is actually looking at, so that guns etc. shoot in the right direction
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Camera camera = event.getCamera();
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof PlaneEntity planeEntity) {
            if (!camera.isDetached()) {
                double partialTicks = event.getPartialTick();

                Quaternionf qPrev = planeEntity.getQ_Prev();
                Quaternionf qNow = planeEntity.getQ_Client();

                qPrev.mul(Axis.YP.rotationDegrees(player.yRotO));
                qPrev.mul(Axis.XP.rotationDegrees(event.getPitch()));
                MathUtil.EulerAngles eulerAnglesPrev = MathUtil.toEulerAngles(qPrev);

                qNow.mul(Axis.YP.rotationDegrees(player.getYRot()));
                qNow.mul(Axis.XP.rotationDegrees(event.getPitch()));
                MathUtil.EulerAngles eulerAnglesNow = MathUtil.toEulerAngles(qNow);

                event.setPitch(-(float) MathUtil.lerpAngle(partialTicks, eulerAnglesPrev.pitch, eulerAnglesNow.pitch));
                event.setYaw((float) MathUtil.lerpAngle(partialTicks, eulerAnglesPrev.yaw, eulerAnglesNow.yaw));
                event.setRoll(-(float) MathUtil.lerpAngle(partialTicks, eulerAnglesPrev.roll, eulerAnglesNow.roll));
            }
        }
    }

    @SubscribeEvent
    public static void onCalculateDetachedCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        if (Minecraft.getInstance().getCameraEntity() != null
                && Minecraft.getInstance().getCameraEntity().getVehicle() instanceof PlaneEntity planeEntity) {
            event.setDistance((float) (4.0 * planeEntity.getCameraDistanceMultiplayer()));
        }
    }
}

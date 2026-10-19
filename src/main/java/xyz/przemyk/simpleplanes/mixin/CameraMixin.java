package xyz.przemyk.simpleplanes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.misc.MathUtil;

/**
 * Camera Rotation Test 2: align the first-person view with the interpolated
 * cockpit seat anchor and aircraft attitude. Minecraft 26.2 calls setPosition
 * inside Camera.alignWithEntity rather than the former Camera.setup target.
 *
 * This is the editable source equivalent of the Test 2 compiled camera mixin.
 */
@Mixin(Camera.class)
public class CameraMixin {

    @WrapOperation(
        method = "alignWithEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
            ordinal = 0
        ),
        require = 0
    )
    private void wrapSetPos(Camera camera, double x, double y, double z, Operation<Void> original) {
        Minecraft mc = Minecraft.getInstance();
        Entity player = mc.getCameraEntity();

        if (player != null && player.getVehicle() instanceof PlaneEntity plane && !camera.isDetached()) {
            float partialTicks = camera.getCameraEntityPartialTicks(mc.getDeltaTracker());
            Vec3 seatPos = plane.getInterpolatedSeatPosition(player, partialTicks);
            if (seatPos != null) {
                MathUtil.EulerAngles angles = MathUtil.toEulerAngles(
                    MathUtil.lerpQ(partialTicks, plane.getQ_Prev(), plane.getQ_Client())
                );
                Quaternionf seatRotation = MathUtil.toQuaternionf(-angles.yaw, angles.pitch, -angles.roll);
                Vector3f rotatedEyeOffset = new Vector3f(
                    0.0f, player.getEyeHeight() - 0.3f, 0.0f
                ).rotate(seatRotation);
                original.call(camera,
                    seatPos.x + rotatedEyeOffset.x(),
                    seatPos.y + rotatedEyeOffset.y() + 0.375,
                    seatPos.z + rotatedEyeOffset.z());
                return;
            }
        }

        original.call(camera, x, y, z);
    }
}

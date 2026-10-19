package xyz.przemyk.simpleplanes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;

/**
 * Adjust the first-person camera for a plane's roll and pitch.
 *
 * The 1.21.1 mixin shadowed Camera.partialTickTime, Camera.eyeHeight and
 * Camera.eyeHeightOld. Those private implementation fields are not a stable
 * API on Minecraft 26.2, and the partialTickTime shadow caused a startup crash.
 * Instead, use the interpolated vanilla camera position from setPosition and
 * apply a relative offset based on the current aircraft rotation.
 */
@Mixin(Camera.class)
public class CameraMixin {

    @WrapOperation(
        method = "setup",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
            ordinal = 0
        ),
        // Do not abort client startup if the internal Camera.setup call layout
        // changes again. In that case the standard vanilla camera is retained.
        require = 0
    )
    private void wrapSetPos(Camera camera, double x, double y, double z, Operation<Void> original) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && player.getVehicle() instanceof PlaneEntity plane && !camera.isDetached()) {
            final float offsetBelowEyes = 0.3f;
            final float seatYOffset = 0.375f;
            final float eyeHeight = player.getEyeHeight();

            // setPosition's x/y/z already include normal vanilla interpolation.
            // Rotate the eye offset with the aircraft, keeping the original
            // camera height as the base for first-person seated flight.
            Vector3f rotatedEyeOffset = new Vector3f(0.0f, eyeHeight - offsetBelowEyes, 0.0f)
                .rotate(plane.getQ_Client());
            original.call(camera,
                x - rotatedEyeOffset.x(),
                y + rotatedEyeOffset.y() - eyeHeight + seatYOffset,
                z - rotatedEyeOffset.z());
        } else {
            original.call(camera, x, y, z);
        }
    }
}

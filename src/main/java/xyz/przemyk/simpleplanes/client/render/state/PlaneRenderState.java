package xyz.przemyk.simpleplanes.client.render.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;

/**
 * Per-frame snapshot. Entity access belongs in extractRenderState, never in submit.
 */
public class PlaneRenderState extends EntityRenderState {
    public float propellerRotation;
    public final Quaternionf planeRotation = new Quaternionf();
    public float hitShake;
    public float hitPhase;
    public int planeVariant;
    public Identifier materialTexture;
}

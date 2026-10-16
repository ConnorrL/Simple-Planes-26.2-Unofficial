package xyz.przemyk.simpleplanes.client.render.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Aircraft rendering snapshot. Populate from entity in PlaneRenderer.extractRenderState. */
public class PlaneRenderState extends EntityRenderState {
    public float propellerRotation;
}

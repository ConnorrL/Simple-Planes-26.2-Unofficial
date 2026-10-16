package xyz.przemyk.simpleplanes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import xyz.przemyk.simpleplanes.client.render.models.ParachuteModel;
import xyz.przemyk.simpleplanes.client.render.state.ParachuteRenderState;
import xyz.przemyk.simpleplanes.entities.ParachuteEntity;

/** 26.2 entity state renderer. Cargo barrel geometry still needs a collector submission. */
public class ParachuteRenderer extends EntityRenderer<ParachuteEntity, ParachuteRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/block/white_wool.png");
    private final ParachuteModel parachuteModel;

    public ParachuteRenderer(EntityRendererProvider.Context context, ParachuteModel parachuteModel) {
        super(context);
        this.parachuteModel = parachuteModel;
    }

    @Override
    public ParachuteRenderState createRenderState() { return new ParachuteRenderState(); }

    @Override
    public void extractRenderState(ParachuteEntity entity, ParachuteRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.hasStorageCrate = entity.hasStorageCrate();
    }

    @Override
    public void submit(ParachuteRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        poseStack.pushPose();
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0, state.hasStorageCrate ? -2.0 : -3.0, 0);
        collector.submitModel(parachuteModel, state, poseStack, RenderTypes.entityCutout(TEXTURE),
            state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0, null);
        poseStack.popPose();
    }
}

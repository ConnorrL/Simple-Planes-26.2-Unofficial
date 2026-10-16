package xyz.przemyk.simpleplanes.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import xyz.przemyk.simpleplanes.client.render.state.PlaneRenderState;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.misc.MathUtil;
import xyz.przemyk.simpleplanes.setup.SimplePlanesEntities;

import java.util.HashMap;
import java.util.Map;

/**
 * Work-in-progress 26.2 render-state renderer for the base aircraft meshes.
 * Upgrade overlays and crates MUST be migrated separately to collector submissions.
 */
public class PlaneRenderer<T extends PlaneEntity> extends EntityRenderer<T, PlaneRenderState> {
    protected final EntityModel<PlaneRenderState> propellerModel;
    protected final EntityModel<PlaneRenderState> planeEntityModel;
    protected final EntityModel<PlaneRenderState> planeMetalModel;
    protected final Identifier metalTexture;
    protected final Identifier propellerTexture;

    public PlaneRenderer(EntityRendererProvider.Context context,
                         EntityModel<PlaneRenderState> planeModel,
                         EntityModel<PlaneRenderState> planeMetalModel,
                         EntityModel<PlaneRenderState> propellerModel,
                         float shadowSize, Identifier metalTexture, Identifier propellerTexture) {
        super(context);
        this.planeEntityModel = planeModel;
        this.planeMetalModel = planeMetalModel;
        this.propellerModel = propellerModel;
        this.metalTexture = metalTexture;
        this.propellerTexture = propellerTexture;
        this.shadowRadius = shadowSize;
    }

    public static float getPropellerRotation(PlaneEntity entity, float partialTicks) {
        return Mth.lerp(partialTicks, entity.propellerRotationOld, entity.propellerRotationNew);
    }

    @Override
    public PlaneRenderState createRenderState() {
        return new PlaneRenderState();
    }

    @Override
    public void extractRenderState(T plane, PlaneRenderState state, float partialTick) {
        super.extractRenderState(plane, state, partialTick);
        state.propellerRotation = getPropellerRotation(plane, partialTick);
        state.planeRotation.set(MathUtil.lerpQ(partialTick, plane.getQ_Prev(), plane.getQ_Client()));
        state.materialTexture = getMaterialTexture(plane);
        EntityType<?> type = plane.getType();
        state.planeVariant = type == SimplePlanesEntities.PLANE.get() ? 0
            : type == SimplePlanesEntities.LARGE_PLANE.get() ? 1
            : type == SimplePlanesEntities.CARGO_PLANE.get() ? 2 : 3;
        float hitRemaining = plane.getTimeSinceHit() - partialTick;
        state.hitShake = hitRemaining > 0 ? Mth.clamp(hitRemaining / 10.0f, -30.0f, 30.0f) : 0.0f;
        state.hitPhase = plane.tickCount + partialTick;
    }

    @Override
    public void submit(PlaneRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        poseStack.pushPose();
        poseStack.translate(0.0, 0.375, 0.0);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.mulPose(state.planeRotation);
        switch (state.planeVariant) {
            case 0 -> poseStack.translate(0, -0.5, -0.5);
            case 1 -> poseStack.translate(0, -0.3, -1);
            case 2 -> poseStack.translate(0, -0.8, -1);
            default -> poseStack.translate(0, 0, 0.9);
        }
        if (state.hitShake > 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(state.hitPhase) * state.hitShake));
        }
        poseStack.translate(0, -1.1, 0);
        // Copy model transforms into the queued model submissions. The render graph draws later.
        collector.submitModel(planeEntityModel, state, poseStack, RenderTypes.entityCutout(state.materialTexture),
            state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0, null);
        collector.submitModel(propellerModel, state, poseStack, RenderTypes.entityCutout(propellerTexture),
            state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0, null);
        collector.submitModel(planeMetalModel, state, poseStack, RenderTypes.entityCutout(metalTexture),
            state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0, null);
        poseStack.popPose();
        // TODO: submit seats, equipment, large upgrades and banners from immutable state.
    }

    // Placeholder for 26.2's redesigned block-model sprite API; preserve the legacy
    // material reference and allow all named plank textures until atlas lookup is ported.
    public static Identifier getMaterialTexture(PlaneEntity plane) {
        return cachedTextures.computeIfAbsent(plane.getMaterial(), block -> {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (id != null && id.getPath().endsWith("_planks")) {
                return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/block/" + id.getPath() + ".png");
            }
            return FALLBACK_TEXTURE;
        });
    }

    public static final Map<Block, Identifier> cachedTextures = new HashMap<>();
    public static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/block/oak_planks.png");
}

package xyz.przemyk.simpleplanes.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.container.ModifyUpgradesContainer;
import xyz.przemyk.simpleplanes.entities.CargoPlaneEntity;

import javax.annotation.Nullable;

public class ModifyUpgradesScreen extends AbstractContainerScreen<ModifyUpgradesContainer> {

    public static final Identifier GUI = Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "textures/gui/modify_upgrades.png");
    public static final Component UPGRADES_TOOLTIP = Component.translatable(SimplePlanesMod.MODID + ".add_upgrades");
    public static final Component CARGO_TOOLTIP = Component.translatable(SimplePlanesMod.MODID + ".add_cargo");

    public ModifyUpgradesScreen(ModifyUpgradesContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 176, 184);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        if (hoveredSlot != null && hoveredSlot.getItem().isEmpty()) {
            if (hoveredSlot.index < 6) {
                guiGraphics.setTooltipForNextFrame(font, font.split(UPGRADES_TOOLTIP, 115), mouseX, mouseY);
            } else if (hoveredSlot.index < 14) {
                guiGraphics.setTooltipForNextFrame(font, font.split(CARGO_TOOLTIP, 115), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (menu.planeEntity != null) {
            guiGraphics.enableScissor(i + 62, j + 8, i + 168, j + 69);
            renderEntityInInventory(guiGraphics, leftPos + 115, topPos + 50, menu.planeEntity instanceof CargoPlaneEntity ? 6 : 11, new Quaternionf().rotateXYZ(0.3f, (minecraft.level.getGameTime() + partialTicks) / 20f, (float) Math.PI), null, menu.planeEntity);
            guiGraphics.disableScissor();
            if (menu.planeEntity instanceof CargoPlaneEntity) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 25, j + 74, 25, 101, 144, 18, 256, 256);
            }
        }

        if (menu.errorSlot != -1) {
            Slot slot = menu.slots.get(menu.errorSlot);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, i + slot.x - 1, j + slot.y - 1, 176, 76, 18, 18, 256, 256);
        }
    }

    /**
     * Capture the entity's current render state, then submit it to the modern GUI
     * render pipeline. The old immediate-mode entity renderer cannot render into
     * a GuiGraphicsExtractor. This preserves the rotating aircraft preview.
     */
    public static void renderEntityInInventory(GuiGraphicsExtractor guiGraphics, int x, int y, int scale,
                                                Quaternionf quaternionf, @Nullable Quaternionf quaternionf1,
                                                Entity entity) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState renderState = dispatcher.getRenderer(entity).createRenderState(entity, 1.0F);
        guiGraphics.entity(renderState, scale, new Vector3f(0.0F, 0.0F, 0.0F),
                quaternionf, quaternionf1, x - 53, y - 30, x + 53, y + 30);
    }
}

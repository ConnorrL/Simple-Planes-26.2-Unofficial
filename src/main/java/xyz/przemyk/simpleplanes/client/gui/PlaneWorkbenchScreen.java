package xyz.przemyk.simpleplanes.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.container.PlaneWorkbenchContainer;
import xyz.przemyk.simpleplanes.network.CycleItemsPacket;

public class PlaneWorkbenchScreen extends AbstractContainerScreen<PlaneWorkbenchContainer> {

    public static final Identifier GUI = Identifier.fromNamespaceAndPath(SimplePlanesMod.MODID, "textures/gui/plane_workbench.png");

    public PlaneWorkbenchScreen(PlaneWorkbenchContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ImageButton(leftPos + 122, topPos + 47, 10, 15, PlaneInventoryScreen.LEFT_BUTTON_SPRITES,
                button -> ClientPacketDistributor.sendToServer(new CycleItemsPacket(CycleItemsPacket.Direction.CRAFTING_LEFT))));

        addRenderableWidget(new ImageButton(leftPos + 152, topPos + 47, 10, 15, PlaneInventoryScreen.RIGHT_BUTTON_SPRITES,
                button -> ClientPacketDistributor.sendToServer(new CycleItemsPacket(CycleItemsPacket.Direction.CRAFTING_RIGHT))));
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphicsExtractor guiGraphics, float partialTicks, int x, int y) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}

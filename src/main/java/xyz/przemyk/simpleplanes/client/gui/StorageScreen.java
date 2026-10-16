package xyz.przemyk.simpleplanes.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import xyz.przemyk.simpleplanes.compat.ironchest.IronChestsCompat;
import xyz.przemyk.simpleplanes.container.StorageContainer;
import xyz.przemyk.simpleplanes.network.CyclePlaneInventoryPacket;

public class StorageScreen extends AbstractContainerScreen<StorageContainer> {

    public final Identifier texture;
    public final int textureYSize;

    public StorageScreen(StorageContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, IronChestsCompat.getXSize(screenContainer.chestType), IronChestsCompat.getYSize(screenContainer.chestType));
        inventoryLabelY = imageHeight - 94;
        texture = IronChestsCompat.getGuiTexture(screenContainer.chestType);
        textureYSize = IronChestsCompat.getTextureYSize(screenContainer.chestType);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ImageButton(leftPos + 3, topPos + 54, 10, 15, PlaneInventoryScreen.LEFT_BUTTON_SPRITES,
                button -> ClientPacketDistributor.sendToServer(new CyclePlaneInventoryPacket(CyclePlaneInventoryPacket.Direction.LEFT))));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 13, topPos + 54, 10, 15, PlaneInventoryScreen.RIGHT_BUTTON_SPRITES,
                button -> ClientPacketDistributor.sendToServer(new CyclePlaneInventoryPacket(CyclePlaneInventoryPacket.Direction.RIGHT))));

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, imageWidth, imageHeight, 256, textureYSize);
    }
}

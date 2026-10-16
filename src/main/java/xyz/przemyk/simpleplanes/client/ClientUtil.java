package xyz.przemyk.simpleplanes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import xyz.przemyk.simpleplanes.client.gui.PlaneInventoryScreen;

public class ClientUtil {

    public static void renderTiledTextureAtlas(GuiGraphicsExtractor guiGraphics, AbstractContainerScreen<?> screen,
                                              TextureAtlasSprite sprite, int x, int y, int width, int height, int depth) {
        // GUI extraction no longer supports an immediate BufferBuilder draw. Submit
        // atlas-sprite render commands instead, retaining the original tile size.
        final int left = screen.getGuiLeft() + x;
        final int top = screen.getGuiTop() + y;
        final int tileW = Math.max(1, sprite.contents().width());
        final int tileH = Math.max(1, sprite.contents().height());
        for (int dy = 0; dy < height; dy += tileH) {
            for (int dx = 0; dx < width; dx += tileW) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, left + dx, top + dy,
                        Math.min(tileW, width - dx), Math.min(tileH, height - dy));
            }
        }
    }

    public static int alpha(int c) {
        return (c >> 24) & 0xFF;
    }

    public static int red(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int green(int c) {
        return (c >> 8) & 0xFF;
    }

    public static int blue(int c) {
        return (c) & 0xFF;
    }

    // Immediate global shader tinting has been removed in Minecraft 26.2.
    // Callers should pass their color to a render-state submission instead.

    public static void renderLiquidEngineFluid(GuiGraphicsExtractor guiGraphics, PlaneInventoryScreen screen, FluidStack fluidStack, int height, int width, int fluidHeight) {
        // Minecraft 26.2 removed getStillTexture from the old fluid client extension.
        // This resolves conventional block/<fluid>_still sprites. Nonstandard
        // custom-fluid material locations still require a dedicated model lookup.
        Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
        String name = fluidId.getPath().replaceFirst("^flowing_", "");
        Identifier stillTexture = Identifier.fromNamespaceAndPath(fluidId.getNamespace(), "block/" + name + "_still");
        TextureAtlasSprite fluidSprite = guiGraphics.getSprite(new SpriteId(TextureAtlas.LOCATION_BLOCKS, stillTexture));
//        setColorRGBA(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack));
        renderTiledTextureAtlas(guiGraphics, screen, fluidSprite, 151 + 2, 7 + 18 + height - 2 - fluidHeight, width - 4, fluidHeight, 100);
//        RenderSystem.setShaderColor(1, 1, 1, 1);
//        RenderSystem.setShaderTexture(0, PlaneInventoryScreen.GUI);
    }
}

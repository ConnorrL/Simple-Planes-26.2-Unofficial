package xyz.przemyk.simpleplanes.upgrades;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xyz.przemyk.simpleplanes.client.gui.PlaneInventoryScreen;
import xyz.przemyk.simpleplanes.client.render.UpgradesModels;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesEntities;

import java.util.function.Function;

public abstract class Upgrade {

    private final UpgradeType type;
    protected final PlaneEntity planeEntity;
    public boolean updateClient = false;
    public boolean removed = false;

    public PlaneEntity getPlaneEntity() {
        return planeEntity;
    }

    public Upgrade(UpgradeType type, PlaneEntity planeEntity) {
        this.type = type;
        this.planeEntity = planeEntity;
    }

    /**
     * Call it when data is changed, and it needs to be synced to the client.
     * If called on a server, results in calling writePacket method on a server and readPacket on a client
     */
    protected void updateClient() {
        updateClient = true;
    }

    /**
     * Call it to remove this upgrade from the plane.
     */
    public void remove() {
        removed = true;
    }

    public final UpgradeType getType() {
        return type;
    }

    /**
     * Called when passenger right clicks with item.
     *
     * @param event The right click event
     */
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {}

    /**
     * Called every tick by plane entity.
     */
    public void tick() {}



    public Tag serializeNBT() {
        return new CompoundTag();
    }

    public void deserializeNBT(CompoundTag nbt) {}

    public void onApply(ItemStack itemStack) {}

    /**
     * Called on the server.
     */
    public abstract void writePacket(RegistryFriendlyByteBuf buffer);

    /**
     * Called on the client.
     */
    public abstract void readPacket(RegistryFriendlyByteBuf buffer);

    /**
     * Called when upgrade is removed using wrench.
     */
    public void onRemoved() {}

    public abstract ItemStack getItemStack();

    public boolean canBeDroppedAsPayload() {
        return false;
    }

    public void dropAsPayload() {}

    public void addContainerData(Function<Slot, Slot> addSlot, Function<DataSlot, DataSlot> addDataSlot) {}

    public void renderScreen(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, PlaneInventoryScreen planeInventoryScreen) {}

    public void renderScreenBg(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks, PlaneInventoryScreen screen) {}

    public <T> T getCap(BaseCapability<T, ?> cap) {
        return null;
    }
}

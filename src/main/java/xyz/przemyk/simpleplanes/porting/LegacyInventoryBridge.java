package xyz.przemyk.simpleplanes.porting;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

/**
 * Exposes the existing ItemStackHandler-backed storage to the NeoForge 26.2
 * transactional item capability without changing the saved items or menu slots.
 *
 * Temporary bridge: the legacy ItemStackHandler will eventually be replaced
 * by a native ItemStacksResourceHandler, after load/save and GUI testing.
 */
public final class LegacyInventoryBridge {
    private LegacyInventoryBridge() {}

    public static ResourceHandler<ItemResource> wrap(ItemStackHandler handler) {
        return VanillaContainerWrapper.of(new ContainerView(handler));
    }

    private static final class ContainerView implements Container {
        private final ItemStackHandler inventory;

        private ContainerView(ItemStackHandler inventory) {
            this.inventory = inventory;
        }

        @Override
        public int getContainerSize() {
            return inventory.getSlots();
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            if (slot < 0 || slot >= inventory.getSlots()) return ItemStack.EMPTY;
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            if (slot < 0 || slot >= inventory.getSlots() || count <= 0) return ItemStack.EMPTY;
            return inventory.extractItem(slot, count, false);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot < 0 || slot >= inventory.getSlots()) return ItemStack.EMPTY;
            ItemStack previous = inventory.getStackInSlot(slot);
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
            return previous;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot >= 0 && slot < inventory.getSlots()) inventory.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {
            // ItemStackHandler#setStackInSlot and #extractItem notify their owners.
        }

        @Override
        public boolean stillValid(Player player) {
            return true; // Access is gated by the owning capability, not by a menu.
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < inventory.getSlots(); i++) {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}

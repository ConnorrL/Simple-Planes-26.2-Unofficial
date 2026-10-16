package xyz.przemyk.simpleplanes.misc;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

/**
 * NeoForge 26.2 energy storage. The legacy-style methods are retained for
 * internal plane-engine logic; capability consumers use the EnergyHandler API.
 * A copy of the saved integer energy value remains compatible with older worlds.
 */
public class EnergyStorageWithSet extends SimpleEnergyHandler {
    private Runnable onChange;

    public EnergyStorageWithSet(int capacity) {
        super(capacity);
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        super.onEnergyChanged(previousAmount);
        if (onChange != null) {
            onChange.run();
        }
    }

    public int getEnergyStored() {
        return getAmountAsInt();
    }

    public int getMaxEnergyStored() {
        return getCapacityAsInt();
    }

    public void setEnergy(int amount) {
        set(Math.max(0, Math.min(amount, getCapacityAsInt())));
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.min(Math.max(0, maxReceive), getCapacityAsInt() - getAmountAsInt());
        if (!simulate && received > 0) {
            set(getAmountAsInt() + received);
        }
        return received;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.min(Math.max(0, maxExtract), getAmountAsInt());
        if (!simulate && extracted > 0) {
            set(getAmountAsInt() - extracted);
        }
        return extracted;
    }
}

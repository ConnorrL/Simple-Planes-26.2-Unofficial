package xyz.przemyk.simpleplanes.porting;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes the existing legacy FluidTank as a NeoForge 26.2 transactional
 * capability, without replacing its saved-data format or the HUD's tank.
 * One persistent wrapper must be kept for each tank to maintain snapshots.
 */
public final class LegacyFluidBridge extends SnapshotJournal<FluidStack>
        implements ResourceHandler<FluidResource> {

    private final FluidTank tank;

    public LegacyFluidBridge(FluidTank tank) {
        this.tank = tank;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        if (index != 0 || tank.isEmpty()) return FluidResource.EMPTY;
        return FluidResource.of(tank.getFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? tank.getFluidAmount() : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return index == 0 && (resource.isEmpty() || isValid(index, resource))
                ? tank.getCapacity() : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 0 && !resource.isEmpty()
                && tank.isFluidValid(0, resource.toStack(1));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || amount <= 0 || !isValid(index, resource)) return 0;
        FluidStack stack = resource.toStack(amount);
        int accepted = tank.fill(stack, IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) return 0;
        updateSnapshots(transaction);
        return tank.fill(resource.toStack(accepted), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || amount <= 0 || resource.isEmpty()
                || !resource.matches(tank.getFluid())) return 0;
        FluidStack available = tank.drain(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
        if (available.isEmpty()) return 0;
        updateSnapshots(transaction);
        return tank.drain(available, IFluidHandler.FluidAction.EXECUTE).getAmount();
    }

    @Override
    protected FluidStack createSnapshot() {
        return tank.getFluid().copy();
    }

    @Override
    protected void revertToSnapshot(FluidStack snapshot) {
        tank.setFluid(snapshot.copy());
    }
}

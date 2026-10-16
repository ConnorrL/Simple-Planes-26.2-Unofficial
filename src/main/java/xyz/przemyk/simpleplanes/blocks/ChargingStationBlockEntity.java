package xyz.przemyk.simpleplanes.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import xyz.przemyk.simpleplanes.misc.EnergyStorageWithSet;
import xyz.przemyk.simpleplanes.setup.SimplePlanesBlocks;

public class ChargingStationBlockEntity extends BlockEntity {

    public final EnergyStorageWithSet energyStorage = new EnergyStorageWithSet(1000);

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SimplePlanesBlocks.CHARGING_STATION_TILE.get(), blockPos, blockState);
    }

    public static void tick(ChargingStationBlockEntity blockEntity) {
        for (Entity entity : blockEntity.level.getEntities(null, new AABB(blockEntity.worldPosition.above()))) {
            var energyHandler = entity.getCapability(Capabilities.Energy.ENTITY, Direction.DOWN);
            if (energyHandler != null) {
                // Bridge older charging logic to the 26.2 transactional energy interface.
                IEnergyStorage entityEnergy = IEnergyStorage.of(energyHandler);
                int available = blockEntity.energyStorage.extractEnergy(1000, true);
                int accepted = entityEnergy.receiveEnergy(available, false);
                blockEntity.energyStorage.extractEnergy(accepted, false);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", energyStorage.getEnergyStored());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyStorage.setEnergy(input.getIntOr("energy", 0));
    }
}

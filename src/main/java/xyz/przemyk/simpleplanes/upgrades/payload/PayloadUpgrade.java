package xyz.przemyk.simpleplanes.upgrades.payload;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xyz.przemyk.simpleplanes.datapack.PayloadEntry;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesEntities;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;
import xyz.przemyk.simpleplanes.upgrades.LargeUpgrade;

public class PayloadUpgrade extends LargeUpgrade {

    private PayloadEntry payloadEntry;

    public PayloadUpgrade(PlaneEntity planeEntity, PayloadEntry payloadEntry) {
        super(SimplePlanesUpgrades.PAYLOAD.get(), planeEntity);
        this.payloadEntry = payloadEntry;
    }

    public PayloadUpgrade(PlaneEntity planeEntity) {
        super(SimplePlanesUpgrades.PAYLOAD.get(), planeEntity);
    }



    @Override
    public void writePacket(RegistryFriendlyByteBuf buffer) {
        buffer.writeIdentifier(BuiltInRegistries.ITEM.getKey(payloadEntry.item()));
        buffer.writeIdentifier(BuiltInRegistries.BLOCK.getKey(payloadEntry.renderBlock()));
        buffer.writeIdentifier(BuiltInRegistries.ENTITY_TYPE.getKey(payloadEntry.dropSpawnEntity()));
        buffer.writeNbt(payloadEntry.compoundTag());
    }

    @Override
    public void readPacket(RegistryFriendlyByteBuf buffer) {
        Item item = BuiltInRegistries.ITEM.getValue(buffer.readIdentifier());
        Block renderBlock = BuiltInRegistries.BLOCK.getValue(buffer.readIdentifier());
        EntityType<?> dropSpawnEntity = BuiltInRegistries.ENTITY_TYPE.getValue(buffer.readIdentifier());
        CompoundTag compoundTag = buffer.readNbt();
        payloadEntry = new PayloadEntry(item, renderBlock, dropSpawnEntity, compoundTag);
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("item", BuiltInRegistries.ITEM.getKey(payloadEntry.item()).toString());
        compoundTag.putString("block", BuiltInRegistries.BLOCK.getKey(payloadEntry.renderBlock()).toString());
        compoundTag.putString("entity", BuiltInRegistries.ENTITY_TYPE.getKey(payloadEntry.dropSpawnEntity()).toString());
        compoundTag.put("entityTag", payloadEntry.compoundTag());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(nbt.getStringOr("item", "")));
        Block renderBlock = BuiltInRegistries.BLOCK.getValue(Identifier.parse(nbt.getStringOr("block", "")));
        EntityType<?> dropSpawnEntity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(nbt.getStringOr("entity", "")));
        payloadEntry = new PayloadEntry(item, renderBlock, dropSpawnEntity, nbt.getCompoundOrEmpty("entityTag"));
    }

    @Override
    public ItemStack getItemStack() {
        return payloadEntry.item().getDefaultInstance();
    }

    @Override
    public boolean canBeDroppedAsPayload() {
        return true;
    }

    @Override
    public void dropAsPayload() {
        // Rehydrate the saved payload on the logical server only. In 26.2,
        // Entity#load(CompoundTag) has been replaced by typed ValueInput; the
        // EntityType loader accepts the stored CompoundTag and applies it.
        if (payloadEntry != null && planeEntity.level() instanceof ServerLevel serverLevel) {
            Entity entity = EntityType.loadEntityRecursive(
                    payloadEntry.dropSpawnEntity(), payloadEntry.compoundTag(),
                    serverLevel, EntitySpawnReason.COMMAND, loadedEntity -> loadedEntity);
            if (entity != null) {
                entity.setPos(planeEntity.position());
                entity.setDeltaMovement(planeEntity.getDeltaMovement());
                serverLevel.addFreshEntity(entity);
            }
        }
        remove();
    }
}

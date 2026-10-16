package xyz.przemyk.simpleplanes.upgrades.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import xyz.przemyk.simpleplanes.client.render.UpgradesModels;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesEntities;
import xyz.przemyk.simpleplanes.setup.SimplePlanesItems;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;
import xyz.przemyk.simpleplanes.upgrades.Upgrade;

public class ArmorUpgrade extends Upgrade {

    private int protectionLevel = 0;

    public ArmorUpgrade(PlaneEntity planeEntity) {
        super(SimplePlanesUpgrades.ARMOR.get(), planeEntity);
    }

    @Override
    public void onApply(ItemStack itemStack) {
        planeEntity.level().registryAccess().lookup(Registries.ENCHANTMENT).flatMap(registry -> registry.get(Enchantments.PROTECTION)).ifPresent(enchant -> {
            protectionLevel = itemStack.getEnchantmentLevel(enchant);
        });
    }



    @Override
    public void writePacket(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(protectionLevel);
    }

    @Override
    public void readPacket(RegistryFriendlyByteBuf buffer) {
        protectionLevel = buffer.readByte();
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = SimplePlanesItems.ARMOR.get().getDefaultInstance();
        if (protectionLevel > 0) {
            planeEntity.level().registryAccess().lookup(Registries.ENCHANTMENT).flatMap(registry -> registry.get(Enchantments.PROTECTION)).ifPresent(enchant -> {
                itemStack.enchant(enchant, protectionLevel);
            });
        }
        return itemStack;
    }

    public float getReducedDamage(float amount) {
        return amount * (1.0f - (0.04f * getArmorValue()));
    }

    public int getArmorValue() {
        return 15 + (protectionLevel * 2);
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByte("protection", (byte) protectionLevel);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        protectionLevel = nbt.getByte("protection").orElse((byte) 0);
    }
}

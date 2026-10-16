package xyz.przemyk.simpleplanes.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Client-side model tint, referenced in assets/simpleplanes/items/*.json. */
public record PlaneMaterialTint() implements ItemTintSource {
    public static final MapCodec<PlaneMaterialTint> MAP_CODEC = MapCodec.unit(new PlaneMaterialTint());

    @Override
    public int calculate(ItemStack itemStack, ClientLevel level, LivingEntity entity) {
        return PlaneItemColors.getColor(itemStack);
    }

    @Override
    public MapCodec<PlaneMaterialTint> type() {
        return MAP_CODEC;
    }
}

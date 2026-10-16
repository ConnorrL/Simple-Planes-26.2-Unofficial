package xyz.przemyk.simpleplanes.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;

public class DescriptionItem extends Item {

    private final Component component;

    public DescriptionItem(Properties properties, Component component) {
        super(properties);
        this.component = component;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag pTooltipFlag) {
        tooltip.accept(component);
    }
}

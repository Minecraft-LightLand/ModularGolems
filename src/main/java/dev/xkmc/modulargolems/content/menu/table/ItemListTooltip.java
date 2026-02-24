package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemListTooltip(List<ItemStack> inv) implements TooltipComponent {

}
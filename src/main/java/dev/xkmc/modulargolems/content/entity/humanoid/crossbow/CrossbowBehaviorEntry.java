package dev.xkmc.modulargolems.content.entity.humanoid.crossbow;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record CrossbowBehaviorEntry(Predicate<ItemStack> item, ICrossbowBehaviorFactory factory) {
}

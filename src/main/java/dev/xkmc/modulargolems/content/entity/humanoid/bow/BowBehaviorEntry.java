package dev.xkmc.modulargolems.content.entity.humanoid.bow;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record BowBehaviorEntry(Predicate<ItemStack> item, IBowBehaviorFactory factory) {
}

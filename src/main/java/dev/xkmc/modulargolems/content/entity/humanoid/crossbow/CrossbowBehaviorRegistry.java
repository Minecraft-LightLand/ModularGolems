package dev.xkmc.modulargolems.content.entity.humanoid.crossbow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class CrossbowBehaviorRegistry {

	private static final LinkedHashMap<ResourceLocation, CrossbowBehaviorEntry> MAP = new LinkedHashMap<>();

	public static void register(ResourceLocation id, Predicate<ItemStack> item, ICrossbowBehaviorFactory factory) {
		MAP.put(id, new CrossbowBehaviorEntry(item, factory));
	}

	public static boolean isValidCrossbowItem(ItemStack stack) {
		for (var e : MAP.values()) {
			if (e.item().test(stack))
				return true;
		}
		return stack.getItem() instanceof CrossbowItem;
	}

	public static Optional<CrossbowBehaviorData> get(HumanoidGolemEntity golem, ItemStack stack) {
		for (var e : MAP.values()) {
			if (e.item().test(stack))
				return Optional.of(e.factory().create(golem, stack));
		}
		if (stack.getItem() instanceof BowItem)
			return Optional.of(new CrossbowBehaviorData(20, new DefaultCrossbowBehavior()));
		return Optional.empty();
	}

	public static void init() {

	}

}

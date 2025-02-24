package dev.xkmc.modulargolems.content.entity.humanoid.bow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class BowBehaviorRegistry {

	private static final LinkedHashMap<ResourceLocation, BowBehaviorEntry> MAP = new LinkedHashMap<>();

	public static void register(ResourceLocation id, Predicate<ItemStack> item, IBowBehaviorFactory factory) {
		MAP.put(id, new BowBehaviorEntry(item, factory));
	}

	public static boolean isValidBowItem(ItemStack stack) {
		for (var e : MAP.values()) {
			if (e.item().test(stack))
				return true;
		}
		return stack.getItem() instanceof BowItem;
	}

	public static Optional<BowBehaviorData> get(HumanoidGolemEntity golem, ItemStack stack) {
		for (var e : MAP.values()) {
			if (e.item().test(stack))
				return Optional.of(e.factory().create(golem, stack));
		}
		if (stack.getItem() instanceof BowItem)
			return Optional.of(new BowBehaviorData(20, new DefaultBowBehavior()));
		return Optional.empty();
	}

	public static void init() {

	}

}

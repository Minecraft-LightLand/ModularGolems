package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Optional;

public class RangedBehaviorRegistry<T> {

	private final ResourceLocation id;
	private final LinkedHashMap<ResourceLocation, RangedBehaviorEntry<T>> MAP = new LinkedHashMap<>();
	@Nullable
	private final RangedBehaviorEntry<T> fallback;

	public RangedBehaviorRegistry(ResourceLocation id) {
		this.id = id;
		fallback = null;
	}

	public RangedBehaviorRegistry(ResourceLocation id, RangedStatusPredicate item, RangedBehaviorFactory<T> factory) {
		this.id = id;
		this.fallback = new RangedBehaviorEntry<>(item, factory);
	}

	public void register(ResourceLocation id, RangedStatusPredicate item, RangedBehaviorFactory<T> factory) {
		MAP.put(id, new RangedBehaviorEntry<>(item, factory));
	}

	public boolean isValidItem(ItemStack stack) {
		return getProperties(stack).map(WeaponStatus::isRanged).orElse(false);
	}

	public Optional<WeaponStatus> getProperties(ItemStack stack) {
		for (var e : MAP.values()) {
			var status = e.item().getProperties(stack);
			if (status.isPresent())
				return status;
		}
		return fallback == null ? Optional.empty() : fallback.item().getProperties(stack);
	}

	public Optional<T> get(HumanoidGolemEntity golem, ItemStack stack) {
		for (var e : MAP.values()) {
			var status = e.item().getProperties(stack);
			if (status.isPresent())
				return Optional.of(e.factory().create(golem, stack));
		}
		if (fallback != null && fallback.item().getProperties(stack).isPresent())
			return Optional.of(fallback.factory().create(golem, stack));
		return Optional.empty();
	}

	private record RangedBehaviorEntry<T>(
			RangedStatusPredicate item,
			RangedBehaviorFactory<T> factory
	) {

	}

}

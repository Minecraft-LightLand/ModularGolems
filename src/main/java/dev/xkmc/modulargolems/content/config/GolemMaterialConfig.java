package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.init.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	public static GolemMaterialConfig get() {
		return NetworkManager.MATERIAL.getMerged();
	}

	public static Map<GolemStatType, Double> collectAttributes(List<ResourceLocation> list) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (ResourceLocation stats : list) {
			get().stats.get(stats)
					.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static Map<GolemModifier, Integer> collectModifiers(List<ResourceLocation> list) {
		HashMap<GolemModifier, Integer> values = new HashMap<>();
		for (ResourceLocation stats : list) {
			get().modifiers.get(stats)
					.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		for (Map.Entry<String, Ingredient> ent : get().ingredients.entrySet()) {
			if (ent.getValue().test(stack)) {
				return Optional.of(new ResourceLocation(ent.getKey()));
			}
		}
		return Optional.empty();
	}

	public static void addAttributes(List<ResourceLocation> list, LivingEntity entity) {
		collectAttributes(list).forEach((k, v) -> k.applyToEntity(entity, v));
	}

	public static Component getDesc(ResourceLocation e) {
		return Component.translatable("golem_material." + e.getNamespace() + "." + e.getPath());
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<String, HashMap<GolemStatType, Double>> stats = new HashMap<>();

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<String, HashMap<GolemModifier, Integer>> modifiers = new HashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public HashMap<String, Ingredient> ingredients = new HashMap<>();

	@DataGenOnly
	public Builder addMaterial(ResourceLocation id, Ingredient ingredient) {
		return new Builder(this, id, ingredient);
	}

	@DataGenOnly
	public static class Builder {

		private final GolemMaterialConfig parent;
		private final ResourceLocation id;
		private final Ingredient ingredient;

		private final HashMap<GolemStatType, Double> stats = new HashMap<>();
		private final HashMap<GolemModifier, Integer> modifiers = new HashMap<>();

		private Builder(GolemMaterialConfig parent, ResourceLocation id, Ingredient ingredient) {
			this.parent = parent;
			this.id = id;
			this.ingredient = ingredient;
		}

		public Builder addStat(GolemStatType type, double val) {
			stats.put(type, val);
			return this;
		}

		public Builder addModifier(GolemModifier modifier, int lv) {
			modifiers.put(modifier, lv);
			return this;
		}

		public GolemMaterialConfig end() {
			parent.stats.put(id.toString(), stats);
			parent.modifiers.put(id.toString(), modifiers);
			parent.ingredients.put(id.toString(), ingredient);
			return parent;
		}

	}

}

package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.init.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	public static GolemMaterialConfig get() {
		return NetworkManager.MATERIALS.getMerged();
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, HashMap<GolemStatType, Double>> stats = new HashMap<>();

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, HashMap<GolemModifier, Integer>> modifiers = new HashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, Ingredient> ingredients = new HashMap<>();

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
			parent.stats.put(id, stats);
			parent.modifiers.put(id, modifiers);
			parent.ingredients.put(id, ingredient);
			return parent;
		}

	}

}

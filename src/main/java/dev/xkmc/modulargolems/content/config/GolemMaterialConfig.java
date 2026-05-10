package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2core.serial.config.BaseConfig;
import dev.xkmc.l2core.serial.config.CollectType;
import dev.xkmc.l2core.serial.config.ConfigCollect;
import dev.xkmc.l2core.util.DataGenOnly;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.http.util.Asserts;

import java.util.*;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	public static GolemMaterialConfig get() {
		return ModularGolems.MATERIALS.getMerged();
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialField
	public HashMap<Identifier, HashMap<GolemStatType, Double>> stats = new HashMap<>();

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialField
	public HashMap<Identifier, HashMap<GolemModifier, Integer>> modifiers = new HashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialField
	public HashMap<Identifier, Ingredient> ingredients = new HashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialField
	public HashMap<Identifier, Ingredient> repairIngredients = new HashMap<>();

	public List<Identifier> getAllMaterials() {
		TreeSet<Identifier> set = new TreeSet<>(stats.keySet());
		set.retainAll(modifiers.keySet());
		set.retainAll(ingredients.keySet());
		List<Identifier> ans = new ArrayList<>(set);
		ans.sort(Comparator.<Identifier, Integer>comparing(rl -> rl.getNamespace().equals(ModularGolems.MODID) ? 0 : 1)
				.thenComparing(Identifier::getNamespace)
				.thenComparing(Identifier::getPath));
		return ans;
	}

	public Ingredient getCraftIngredient(Identifier id) {
		var ans = ingredients.get(id);
		return ans == null ? Ingredient.EMPTY : ans;
	}

	public Ingredient getRepairIngredient(Identifier id) {
		var rep = repairIngredients.get(id);
		if (rep != null) return rep;
		var ans = ingredients.get(id);
		return ans == null ? Ingredient.EMPTY : ans;
	}

	@DataGenOnly
	public Builder addMaterial(Identifier id, Ingredient ingredient) {
		return new Builder(this, id, ingredient);
	}

	@DataGenOnly
	public Builder addMaterial(Identifier id, Ingredient ingredient, Ingredient repair) {
		return new Builder(this, id, ingredient, repair);
	}

	@DataGenOnly
	public static class Builder {

		private final GolemMaterialConfig parent;
		private final Identifier id;
		private final Ingredient ingredient;
		private final Ingredient repairIngredient;

		private final HashMap<GolemStatType, Double> stats = new HashMap<>();
		private final HashMap<GolemModifier, Integer> modifiers = new HashMap<>();

		private Builder(GolemMaterialConfig parent, Identifier id, Ingredient ingredient) {
			this.parent = parent;
			this.id = id;
			this.ingredient = ingredient;
			this.repairIngredient = ingredient;
		}

		private Builder(GolemMaterialConfig parent, Identifier id, Ingredient ingredient, Ingredient repair) {
			this.parent = parent;
			this.id = id;
			this.ingredient = ingredient;
			this.repairIngredient = repair;
		}

		public Builder addStat(GolemStatType type, double val) {
			stats.put(type, val);
			return this;
		}

		public Builder addModifier(GolemModifier modifier, int lv) {
			Asserts.check(!(modifier instanceof AttributeGolemModifier), "Material cannot use attribute modifier");
			modifiers.put(modifier, lv);
			return this;
		}

		public GolemMaterialConfig end() {
			parent.stats.put(id, stats);
			parent.modifiers.put(id, modifiers);
			parent.ingredients.put(id, ingredient);
			parent.repairIngredients.put(id, repairIngredient);
			return parent;
		}

	}

}

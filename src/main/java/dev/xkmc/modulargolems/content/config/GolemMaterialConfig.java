package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.http.util.Asserts;

import java.util.*;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	public static GolemMaterialConfig get() {
		return ModularGolems.MATERIALS.getMerged();
	}


	public static boolean mayApply(GolemHolder<?, ?> holder, ResourceLocation rl) {
		for (var part : holder.getEntityType().values()) {
			if (!mayApply(part.toItem(), rl))
				return false;
		}
		return true;
	}

	public static boolean mayApply(GolemPart<?, ?> part, ResourceLocation rl) {
		var config = get();
		var limit = config.partLimitation.get(rl);
		if (limit == null) return part.getDefaultInstance().is(MGTagGen.GENERIC_PARTS);
		return limit.contains(part);
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, HashMap<GolemStatType, Double>> stats = new LinkedHashMap<>();

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, HashMap<GolemModifier, Integer>> modifiers = new LinkedHashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, Ingredient> ingredients = new LinkedHashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, Ingredient> repairIngredients = new LinkedHashMap<>();

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, LinkedHashSet<Item>> partLimitation = new LinkedHashMap<>();

	public List<ResourceLocation> getAllMaterials() {
		TreeSet<ResourceLocation> set = new TreeSet<>(stats.keySet());
		set.retainAll(modifiers.keySet());
		set.retainAll(ingredients.keySet());
		if (FMLLoader.isProduction())
			set.removeIf(e -> isEmpty(ingredients.get(e)));
		List<ResourceLocation> ans = new ArrayList<>(set);
		ans.sort(Comparator.<ResourceLocation, Integer>comparing(rl -> rl.getNamespace().equals(ModularGolems.MODID) ? 0 : 1)
				.thenComparing(ResourceLocation::getNamespace)
				.thenComparing(ResourceLocation::getPath));
		return ans;
	}

	public Ingredient getCraftIngredient(ResourceLocation id) {
		var ans = ingredients.get(id);
		return ans == null ? Ingredient.EMPTY : ans;
	}

	public Ingredient getRepairIngredient(ResourceLocation id) {
		var rep = repairIngredients.get(id);
		if (rep != null) return rep;
		var ans = ingredients.get(id);
		return ans == null ? Ingredient.EMPTY : ans;
	}

	private static boolean isEmpty(Ingredient ing) {
		var items = ing.getItems();
		return items.length == 0 || items[0].is(Items.BARRIER);
	}

	@DataGenOnly
	public Builder addMaterial(ResourceLocation id, Ingredient ingredient) {
		return new Builder(this, id, ingredient);
	}

	@DataGenOnly
	public Builder addMaterial(ResourceLocation id, Ingredient ingredient, Ingredient repair) {
		return new Builder(this, id, ingredient, repair);
	}

	public GolemMaterialConfig supportsDefaultAnd(List<Item> part, ResourceLocation... ids) {
		List<Item> list = new ArrayList<>();
		list.addAll(List.of(GolemItems.GOLEM_BODY.get(), GolemItems.GOLEM_ARM.get(), GolemItems.GOLEM_LEGS.get(),
				GolemItems.HUMANOID_BODY.get(), GolemItems.HUMANOID_ARMS.get(), GolemItems.HUMANOID_LEGS.get(),
				GolemItems.DOG_BODY.get(), GolemItems.DOG_LEGS.get()));
		list.addAll(part);
		for (var id : ids)
			partLimitation.put(id, new LinkedHashSet<>(list));
		return this;
	}

	public GolemMaterialConfig supportsAlso(List<Item> part, ResourceLocation... ids) {
		for (var id : ids)
			partLimitation.put(id, new LinkedHashSet<>(part));
		return this;
	}

	@DataGenOnly
	public static class Builder {

		private final GolemMaterialConfig parent;
		private final ResourceLocation id;
		private final Ingredient ingredient;
		private final Ingredient repairIngredient;

		private final HashMap<GolemStatType, Double> stats = new HashMap<>();
		private final HashMap<GolemModifier, Integer> modifiers = new HashMap<>();

		private Builder(GolemMaterialConfig parent, ResourceLocation id, Ingredient ingredient) {
			this.parent = parent;
			this.id = id;
			this.ingredient = ingredient;
			this.repairIngredient = ingredient;
		}

		private Builder(GolemMaterialConfig parent, ResourceLocation id, Ingredient ingredient, Ingredient repair) {
			this.parent = parent;
			this.id = id;
			this.ingredient = ingredient;
			this.repairIngredient = repair;
		}

		public Builder onlyFor(Item... part) {
			parent.partLimitation.put(id, new LinkedHashSet<>(List.of(part)));
			return this;
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

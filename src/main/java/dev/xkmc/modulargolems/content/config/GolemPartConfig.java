package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.LinkedHashMap;

@SerialClass
public class GolemPartConfig extends BaseConfig {

	public static GolemPartConfig get() {
		return ModularGolems.PARTS.getMerged();
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<Item, HashMap<StatFilterType, Double>> filters = new HashMap<>();

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<ResourceLocation, HashMap<GolemStatType, Double>> magnifiers = new HashMap<>();

	/**
	 * Template ingredient used to craft each golem type. Datapack authors that add or change a
	 * golem type's parts and stonecutting recipes should also provide the matching template here,
	 * so the assembly table does not have to infer the template from recipes.
	 */
	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialClass.SerialField
	public LinkedHashMap<ResourceLocation, Ingredient> templates = new LinkedHashMap<>();

	public HashMap<StatFilterType, Double> getFilter(GolemPart<?, ?> part) {
		return filters.get(part);
	}

	public HashMap<GolemStatType, Double> getMagnifier(GolemType<?, ?> part) {
		return magnifiers.get(part.getRegistryName());
	}

	public Ingredient getTemplate(GolemType<?, ?> type) {
		var ans = templates.get(type.getRegistryName());
		return ans == null ? Ingredient.EMPTY : ans;
	}

	@DataGenOnly
	public GolemPartConfig addTemplate(GolemType<?, ?> type, Ingredient ingredient) {
		templates.put(type.getRegistryName(), ingredient);
		return this;
	}

	@DataGenOnly
	public PartBuilder addPart(GolemPart<?, ?> part) {
		return new PartBuilder(this, part);
	}

	@DataGenOnly
	public HolderBuilder addEntity(GolemType<?, ?> part) {
		return new HolderBuilder(this, part);
	}

	@DataGenOnly
	public static class PartBuilder {

		private final GolemPartConfig parent;
		private final GolemPart<?, ?> part;

		private final HashMap<StatFilterType, Double> filter = new HashMap<>();

		private PartBuilder(GolemPartConfig parent, GolemPart<?, ?> part) {
			this.parent = parent;
			this.part = part;
		}

		public PartBuilder addFilter(StatFilterType type, double val) {
			filter.put(type, val);
			return this;
		}

		public GolemPartConfig end() {
			parent.filters.put(part, filter);
			return parent;
		}

	}

	@DataGenOnly
	public static class HolderBuilder {

		private final GolemPartConfig parent;
		private final GolemType<?, ?> part;

		private final HashMap<GolemStatType, Double> filter = new HashMap<>();

		private HolderBuilder(GolemPartConfig parent, GolemType<?, ?> part) {
			this.parent = parent;
			this.part = part;
		}

		public HolderBuilder addFilter(GolemStatType type, double val) {
			filter.put(type, val);
			return this;
		}

		public GolemPartConfig end() {
			parent.magnifiers.put(part.getRegistryName(), filter);
			return parent;
		}

	}

}

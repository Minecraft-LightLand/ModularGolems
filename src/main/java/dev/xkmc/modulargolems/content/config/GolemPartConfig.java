package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.init.NetworkManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;

@SerialClass
public class GolemPartConfig extends BaseConfig {

	public static GolemPartConfig get() {
		return NetworkManager.PARTS.getMerged();
	}

	public static void addAttributes(List<GolemMaterial> list, LivingEntity entity) {
		GolemMaterial.collectAttributes(list).forEach((k, v) -> k.applyToEntity(entity, v));
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public HashMap<Item, HashMap<StatFilterType, Double>> filters = new HashMap<>();

	@DataGenOnly
	public Builder addMaterial(GolemPart<?, ?> part) {
		return new Builder(this, part);
	}

	public HashMap<StatFilterType, Double> getFilter(GolemPart<?, ?> part) {
		return filters.get(part);
	}

	@DataGenOnly
	public static class Builder {

		private final GolemPartConfig parent;
		private final GolemPart<?, ?> part;

		private final HashMap<StatFilterType, Double> filter = new HashMap<>();

		private Builder(GolemPartConfig parent, GolemPart<?, ?> part) {
			this.parent = parent;
			this.part = part;
		}

		public Builder addFilter(StatFilterType type, double val) {
			filter.put(type, val);
			return this;
		}

		public GolemPartConfig end() {
			parent.filters.put(part, filter);
			return parent;
		}

	}

}

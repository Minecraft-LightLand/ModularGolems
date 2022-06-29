package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.NetworkManager;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GolemMaterial extends NamedEntry<GolemMaterial> {
	
	public static Map<GolemStatType, Double> collectAttributes(List<GolemMaterial> list) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			GolemMaterialConfig.get().stats.get(stats.getID())
					.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static void addAttributes(List<GolemMaterial> list, LivingEntity entity) {
		collectAttributes(list).forEach((k, v) -> k.applyToEntity(entity, v));
	}

	public GolemMaterial() {
		super(GolemTypeRegistry.MATERIALS);
	}

}

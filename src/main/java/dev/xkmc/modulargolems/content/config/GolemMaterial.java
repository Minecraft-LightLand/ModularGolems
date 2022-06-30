package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class GolemMaterial {

	public static Map<GolemStatType, Double> collectAttributes(List<ResourceLocation> list) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (ResourceLocation stats : list) {
			GolemMaterialConfig.get().stats.get(stats.toString())
					.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static HashMap<GolemModifier, Integer> collectModifiers(Collection<ResourceLocation> list) {
		HashMap<GolemModifier, Integer> values = new HashMap<>();
		for (ResourceLocation stats : list) {
			GolemMaterialConfig.get().modifiers.get(stats.toString())
					.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		for (Map.Entry<String, Ingredient> ent : GolemMaterialConfig.get().ingredients.entrySet()) {
			if (ent.getValue().test(stack)) {
				return Optional.of(new ResourceLocation(ent.getKey()));
			}
		}
		return Optional.empty();
	}

	public static Component getDesc(ResourceLocation e) {
		return Component.translatable("golem_material." + e.getNamespace() + "." + e.getPath());
	}

}

package dev.xkmc.modulargolems.content.config;

import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.item.GolemPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class GolemMaterial {

	public static Map<GolemStatType, Double> collectAttributes(List<GolemMaterial> list) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.stats.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static HashMap<GolemModifier, Integer> collectModifiers(Collection<GolemMaterial> list) {
		HashMap<GolemModifier, Integer> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.modifiers.forEach((k, v) -> values.compute(k, (a, old) -> (old == null ? 0 : old) + v));
		}
		return values;
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		for (Map.Entry<ResourceLocation, Ingredient> ent : GolemMaterialConfig.get().ingredients.entrySet()) {
			if (ent.getValue().test(stack)) {
				return Optional.of(ent.getKey());
			}
		}
		return Optional.empty();
	}

	private final HashMap<GolemStatType, Double> stats;
	private final HashMap<GolemModifier, Integer> modifiers;
	private final ResourceLocation id;
	private final GolemPart part;

	public GolemMaterial(HashMap<GolemStatType, Double> stats, HashMap<GolemModifier, Integer> modifiers, ResourceLocation id, GolemPart part) {
		this.stats = stats;
		this.modifiers = modifiers;
		this.id = id;
		this.part = part;
	}

	public Component getDesc() {
		return Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath());
	}


}

package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public record GolemMaterial(HashMap<GolemStatType, Double> stats, HashMap<GolemModifier, Integer> modifiers,
							ResourceLocation id, Item part) {

	public static final ResourceLocation EMPTY = new ResourceLocation(ModularGolems.MODID, "empty");

	public static Map<GolemStatType, Double> collectAttributes(List<GolemMaterial> list, List<UpgradeItem> upgrades) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.stats.forEach((k, v) -> updateStat(values, k, v));
		}
		for (UpgradeItem item : upgrades) {
			if (item.get() instanceof AttributeGolemModifier attr) {
				for (var ent : attr.entries) {
					updateStat(values, ent.type().get(), ent.getValue(item.level));
				}
			}
		}
		return values;
	}

	private static void updateStat(HashMap<GolemStatType, Double> values, GolemStatType k, double v) {
		values.compute(k, (a, old) -> a.kind == GolemStatType.Kind.PERCENT ? (old == null ? 1 : old) * (1 + v) : (old == null ? 0 : old) + v);
	}

	public static HashMap<GolemModifier, Integer> collectModifiers(Collection<GolemMaterial> list, Collection<UpgradeItem> upgrades) {
		HashMap<GolemModifier, Integer> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.modifiers.forEach((k, v) -> values.compute(k, (a, old) -> Math.min(a.maxLevel, (old == null ? 0 : old) + v)));
		}
		upgrades.forEach(e -> values.compute(e.get(), (a, old) -> Math.min(a.maxLevel, (old == null ? 0 : old) + e.level)));
		return values;
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> void addAttributes(List<GolemMaterial> list, List<UpgradeItem> upgrades, T entity) {
		var map = DefaultAttributes.getSupplier(Wrappers.cast(entity.getType()));
		var attrs = collectAttributes(list, upgrades);
		attrs.keySet().stream().map(GolemStatType::getAttribute).distinct().forEach(e -> {
			var attr = entity.getAttribute(e);
			if (attr != null) {
				attr.setBaseValue(map.getBaseValue(e));
			}
		});
		attrs.forEach((k, v) -> k.applyToEntity(entity, v));
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		for (Map.Entry<ResourceLocation, Ingredient> ent : GolemMaterialConfig.get().ingredients.entrySet()) {
			if (ent.getValue().test(stack)) {
				return Optional.of(ent.getKey());
			}
		}
		return Optional.empty();
	}

	public MutableComponent getDesc() {
		return Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath()).withStyle(ChatFormatting.GOLD);
	}

	public GolemPart<?, ?> getPart() {
		return Wrappers.cast(part());
	}
}

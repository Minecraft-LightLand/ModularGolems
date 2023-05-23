package dev.xkmc.modulargolems.content.config;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.ModConfig;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public record GolemMaterial(HashMap<GolemStatType, Double> stats, HashMap<GolemModifier, Integer> modifiers,
							ResourceLocation id, Item part) {

	public static final ResourceLocation EMPTY = new ResourceLocation(ModularGolems.MODID, "empty");

	public static Map<Attribute, Pair<GolemStatType, Double>> collectAttributes(List<GolemMaterial> list, List<UpgradeItem> upgrades) {
		HashMap<Attribute, Pair<GolemStatType, Double>> values = new HashMap<>();
		for (GolemStatType type : GolemTypes.STAT_TYPES.get().getValues()) {
			updateStat(values, type, 0);
		}
		for (GolemMaterial stats : list) {
			stats.stats.forEach((k, v) -> updateStat(values, k, v));
		}
		for (var entry : collectModifiers(list, upgrades).entrySet()) {
			if (entry.getKey() instanceof AttributeGolemModifier attr) {
				for (var ent : attr.entries) {
					updateStat(values, ent.type().get(), ent.getValue(entry.getValue()));
				}
			}
		}
		return values;
	}

	private static void updateStat(Map<Attribute, Pair<GolemStatType, Double>> values, GolemStatType k, double v) {
		values.compute(k.getAttribute(), (a, old) -> Pair.of(k, ModConfig.COMMON.exponentialStat.get() && k.kind == GolemStatType.Kind.PERCENT ?
				(old == null ? 1 : old.getSecond()) * (1 + v) : (old == null ? 0 : old.getSecond()) + v));
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
		attrs.keySet().forEach(e -> {
			var attr = entity.getAttribute(e);
			if (attr != null) {
				attr.setBaseValue(map.getBaseValue(e));
			}
		});
		attrs.forEach((k, v) -> v.getFirst().applyToEntity(entity, v.getSecond()));
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

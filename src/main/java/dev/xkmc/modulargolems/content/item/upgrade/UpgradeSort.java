package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class UpgradeSort {

	private static final Map<Item, Integer> REGISTER_INDEX = new HashMap<>();

	private static int kind(ItemStack s) {
		if (s.getItem() instanceof AddSlotTemplate) return 0;
		if (s.is(MGTagGen.RED_UPGRADES)) return 1;
		if (s.is(MGTagGen.YELLOW_UPGRADES)) return 2;
		if (s.is(MGTagGen.BLUE_UPGRADES)) return 3;
		if (s.is(MGTagGen.GREEN_UPGRADES)) return 4;
		if (s.is(MGTagGen.POTION_UPGRADES)) return 5;
		return 6;
	}

	private static int registryIndex(Item item) {
		if (REGISTER_INDEX.isEmpty()) {
			int i = 0;
			for (var it : ForgeRegistries.ITEMS) {
				REGISTER_INDEX.put(it, i++);
			}
		}
		Integer idx = REGISTER_INDEX.get(item);
		return idx == null ? Integer.MAX_VALUE : idx;
	}

	public static Comparator<ItemStack> itemComparator() {
		return (a, b) -> {
			int c = Integer.compare(kind(a), kind(b));
			if (c != 0) return c;
			return Integer.compare(registryIndex(a.getItem()), registryIndex(b.getItem()));
		};
	}

	public static <T extends IUpgradeItem> Comparator<T> upgradeComparator() {
		return (a, b) -> itemComparator().compare(a.asItem().getDefaultInstance(), b.asItem().getDefaultInstance());
	}

	public static <K extends GolemModifier, V> Comparator<Map.Entry<K, V>> entryComparator(Collection<? extends IUpgradeItem> order) {
		Map<GolemModifier, Integer> index = new HashMap<>();
		int i = 0;
		for (var up : order) {
			int idx = i++;
			for (var e : up.get()) index.putIfAbsent(e.mod(), idx);
		}
		return (a, b) -> Integer.compare(index.getOrDefault(a.getKey(), Integer.MAX_VALUE),
				index.getOrDefault(b.getKey(), Integer.MAX_VALUE));
	}

}
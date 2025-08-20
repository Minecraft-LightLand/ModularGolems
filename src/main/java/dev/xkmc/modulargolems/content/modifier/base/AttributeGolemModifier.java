package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * should not be used in materials
 */
public class AttributeGolemModifier extends GolemModifier {

	public record AttrEntry(Supplier<GolemStatType> type, DoubleSupplier value) {

		public double getValue(int level) {
			return value.getAsDouble() * level;
		}
	}

	public final AttrEntry[] entries;

	public AttributeGolemModifier(int max, AttrEntry... entries) {
		super(StatFilterType.MASS, max);
		this.entries = entries;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		List<MutableComponent> ans = new ArrayList<>();
		for (AttrEntry ent : entries) {
			ans.add(ent.type.get().getAdderTooltip(ent.getValue(v)));
		}
		return ans;
	}

	@Nullable
	private Set<GolemStatType> checking = null;

	private Set<GolemStatType> checking() {
		if (checking == null) {
			checking = new LinkedHashSet<>();
			for (var e : entries) {
				checking.addAll(e.type.get().hasConflict());
			}
		}
		return checking;
	}

	@Override
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		if (checking().isEmpty()) return 0;
		for (var item : upgrades) {
			if (item instanceof UpgradeItem up) {
				for (var mod : up.get()) {
					if (mod.mod() instanceof AttributeGolemModifier attr) {
						for (var ent : attr.entries) {
							var stat = ent.type().get();
							if (checking().contains(stat)) return -1000000;
						}
					}
				}
			}
		}
		return 0;
	}

}

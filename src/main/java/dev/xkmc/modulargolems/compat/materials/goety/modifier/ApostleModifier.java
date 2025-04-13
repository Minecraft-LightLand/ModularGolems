package dev.xkmc.modulargolems.compat.materials.goety.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

import java.util.List;

public class ApostleModifier extends GolemModifier {

	public ApostleModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public int addSlot(List<UpgradeItem> upgrades, int lv) {
		int add = 0;
		for (var e : upgrades) {
			if (!e.get().isEmpty() && e.get().get(0).mod() instanceof IApostleModifier) {
				add++;
			}
		}
		return add;
	}
}

package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGTagGen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TalentMetaModifier extends GolemModifier {

	public TalentMetaModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public int addSlot(List<UpgradeItem> upgrades, int lv) {
		int ans = 0;
		Set<UpgradeItem> set = new HashSet<>(upgrades);
		for (UpgradeItem item : set) {
			if (item.getDefaultInstance().is(MGTagGen.BLUE_UPGRADES)) {
				ans++;
			}
		}
		return Math.min(4, ans);
	}
}

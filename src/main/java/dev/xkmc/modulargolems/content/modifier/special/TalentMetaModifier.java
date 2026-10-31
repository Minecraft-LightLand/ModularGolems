package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TalentMetaModifier extends GolemModifier {

	/**
	 * How many blue upgrades may be installed for free. Shared by {@link #addSlot} and the tooltip
	 * so the number a player reads cannot drift away from the number they get.
	 */
	public static final int MAX_FREE_UPGRADES = 4;

	public TalentMetaModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		int ans = 0;
		Set<IUpgradeItem> set = new HashSet<>(upgrades);
		for (IUpgradeItem item : set) {
			if (item.asItem().getDefaultInstance().is(MGTagGen.BLUE_UPGRADES)) {
				ans++;
			}
		}
		return Math.min(MAX_FREE_UPGRADES, ans);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var cap = Component.literal("" + MAX_FREE_UPGRADES).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", cap).withStyle(ChatFormatting.GREEN));
	}
}

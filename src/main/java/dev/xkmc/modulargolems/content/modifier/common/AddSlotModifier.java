package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class AddSlotModifier extends GolemModifier {

	public AddSlotModifier() {
		super(StatFilterType.MASS, 20);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", v).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public int addSlot(List<UpgradeItem> list, int lv) {
		return lv;
	}

}

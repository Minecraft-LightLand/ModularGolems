package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class DungeonHealModifier extends GolemModifier {

	private static boolean recursive = false;

	public DungeonHealModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		if (recursive) return;
		recursive = true;
		double factor = MGConfig.COMMON.dungeonLinkHealFactor.get();
		var val = heal * factor * value;//TODO config
		var player = golem.getOwner();
		if (player == null) return;
		player.heal((float) val);
		recursive = false;
	}

	public List<MutableComponent> getDetail(int v) {
		float factor = (float) (v * MGConfig.COMMON.dungeonLinkHealFactor.get());
		int perc = Math.round(100 * factor);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}
}

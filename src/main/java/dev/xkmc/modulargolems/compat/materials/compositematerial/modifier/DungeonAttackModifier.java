package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class DungeonAttackModifier extends GolemModifier {

	public DungeonAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		var event = cache.getLivingAttackEvent();
		if (event == null) return;
		var source = event.getSource();
		if (!source.is(L2DamageTypes.DIRECT)) return;
		double cost = MGConfig.COMMON.dungeonMeleeHealFactor.get();
		golem.heal((float) (cache.getDamageDealt() * cost * value));//TODO config
	}

	public List<MutableComponent> getDetail(int v) {
		float factor = (float) (v * MGConfig.COMMON.dungeonMeleeHealFactor.get());
		int perc = Math.round(100 * factor);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}
}

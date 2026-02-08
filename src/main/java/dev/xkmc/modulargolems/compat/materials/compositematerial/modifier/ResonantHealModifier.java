package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ResonantHealModifier extends GolemModifier {

	private static boolean recursive = false;

	public ResonantHealModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		if (recursive) return;
		recursive = true;
		double factor = MGConfig.COMMON.resonanceHealFactor.get();
		var val = heal * factor * value;
		var level = golem.level();
		var aabb = golem.getBoundingBox().inflate(MGConfig.COMMON.resonanceHealRange.get());
		var list = level.getEntitiesOfClass(AbstractGolemEntity.class, aabb);
		for (var e : list) {
			if (e == golem || !e.isAlliedTo(golem)) continue;
			if (!e.getModifiers().containsKey(this)) continue;
			int lvl = (Integer) e.getModifiers().get(this);
			e.heal((float) val * lvl);
		}
		recursive = false;
	}

	public List<MutableComponent> getDetail(int v) {
		float factor = (float)(MGConfig.COMMON.resonanceHealFactor.get() * v);
		int perc = Math.round(factor * 100);
		int range = MGConfig.COMMON.resonanceHealRange.get();
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc, range).withStyle(ChatFormatting.GREEN));
	}
}

package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2library.base.effects.EffectBuilder;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;

public class PrimitiveSustainModifier extends GolemModifier {

	public PrimitiveSustainModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.tickCount % 13 != 4) return;
		for (var e : golem.getActiveEffects()) {
			if (e.getDuration() > 400 && e.isCurativeItem(Items.MILK_BUCKET.getDefaultInstance())) {
				var ans = new EffectBuilder(new MobEffectInstance(e)).setDuration(Math.min(500, e.getDuration() + 20)).ins;
				golem.addEffect(ans);
			}
		}
	}

	@Override
	public float onHealPre(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		for (var e : golem.getActiveEffects()) {
			if (e.getEffect().isBeneficial()) heal = heal / 2;
		}
		return heal;
	}

}

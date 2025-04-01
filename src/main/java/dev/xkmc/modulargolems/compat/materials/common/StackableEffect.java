package dev.xkmc.modulargolems.compat.materials.common;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2core.base.effects.api.InherentEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface StackableEffect<T extends InherentEffect> {

	static void addTo(Holder<MobEffect> eff, LivingEntity target, int dur, int max, @Nullable Entity source) {
		var old = target.getEffect(eff);
		MobEffectInstance ins;
		if (old == null) {
			ins = new MobEffectInstance(eff, dur);
		} else {
			ins = new MobEffectInstance(eff, Math.max(dur, old.getDuration()), Math.min(max, old.getAmplifier() + 1));
		}
		EffectUtil.addEffect(target, ins, source);
	}

}

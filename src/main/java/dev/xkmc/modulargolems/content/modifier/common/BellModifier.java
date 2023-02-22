package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;

public class BellModifier extends GolemModifier {

	public BellModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onSetTarget(AbstractGolemEntity<?, ?> golem, Mob mob, int level) {
		golem.playSound(SoundEvents.BELL_BLOCK, 2, 1);
		var aabb = golem.getBoundingBox().inflate(48);
		var list = golem.level.getEntitiesOfClass(Mob.class, aabb, golem::canAttack);
		for (var e : list) {
			EffectUtil.addEffect(e, new MobEffectInstance(MobEffects.GLOWING, 200), EffectUtil.AddReason.NONE, golem);
			if (!(e.getTarget() instanceof AbstractGolemEntity<?, ?>) && e.canAttack(golem))
				e.setTarget(golem);
		}
	}
}

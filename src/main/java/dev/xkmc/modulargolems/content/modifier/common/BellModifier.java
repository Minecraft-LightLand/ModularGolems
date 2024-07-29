package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;

public class BellModifier extends GolemModifier {

	public BellModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onSetTarget(AbstractGolemEntity<?, ?> golem, Mob mob, int level) {
		var aabb = golem.getBoundingBox().inflate(48);
		var list = golem.level().getEntitiesOfClass(Mob.class, aabb, golem::canAttack);
		boolean sound = false;
		for (var e : list) {
			if (e instanceof Enemy && !(e instanceof Creeper) && e.canAttack(golem)) {
				sound |= !e.hasEffect(MobEffects.GLOWING);
				EffectUtil.addEffect(e, new MobEffectInstance(MobEffects.GLOWING, 200), golem);
				if (!(e.getTarget() instanceof AbstractGolemEntity<?, ?>))
					e.setTarget(golem);
			}
		}
		if (sound) {
			golem.playSound(SoundEvents.BELL_BLOCK, 1, 1);
		}
	}
}

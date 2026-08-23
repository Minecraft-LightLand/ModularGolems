package dev.xkmc.modulargolems.compat.materials.royalvariation;

import com.mongoose.royalvariations.common.effects.RVEffects;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MarkingModifier extends GolemModifier {

	public MarkingModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		cache.getAttackTarget().addEffect(new MobEffectInstance(RVEffects.MARKED.get(), 200, value - 1));
	}

}

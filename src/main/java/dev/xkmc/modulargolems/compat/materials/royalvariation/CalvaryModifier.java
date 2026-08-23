package dev.xkmc.modulargolems.compat.materials.royalvariation;

import com.mongoose.royalvariations.common.effects.RVEffects;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class CalvaryModifier extends GolemModifier {

	public CalvaryModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onDamageMax(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		var list = golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(24));
		for (var e : list) {
			if (golem.isAlliedTo(e)) {
				e.addEffect(new MobEffectInstance(RVEffects.ROYAL_BLESSING.get(), 200, value - 1));
			}
		}
	}

}

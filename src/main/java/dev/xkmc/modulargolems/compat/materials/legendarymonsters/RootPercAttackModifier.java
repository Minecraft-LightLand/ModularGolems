package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.util.GolemUtils;

public class RootPercAttackModifier extends GolemModifier {

	public RootPercAttackModifier() {
		super(StatFilterType.ATTACK, 2);
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		float max = cache.getAttackTarget().getMaxHealth();
		cache.addHurtModifier(DamageModifier.nonlinearPre(173, d -> calc(d, max, level)));
	}

	private float calc(float val, float max, int level) {
		return GolemUtils.adjustedDamage(val, 0.01f * level * max);
	}

}

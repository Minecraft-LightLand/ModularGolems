package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class PrimitiveBlastModifier extends GolemModifier {

	public PrimitiveBlastModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingAttackEvent();
		if (event == null) return;
		var source = event.getSource();
		if (!source.is(L2DamageTypes.DIRECT)) return;
		float val = entity.getMaxHealth() * level * 0.05f;//TODO config
		cache.addHurtModifier(DamageModifier.addExtra(val));
	}

}

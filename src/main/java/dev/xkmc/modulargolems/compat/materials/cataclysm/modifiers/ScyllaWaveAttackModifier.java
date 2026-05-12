package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;

public class ScyllaWaveAttackModifier extends GolemModifier {

	public ScyllaWaveAttackModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void postDamaged(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		var attacker = event.getSource().getEntity();
		if (!(attacker instanceof LivingEntity le)) return;
		long time = entity.level().getGameTime();
		var last = entity.getPersistentData().getLong("ScyllaWaveTime");
		if (last < time && last > time - 40) return;
		entity.getPersistentData().putLong("ScyllaWaveTime", time);
		MWCataProxy.ceraunus(entity.level(), entity, le);
	}

}

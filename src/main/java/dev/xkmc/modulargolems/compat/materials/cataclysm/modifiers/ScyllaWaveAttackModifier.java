package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class ScyllaWaveAttackModifier extends GolemModifier implements ICooldownSkill {
	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		long last = golem.getPersistentData().getLong("ScyllaWaveTime");
		return (int) Math.max(0, last + 40 - time);
	}


	public ScyllaWaveAttackModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		var attacker = event.getSource().getEntity();
		if (!(attacker instanceof LivingEntity le)) return;
		long time = entity.level().getGameTime();
		var last = entity.getPersistentData().getLong("ScyllaWaveTime");
		if (last < time && last > time - 40) return;
		entity.getPersistentData().putLong("ScyllaWaveTime", time);
		entity.getPersistentData().putLong("ScyllaWave2Time", time + 6);
		GolemCataProxy.scyllaSummonWave(entity, le);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		long time = golem.level().getGameTime();
		long pending = golem.getPersistentData().getLong("ScyllaWave2Time");
		if (pending > 0 && time >= pending) {
			golem.getPersistentData().putLong("ScyllaWave2Time", 0);
			GolemCataProxy.scyllaSummonWave2(golem, golem.getTarget());
		}
	}

}

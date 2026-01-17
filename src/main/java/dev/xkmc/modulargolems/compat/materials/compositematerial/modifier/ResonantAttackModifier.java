package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2library.init.events.GeneralEventHandler;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;

public class ResonantAttackModifier extends GolemModifier {

	private static final String KEY = "ResonantAttackTimeStamp";

	public ResonantAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		var level = golem.level();
		long last = golem.getPersistentData().getLong(KEY);
		long time = level.getGameTime();
		if (last <= time && last > time - 20) return;//TODO config
		golem.getPersistentData().putLong(KEY, time);
		var target = cache.getAttackTarget();
		var damage = cache.getDamageDealt() * 0.1f * value;// TODO config
		double x = target.getX();
		double y = target.getY() + target.getBbHeight() / 2;
		double z = target.getZ();
		// TODO add particle
		GeneralEventHandler.schedulePersistent(() -> {
			if (level.getGameTime() > time + 10) {//TODO delay should be adjusted
				var source = golem.damageSources().magic();
				var aabb = target.getBoundingBox().inflate(8);//TODO config
				var list = level.getEntitiesOfClass(LivingEntity.class, aabb);
				for (var e : list) {
					if (e.getType() != target.getType()) continue;
					if (TargetManager.predicateTarget(golem, e) != null) {
						e.hurt(source, damage);
					}
				}
				return true;
			}
			return false;
		});
	}

}

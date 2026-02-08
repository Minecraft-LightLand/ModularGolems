package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2library.init.events.GeneralEventHandler;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

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
		if (last <= time && last > time - MGConfig.COMMON.resonanceAttackCooldown.get()) return;
		golem.getPersistentData().putLong(KEY, time);
		var target = cache.getAttackTarget();
		double factor = MGConfig.COMMON.resonanceAttackDamageFactor.get();
		var damage = cache.getDamageDealt() * (float)factor * value;
		double x = target.getX();
		double y = target.getY() + target.getBbHeight() / 2;
		double z = target.getZ();
		// TODO add particle
		GeneralEventHandler.schedulePersistent(() -> {
			if (level.getGameTime() > time + 10) {//TODO delay should be adjusted
				var source = golem.damageSources().magic();
				var aabb = target.getBoundingBox().inflate(MGConfig.COMMON.resonanceAttackRange.get());
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

	public List<MutableComponent> getDetail(int v) {
		float factor = (float)(MGConfig.COMMON.resonanceAttackDamageFactor.get() * v);
		int perc = Math.round(100 * factor);
		int range = MGConfig.COMMON.resonanceAttackRange.get();
		return List.of(Component.translatable(getDescriptionId() + ".desc", range, perc).withStyle(ChatFormatting.GREEN));
	}
}

package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GolemTargetUtil {

	/**
	 * 技能是否可以打这个实体。近战只取 {@code golem.getTarget()}，{@code setTarget} 内部已过 canAttack；
	 * 自行扫实体的技能必须两道闸门都查：canAttack 管敌我，predicateTarget 管被动傀儡、黑名单等
	 * 自身过滤规则。少查一道就会命中任何碰巧在附近的实体。
	 */
	public static boolean canTarget(AbstractGolemEntity<?, ?> golem, LivingEntity e) {
		return e != null && e.isAlive() && !e.isRemoved() && golem.canAttack(e) && golem.predicateTarget(e);
	}

	public static void collect(AbstractGolemEntity<?, ?> golem, LivingEntity main, double range, int maxCount, List<LivingEntity> out) {
		out.clear();
		if (main == null || !main.isAlive()) return;
		out.add(main);
		List<LivingEntity> list = new ArrayList<>();
		AABB aabb = golem.getBoundingBox().inflate(range);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (e == main || !canTarget(golem, e)) continue;
			if (golem.distanceToSqr(e) > range * range) continue;
			list.add(e);
		}
		list.sort(Comparator.comparingDouble(golem::distanceToSqr));
		for (int i = 0; i < Math.min(maxCount - 1, list.size()); i++) {
			out.add(list.get(i));
		}
	}

	public static LivingEntity resolve(AbstractGolemEntity<?, ?> golem, LivingEntity locked) {
		if (canTarget(golem, locked)) return locked;
		LivingEntity cur = golem.getTarget();
		return cur != null && cur.isAlive() && !cur.isRemoved() ? cur : locked;
	}

}

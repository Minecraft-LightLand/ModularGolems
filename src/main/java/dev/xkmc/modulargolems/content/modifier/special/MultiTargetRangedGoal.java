package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;

public abstract class MultiTargetRangedGoal extends BaseRangedAttackGoal {

	protected final int lv;
	@Nullable
	private LinkedList<LivingEntity> targets = null;
	private int cd = 0;

	public MultiTargetRangedGoal(int waitTime, int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		super(waitTime, near, far, golem, lv);
		this.lv = lv;
	}

	protected void makeTarget() {
		var list = golem.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(searchRange()),
				e -> golem.canAttack(e) && golem.hasLineOfSight(e) && golem.predicateTarget(e));
		list.sort(Comparator.comparing(ke -> Optional.ofNullable(
				TargetManager.predicateTarget(golem, ke)
		).map(Enum::ordinal).orElse(100)));

		targets = new LinkedList<>();
		for (var e : list) {
			if (targets.size() < getMaxTarget())
				targets.add(e);
			else break;
		}
	}

	protected abstract int searchRange();

	protected abstract int getMaxTarget();

	protected abstract int cd();

	@Override
	protected final boolean performAttack(LivingEntity target) {
		if (targets == null) {
			makeTarget();
		}
		if (cd > 0) {
			cd--;
			return false;
		}
		if (targets.isEmpty()) {
			targets = null;
			return true;
		}
		var e = targets.poll();
		if (e.isAlive()) {
			performAttackImpl(e);
			cd = cd();
		}
		if (targets.isEmpty()) {
			targets = null;
			return true;
		}
		return false;
	}

	protected abstract void performAttackImpl(LivingEntity target);
}

package dev.xkmc.modulargolems.compat.materials.geoty.revelation;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;

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
				e -> golem.canAttack(e) && golem.hasLineOfSight(e));
		var first = new ArrayList<LivingEntity>();
		var second = new ArrayList<LivingEntity>();
		for (var e : list) {
			if (golem.predicatePriorityTarget(e)) {
				first.add(e);
			} else if (golem.predicateSecondaryTarget(e)) {
				second.add(e);
			}
		}
		targets = new LinkedList<>();
		for (var e : first) {
			if (targets.size() >= getMaxTarget()) return;
			targets.add(e);
		}
		for (var e : second) {
			if (targets.size() >= getMaxTarget()) return;
			targets.add(e);
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

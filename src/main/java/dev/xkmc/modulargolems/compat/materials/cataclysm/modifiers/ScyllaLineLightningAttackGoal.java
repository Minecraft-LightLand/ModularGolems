package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaLineLightningAttackGoal extends BaseRangedAttackGoal {

	private static final int REPEAT_DELAY = 10;

	private int delay = -1;

	public ScyllaLineLightningAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ScyllaLineLightningModifier.COOLDOWN, ScyllaLineLightningModifier.MIN_RANGE, ScyllaLineLightningModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		// 一字闪电只在完整形态（半血以下，或被风暴胸甲钉住）可用
		return CataArmorHelper.stormFinalForm(golem) && super.canUse();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (delay < 0) {
			fire(target);
			if (lv < 2) return true;
			delay = REPEAT_DELAY;
			return false;
		}
		delay--;
		if (delay > 0) return false;
		if (target.isAlive() && golem.canAttack(target)) {
			fire(target);
		}
		return true;
	}

	private void fire(LivingEntity target) {
		GolemCataProxy.scyllaLightningSelfRing(golem);
		double px = target.getX() + target.getDeltaMovement().x * ScyllaLineLightningModifier.LEAD;
		double pz = target.getZ() + target.getDeltaMovement().z * ScyllaLineLightningModifier.LEAD;
		double dx = px - golem.getX(), dz = pz - golem.getZ();
		Vec3 dir = new Vec3(-dz, 0, dx);
		if (dir.lengthSqr() < 1e-6) {
			dx = target.getX() - golem.getX();
			dz = target.getZ() - golem.getZ();
			dir = new Vec3(-dz, 0, dx);
		}
		if (dir.lengthSqr() < 1e-6) dir = new Vec3(1, 0, 0);
		dir = dir.normalize();
		double top = Math.max(target.getY(), golem.getY());
		for (int i = -ScyllaLineLightningModifier.COUNT / 2; i <= ScyllaLineLightningModifier.COUNT / 2; i++) {
			double fx = px + dir.x * ScyllaLineLightningModifier.SPACING * i;
			double fz = pz + dir.z * ScyllaLineLightningModifier.SPACING * i;
			Vec3 floor = EarthquakeHelper.findFloor(golem.level(), new Vec3(fx, top, fz), 16);
			if (floor == null) continue;
			GolemCataProxy.lightningStorm(golem, fx, floor.y, fz, 2.0F);
		}
	}


	@Override
	public void stop() {
		// super releases the EarthquakeHelper busy lock that the backstep holds; skipping it would
		// leave the golem flagged as leaping and lock out every charge and leap skill for good
		super.stop();
		delay = -1;
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}

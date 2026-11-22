package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class EnderGuardianStompGoal extends Goal implements ICooldownSkill {

	protected String cooldownNameKey;
	public EnderGuardianStompGoal withName(String key) {
		this.cooldownNameKey = key;
		return this;
	}

	@Override
	public String getCooldownNameKey() {
		return cooldownNameKey;
	}
	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		long last = golem.getPersistentData().getLong(KEY_TIME);
		return (int) Math.max(0, last + COOLDOWN - time);
	}


	private static final String KEY_TIME = "EnderGuardianStompTime";

	private static final int MAX_TICKS = 22, COOLDOWN = 200, FIRE_TICK = 20;

	private static final double MIN_RANGE = 5, RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private final int lv;
	private LivingEntity target;
	private int ticks;

	public EnderGuardianStompGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		this.golem = golem;
		this.lv = lv;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		// EarthquakeHelper's busy flag is the global lock every charge and leap skill holds; running
		// alongside one of them means both fight over the position this skill repositions
		if (EarthquakeHelper.isBusy(golem)) return false;
		long time = golem.level().getGameTime();
		if (time < golem.getPersistentData().getLong(KEY_TIME) + COOLDOWN) return false;
		if (!golem.onGround()) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		double dist = golem.distanceTo(target);
		return dist >= MIN_RANGE && dist <= RANGE;
	}

	@Override
	public void start() {
		this.target = findTarget();
		this.ticks = 0;
		this.golem.getNavigation().stop();
		this.golem.getPersistentData().putLong(KEY_TIME, golem.level().getGameTime());
		setFacing();
	}

	@Override
	public boolean canContinueToUse() {
		if (!golem.getPassengers().isEmpty() || golem.isRidingGolemCombo()) return false;
		return ticks < MAX_TICKS && target != null;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		this.ticks++;
		setFacing();
		if (this.ticks == FIRE_TICK) {
			LivingEntity target = golem.getTarget();
			if (target == null || !target.isAlive()) target = this.target;
			GolemCataProxy.guardianStompFangs(golem, target);
		}
	}

	private void setFacing() {
		LivingEntity target = golem.getTarget();
		if (target == null) target = this.target;
		if (target == null) return;
		Vec3 dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		float yaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}

	private LivingEntity findTarget() {
		LivingEntity ans = null;
		double best = 0;
		AABB aabb = golem.getBoundingBox().inflate(RANGE);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			double dist = golem.distanceToSqr(e);
			if (dist > RANGE * RANGE) continue;
			if (dist > best) {
				best = dist;
				ans = e;
			}
		}
		return ans;
	}

}

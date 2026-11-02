package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EnderGuardianHugGoal extends Goal implements ICooldownSkill {

	protected String cooldownNameKey;
	public EnderGuardianHugGoal withName(String key) {
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
		return (int) Math.max(0, last + cooldown() - time);
	}


	private static final String KEY_TIME = "EnderGuardianHugTime";

	private static final int MAX_TICKS = 30, COOLDOWN = 200, COOLDOWN_PER_LEVEL = 100, CALC_TICK = 14, TP_TICK = 20, HIT_TICK = 22;

	private static final double MAX_RANGE = 32;

	private final AbstractGolemEntity<?, ?> golem;
	private final int lv;
	private int ticks;
	private double startX, startZ;
	private double predX, predY, predZ;
	private boolean valid;

	public EnderGuardianHugGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		this.golem = golem;
		this.lv = lv;

		setFlags(EnumSet.noneOf(Goal.Flag.class));
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		// EarthquakeHelper's busy flag is the global lock every charge and leap skill holds; running
		// alongside one of them means both fight over the position this skill repositions
		if (EarthquakeHelper.isBusy(golem)) return false;
		long time = golem.level().getGameTime();
		if (time < golem.getPersistentData().getLong(KEY_TIME) + cooldown()) return false;

		if (!golem.onGround()) return false;
		LivingEntity target = golem.getTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		double dist = golem.distanceTo(target);
		return dist <= MAX_RANGE;
	}

	@Override
	public void start() {
		this.ticks = 0;
		this.valid = false;
		LivingEntity target = golem.getTarget();
		if (target != null) {
			this.startX = target.getX();
			this.startZ = target.getZ();
		}
		this.golem.getPersistentData().putLong(KEY_TIME, golem.level().getGameTime());
		setFacing();
	}

	@Override
	public boolean canContinueToUse() {

		if (!golem.getPassengers().isEmpty() || golem.isRidingGolemCombo()) return false;
		return ticks < MAX_TICKS;
	}

	@Override
	public void stop() {
		this.valid = false;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		this.ticks++;
		setFacing();
		if (golem.level().isClientSide()) return;
		LivingEntity target = golem.getTarget();
		if (this.ticks == CALC_TICK && target != null && target.isAlive()) {
			double vx = (target.getX() - startX) / CALC_TICK;
			double vz = (target.getZ() - startZ) / CALC_TICK;

			this.predX = target.getX() + vx * (TP_TICK - CALC_TICK);
			this.predZ = target.getZ() + vz * (TP_TICK - CALC_TICK);
			this.predY = target.getY();

			double dx = this.predX - golem.getX(), dz = this.predZ - golem.getZ();
			double d = Math.sqrt(dx * dx + dz * dz), max = MAX_RANGE;
			if (d > max) {
				this.predX = golem.getX() + dx / d * max;
				this.predZ = golem.getZ() + dz / d * max;
			}
			this.valid = true;
		}
		if (valid && this.ticks >= CALC_TICK && this.ticks <= TP_TICK && golem.level() instanceof ServerLevel sl) {
			for (int i = 0; i < 8; i++) {
				double ang = golem.getRandom().nextDouble() * Math.PI * 2;
				sl.sendParticles(ParticleTypes.END_ROD,
						predX + Math.cos(ang) * 2, predY + 0.3 + golem.getRandom().nextDouble(),
						predZ + Math.sin(ang) * 2, 1, 0, 0, 0, 0);
			}
		}
		if (this.ticks == TP_TICK && valid) {
			golem.teleportTo(predX, predY, predZ);
			valid = false;
		}
		if (this.ticks == HIT_TICK) {
			LivingEntity hugTarget = golem.getTarget();
			GolemCataProxy.hugMe(golem, hugTarget);
			GolemCataProxy.guardianHugFX(golem);
		}
	}

	private int cooldown() {
		return Math.max(0, COOLDOWN - (lv - 1) * COOLDOWN_PER_LEVEL);
	}

	private void setFacing() {
		LivingEntity target = golem.getTarget();
		if (target == null) return;
		Vec3 dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		float yaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}

}

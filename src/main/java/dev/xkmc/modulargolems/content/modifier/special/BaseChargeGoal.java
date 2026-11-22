package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * Shared base for charge skills. Cooldown lives here; busy mutual-exclusion uses GolemMeleeGoal.busy.
 */
public abstract class BaseChargeGoal extends Goal implements ICooldownSkill {

	protected final ChargeHelper.Modifier mod;
	protected final int lv;

	protected int dashGap;

	private BackstepHelper backstep;
	private boolean backstepAirborne;
	protected LivingEntity pendingChargeTarget;

	protected void startDashGap() {
		this.dashGap = ChargeHelper.DASH_GAP;
	}

	protected boolean tickDashGap() {
		if (this.dashGap <= 0) return false;
		this.dashGap--;
		return true;
	}

	private static boolean isBusy(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(EarthquakeHelper.SKILL_BUSY_KEY);
	}

	private static void setBusy(AbstractGolemEntity<?, ?> golem, boolean busy) {
		golem.setTransientFlag(EarthquakeHelper.SKILL_BUSY_KEY, busy);
	}

	protected boolean startBackstep(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		if (this.backstep != null) return true;
		BackstepHelper b = new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
		boolean busy = isBusy(golem);
		if (busy) setBusy(golem, false);
		boolean ok = b.canUseMelee(target);
		if (!ok) {
			if (busy) setBusy(golem, true);
			return false;
		}
		this.backstep = b;
		this.pendingChargeTarget = target;
		this.backstepAirborne = false;
		b.start();
		return true;
	}

	protected boolean tickBackstep() {
		if (this.backstep == null) return false;
		if (!this.backstepAirborne) {
			if (this.backstep.isAirborne()) this.backstepAirborne = true;
			return true;
		}
		if (this.backstep.tick()) {
			this.backstep = null;
			return false;
		}
		return true;
	}

	protected boolean isBackstepping() {
		return this.backstep != null || this.pendingChargeTarget != null;
	}

	protected final boolean canContinueCharge(AbstractGolemEntity<?, ?> golem, Vec3 start, double total,
			int ticks, int maxTicks, boolean blocked) {
		if (blocked) return false;
		if (isBackstepping()) return true;
		if (start == null) return false;
		return ticks < maxTicks && golem.position().distanceToSqr(start) < total * total;
	}

	protected void cancelBackstep() {
		if (this.backstep != null) {
			this.backstep.cancel();
			this.backstep = null;
		}
		this.backstepAirborne = false;
		this.pendingChargeTarget = null;
	}

	protected void finishCharge(AbstractGolemEntity<?, ?> golem, String activeKey) {
		cancelBackstep();
		golem.setTransientFlag(activeKey, false);
		EarthquakeHelper.setBusy(golem, false);
		if (golem.meleeGoal != null) golem.meleeGoal.busy = false;
		golem.meleeGoal.clearDelay();
	}

	protected BaseChargeGoal(ChargeHelper.Modifier mod, int lv) {
		this.mod = mod;
		this.lv = lv;
	}

	private String cooldownTag() {
		return mod.self().getID() + ":timestamp";
	}

	protected boolean onCooldown(AbstractGolemEntity<?, ?> golem) {
		long last = golem.getPersistentData().getLong(cooldownTag());
		return golem.level().getGameTime() < last + mod.getCooldown(golem, lv);
	}

	protected void markUsed(AbstractGolemEntity<?, ?> golem) {
		golem.getPersistentData().putLong(cooldownTag(), golem.level().getGameTime());
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long last = golem.getPersistentData().getLong(cooldownTag());
		return (int) Math.max(0, last + mod.getCooldown(golem, lv) - golem.level().getGameTime());
	}

	@Override
	public String getCooldownNameKey() {
		return mod.self().getDescriptionId();
	}

}

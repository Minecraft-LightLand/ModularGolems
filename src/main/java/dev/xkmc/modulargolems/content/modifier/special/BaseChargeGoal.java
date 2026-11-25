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
		return golem.meleeGoal != null && golem.meleeGoal.busy;
	}

	private static void setBusy(AbstractGolemEntity<?, ?> golem, boolean busy) {
		if (golem.meleeGoal != null) golem.meleeGoal.busy = busy;
	}

	/**
	 * 冲锋前的后撤步实现。默认是跳跃后撤；需要「真瞬移后撤」的傀儡（如灾变末影守卫）
	 * 覆写本方法返回 {@link TeleportBackstepHelper} 即可。
	 */
	protected BackstepHelper chargeBackstep(AbstractGolemEntity<?, ?> golem) {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}

	protected boolean startBackstep(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		if (this.backstep != null) return true;
		BackstepHelper b = chargeBackstep(golem);
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

	/**
	 * 冲锋类技能的通用前置条件：必须站在地面。
	 * <p>
	 * 与大跳共用的 {@code EarthquakeHelper} busy 锁是双重保险：busy 锁只保证两者不同时施放，
	 * 但落地判定与起手的时序偶有窗口，这里再挡一次，避免在空中起冲。
	 * <p>
	 * 水上/空中的变体（如 riptide）不要调这个，改用自己的环境判定。
	 */
	protected boolean canStartCharge(AbstractGolemEntity<?, ?> golem) {
		// 冲锋的公共前置：骑乘/载客/远程模式（含停留模式）一律不放。
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		// 作为"玩家骑着的坐骑"身上的傀儡乘客时，也不许 AI 自己冲锋（否则会把冲锋作用到坐骑身上）
		if (golem.getVehicle() instanceof AbstractGolemEntity<?, ?> mv && mv.hasPlayerPassenger()) return false;
		return golem.onGround();
	}

	/**
	 * 冲锋 {@code canUse()} 的公共模板：
	 * <ul>
	 *   <li>位移/状态门槛 → {@link #canStartCharge}（骑乘/载客/远程模式/落地）；</li>
	 *   <li>与跳跃技能共用的 busy 锁、冷却；</li>
	 *   <li>目标有效性、可达性、距离区间。</li>
	 * </ul>
	 * 子类只需提供「取哪个目标」「距离区间」「额外门槛」，不必再抄这几条公共判定。
	 *
	 * @param target 已取好的目标，可为 null
	 * @param near   最小距离（格）
	 * @param far    最大距离（格）
	 */
	protected boolean canUseCharge(AbstractGolemEntity<?, ?> golem, LivingEntity target, double near, double far) {
		if (!canStartCharge(golem)) return false;
		// EarthquakeHelper 的 busy 标志是所有冲锋与跳跃技能共用的全局锁
		if (EarthquakeHelper.isBusy(golem)) return false;
		if (onCooldown(golem)) return false;
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		double dist = golem.distanceTo(target);
		return dist >= near && dist <= far;
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

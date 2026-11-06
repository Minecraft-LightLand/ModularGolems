package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * Shared base for charge skills. Keeps the cooldown in one place: it reads and writes the same
 * {@code <modifier id>:timestamp} used by {@link ChargeHelper}, so charge cooldowns behave exactly
 * like jump and ranged cooldowns and no longer need a per-skill key.
 */
public abstract class BaseChargeGoal extends Goal implements ICooldownSkill {

	protected final ChargeHelper.Modifier mod;
	protected final int lv;

	/**
	 * 多段冲锋的段间等待剩余 tick。由 {@link #tickDashGap} 每 tick 递减；
	 * 大于 0 时技能应原地停住、不推进也不结算。
	 */
	protected int dashGap;

	/**
	 * 冲锋前用来拉开距离的后撤步。目标太近时先起跳后撤，落地后再真正冲锋。
	 */
	private BackstepHelper backstep;
	/** 后撤步起跳后要先空过一帧：start() 只设速度，实体下一次 tick 才离地，
	 * 否则 Goal 同帧调用 tick() 时会因仍在 onGround 而误判为已落地，导致刚起跳就冲锋。 */
	private boolean backstepAirborne;
	/** 后撤步落地后要冲锋的目标；非空表示下一帧应调用子类的 begin。 */
	protected net.minecraft.world.entity.LivingEntity pendingChargeTarget;

	/**
	 * 进入段间等待。下一段应当在 {@link #isDashGapDone} 返回 true 后开始。
	 */
	protected void startDashGap() {
		this.dashGap = ChargeHelper.DASH_GAP;
	}

	/**
	 * 推进段间等待。返回 true 表示还在等待中，调用方应直接 return（不推进、不结算）。
	 */
	protected boolean tickDashGap() {
		if (this.dashGap <= 0) return false;
		this.dashGap--;
		return true;
	}

	/**
	 * 目标太近时起点后撤步。返回 true 表示已经起跳（这一帧不要 begin，等 {@link #tickBackstep} 落地）。
	 * 触发距离沿用 {@link BackstepHelper#canUse}：非远程模式下按傀儡自身 reach - 0.5 判定。
	 */
	protected boolean startBackstep(AbstractGolemEntity<?, ?> golem, net.minecraft.world.entity.LivingEntity target) {
		if (this.backstep != null) return true;
		BackstepHelper b = new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
		// 各冲锋 Goal 的 start() 会先 setBusy(true) 再调这里，而 BackstepHelper.canUse 第一件事就是检查 busy，
		// 不先解锁会导致后撤步永远不触发。判定完按结果重新占锁：起跳由 b.start() 自己占，没起跳则由调用方继续。
		boolean busy = golem.isTransientFlag(EarthquakeHelper.SKILL_BUSY_KEY);
		if (busy) golem.setTransientFlag(EarthquakeHelper.SKILL_BUSY_KEY, false);
		// 冲锋一律按「目标已进入自身近战距离」判定，不用远程 leapRange（否则有远程技能的傀儡会在 16 格外乱撤）
		boolean ok = b.canUseMelee(target);
		if (!ok) {
			if (busy) golem.setTransientFlag(EarthquakeHelper.SKILL_BUSY_KEY, true);
			return false;
		}
		this.backstep = b;
		this.pendingChargeTarget = target;
		this.backstepAirborne = false;
		b.start();
		return true;
	}

	/**
	 * 每 tick 推进后撤步。返回 true 表示还在空中（调用方应直接 return）。落地那一帧返回 false，
	 * 此时 {@link #pendingChargeTarget} 仍保留，调用方据此开始真正的冲锋。
	 */
	protected boolean tickBackstep() {
		if (this.backstep == null) return false;
		if (!this.backstepAirborne) {
			// 起跳那一帧实体还没真正离地，先等它离地再开始判落地
			if (this.backstep.isAirborne()) this.backstepAirborne = true;
			return true;
		}
		if (this.backstep.tick()) {
			this.backstep = null;
			return false;
		}
		return true;
	}

	/**
	 * 后撤步是否仍在进行。这段窗口里子类的 begin() 还没被调用，冲锋起点 start 仍是 null，
	 * 子类的 canContinueToUse 必须据此放行，否则会解引用 null 崩服。
	 */
	protected boolean isBackstepping() {
		return this.backstep != null || this.pendingChargeTarget != null;
	}

	/**
	 * 单段冲锋的通用“是否继续”判定，供各冲锋 Goal 的 {@code canContinueToUse()} 统一调用：
	 * <ul>
	 *   <li>blocked / 后撤步优先处理（后撤步期间 begin() 尚未执行，start 仍可能为 null）；</li>
	 *   <li>{@code start == null} 时直接结束，避免对 null 起点做距离判定而崩服；</li>
	 *   <li>否则按“跑够距离或超时”结束。</li>
	 * </ul>
	 *
	 * @param maxTicks 该技能的最长冲锋 tick（各技能可能不同）
	 */
	protected final boolean canContinueCharge(AbstractGolemEntity<?, ?> golem, Vec3 start, double total,
			int ticks, int maxTicks, boolean blocked) {
		if (blocked) return false;
		if (isBackstepping()) return true;
		if (start == null) return false;
		return ticks < maxTicks && golem.position().distanceToSqr(start) < total * total;
	}

	/** 在 stop() 里调用，取消未完成的后撤步，避免永久占住 busy 锁。 */
	protected void cancelBackstep() {
		if (this.backstep != null) {
			this.backstep.cancel();
			this.backstep = null;
		}
		this.backstepAirborne = false;
		this.pendingChargeTarget = null;
	}

	/**
	 * 冲锋结束时统一收尾：撤销未完成后撤步、清活动标志、释放 busy 锁，并重寻路。
	 * 冲锋期间路径被 getNavigation().stop() 丢弃，而近战 repath 只在目标移动或 5% 概率时触发，
	 * 不主动 clearDelay 的话傀儡会在冲锋终点原地愣一会儿才恢复追击。
	 * 子类若有额外收尾（如发射火箭拳）可在自己 stop() 里继续调用其它逻辑。
	 */
	protected void finishCharge(AbstractGolemEntity<?, ?> golem, String activeKey) {
		cancelBackstep();
		golem.setTransientFlag(activeKey, false);
		EarthquakeHelper.setBusy(golem, false);
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

package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 一字闪电（本体 {@code Scylla_Entity.Scylla_Lightning_Explosion}，attackState 18）：
 * <ul>
 *   <li>tick 25 / 53 各落一排雷，共 9 道，间距 1.25 格，size 3.0；</li>
 *   <li>这一排是沿**目标自身朝向**铺开的（{@code target.getYRot()}），不是垂直于连线的横线；</li>
 *   <li>落点带提前量：本体用「目标当前速度 × lifeticks」外推；</li>
 *   <li>tick 24~54 本体脚下持续冒圈状闪电粒子；</li>
 *   <li>tick 25 / 53 本体同时以 4 格半径结算一次闪电伤害。</li>
 * </ul>
 */
public class ScyllaLineLightningAttackGoal extends BaseRangedAttackGoal {

	/** 两次打击之间本体也在脚下冒粒子。 */
	private static final int SPARK_INTERVAL = 2;

	private LivingEntity castTarget;
	private int castTicks;
	private boolean casting;
	/** 上一次采样到的目标位置，用来算提前量（本体 start() 里记 prevX/prevZ）。 */
	private Vec3 prevPos;
	/** 上一排打的是谁；换目标时清掉 prevPos，避免把上一个目标的位移当成速度算出离谱的提前量。 */
	private LivingEntity lastFireTarget;

	public ScyllaLineLightningAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ScyllaLineLightningModifier.cooldownFor(lv), ScyllaLineLightningModifier.MIN_RANGE,
				ScyllaLineLightningModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		// 一字闪电只在完整形态（半血以下，或被风暴胸甲钉住）可用
		return CataArmorHelper.stormFinalForm(golem) && super.canUse();
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTarget = null;
		castTicks = 0;
		prevPos = null;
		lastFireTarget = null;
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
		castTarget = null;
		prevPos = null;
		lastFireTarget = null;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}

	@Override
	public void tick() {
		if (casting) {
			// 只有傀儡本身没了才中断整段施放。目标死亡不再直接中止——否则第一排把目标打死后，
			// 后面几排会因为"目标失效"被整段取消（等级 2 有时只放一次的根因）。
			if (golem.isRemoved()) {
				casting = false;
				castTarget = null;
			} else {
				castTicks++;
				// 本体 tick 24~54：脚下持续冒圈状闪电
				if (castTicks >= ScyllaLineLightningModifier.SPARK_START
						&& castTicks < ScyllaLineLightningModifier.SPARK_END
						&& castTicks % SPARK_INTERVAL == 0) {
					GolemCataProxy.scyllaThunderCloudLightning(golem,
							golem.getX(), golem.getY() + 0.1, golem.getZ());
				}
				// 固定两排。每一排都在开火当刻重新索敌：优先傀儡当前锁定的活目标，打死一个就自动改打下一个。
				if (castTicks == ScyllaLineLightningModifier.FIRST_TICK
						|| castTicks == ScyllaLineLightningModifier.SECOND_TICK) {
					LivingEntity target = acquireTarget();
					if (target != null) fire(target);
				}
				if (castTicks > ScyllaLineLightningModifier.SPARK_END) {
					casting = false;
					castTarget = null;
				}
			}
		}
		super.tick();
	}

	/**
	 * 开火当刻该打谁：优先傀儡此刻锁定的活目标（第一排击杀后傀儡会锁下一个）；
	 * 当前没有活目标时退回施放时记下的目标位置，保证这一排照样落下（打尸体无伤害，但视觉/时序与本体一致）。
	 */
	private LivingEntity acquireTarget() {
		LivingEntity current = golem.getTarget();
		if (current != null && current.isAlive() && !current.isRemoved()) {
			castTarget = current;
			return current;
		}
		return castTarget;
	}

	/**
	 * 落一排雷。本体把落点取在**目标位置**并按目标自身朝向铺开，所以傀儡这里也用目标坐标与目标 yaw，
	 * 而不是「傀儡→目标」的垂直方向。
	 */
	private void fire(LivingEntity target) {
		// 本体 tick 25/53：HEAVY_SMASH(0.5) + SUPER_LIGHTNING(0.2)
		GolemCataProxy.scyllaLineLightningSound(golem);
		GolemCataProxy.scyllaLightningSelfRing(golem);
		// 提前量：本体用「目标位置 - 上一 tick 采样位置」的速度外推 lifeticks
		double vx = 0, vz = 0;
		if (prevPos != null && target == lastFireTarget) {
			int dt = Math.max(1, ScyllaLineLightningModifier.FIRST_TICK);
			vx = (target.getX() - prevPos.x) / dt;
			vz = (target.getZ() - prevPos.z) / dt;
		}
		prevPos = target.position();
		lastFireTarget = target;
		double px = target.getX() + vx * ScyllaLineLightningModifier.LEAD;
		double pz = target.getZ() + vz * ScyllaLineLightningModifier.LEAD;
		// 本体用 target.getYRot()：这一排横在目标身上
		GolemCataProxy.scyllaLineLightningRow(golem, px, pz, target.getYRot());
		// 本体 tick 25/53 还会以自身为中心 4 格半径结算一次闪电伤害
		GolemCataProxy.scyllaLightningExplosion(golem, golem.getX(), golem.getY(), golem.getZ());
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTarget = target;
		castTicks = 0;
		prevPos = null;
		lastFireTarget = null;
		casting = true;
		return true;
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

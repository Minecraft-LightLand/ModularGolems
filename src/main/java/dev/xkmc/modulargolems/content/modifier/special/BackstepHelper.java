package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/**
 * 技能在开火前用来拉开距离的后撤步。
 *
 * <p>只有站在地面上、原地不动的傀儡才会用：正在跳跃或冲锋的傀儡本身就在移动；
 * 被命令原地固守的傀儡不该乱跳；目标很远时也完全不需要闪避。
 *
 * <p>傀儡在整个腾空期间保持忙碌，落地时才消耗冲锋技能的冷却，
 * 所以后撤步结束的瞬间不会立刻接着冲锋。这与跳跃技能的做法一致——
 * 它们在整段飞行期间都保持忙碌。
 */
public class BackstepHelper {

	private static final double EPS = 1.0E-6;

	/**
	 * 多数技能使用的后撤距离，单位：格。
	 */
	public static final double DEFAULT_DISTANCE = 16;
	/**
	 * 后撤跳的顶点高度，单位：格；垂直初速由它推导。
	 */
	public static final double DEFAULT_HEIGHT = 1.0;
	/**
	 * 目标超过这个距离就不再值得后撤。
	 */
	public static final double DEFAULT_LEAP_RANGE = 8;

	private final AbstractGolemEntity<?, ?> golem;
	private final double distance;
	private final double height;

	public BackstepHelper(AbstractGolemEntity<?, ?> golem, double distance, double height) {
		this.golem = golem;
		this.distance = distance;
		this.height = height;
	}

	/**
	 * 傀儡此刻是否可以起跳，以及目标是否近到后撤有意义。
	 * 远程模式下用技能给的 leap range；近战时按自身 reach 减半格判断，
	 * 只躲已经贴到脸上的目标。结果非正数时跳过距离检查。
	 */
	public boolean canUse(LivingEntity target, double range) {
		if (!golem.onGround() || EarthquakeHelper.isBusy(golem)) return false;
		if (golem.getMode() == GolemModes.STAND) return false;
		// 远程傀儡从远处开火，技能 leap range（默认 8 格）内的目标都值得后撤；
		// 近战傀儡只在目标进入自身 reach（比近战距离短半格）时才闪避。
		double reach = golem.isInRangedMode() ? range
				: golem.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 0.5;
		return reach <= 0 || golem.distanceTo(target) <= reach;
	}

	/**
	 * 冲锋技能用的判定：无论傀儡当前是否被判为远程模式，一律按自身近战 reach 判断，
	 * 也就是只有目标真的贴脸（进入攻击距离）时才后撤。远程技能仍走 {@link #canUse}。
	 */
	public boolean canUseMelee(LivingEntity target) {
		if (!golem.onGround() || EarthquakeHelper.isBusy(golem)) return false;
		if (golem.getMode() == GolemModes.STAND) return false;
		double reach = golem.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 0.5;
		return reach <= 0 || golem.distanceTo(target) <= reach;
	}

	/**
	 * 起跳，并一直占住 busy 锁直到 {@link #tick} 检测到落地。
	 */
	public void start() {
		var back = golem.getForward();
		back = new Vec3(back.x, 0, back.z);
		if (back.lengthSqr() < EPS) back = new Vec3(0, 0, 1);
		else back = back.normalize().scale(-1.0);
		var motion = golem.getDeltaMovement();
		golem.hasImpulse = true;
		golem.setDeltaMovement(back.x * speed(), motion.y + verticalSpeed(), back.z * speed());
		// 丢弃傀儡当前跟随的路径。那条路径是为它正要离开的位置规划的；
		// 腾空期间原版导航会取消任何下一节点高于自己的路径，
		// 留着它只会得到一条在下降途中被丢弃的废路径。
		golem.getNavigation().stop();
		EarthquakeHelper.setBusy(golem, true);
	}

	/** 是否已经真正离地（起跳那一帧速度刚设、实体还没动，需要靠这个判断）。 */
	public boolean isAirborne() {
		return !golem.onGround();
	}

	/**
	 * 腾空期间保持 busy 锁，落地时释放。落地那一帧返回 true，
	 * 调用方据此恰好消耗一次冲锋冷却。
	 */
	public boolean tick() {
		if (!golem.onGround()) return false;
		EarthquakeHelper.setBusy(golem, false);
		// 上面腾空时取消路径会让导航保持空闲，而重新寻路只在目标移动或 5% 概率时触发。
		// 这里主动请求一条新路径，否则落在静止目标旁的傀儡会站在原地打完这场战斗。
		golem.meleeGoal.clearDelay();
		return true;
	}

	/**
	 * 释放 busy 锁但不消耗冷却。技能在空中被打断时调用，
	 * 否则傀儡会一直占着锁，所有冲锋技能都会被永久锁死。
	 */
	public void cancel() {
		EarthquakeHelper.setBusy(golem, false);
	}

	/**
	 * 飞行期间走完 {@link #distance} 所需的水平速度，
	 * 已把实体腾空时每 tick 损失的摩擦补偿进去。
	 */
	private double speed() {
		return distance / (flightTicks() * friction());
	}

	private double verticalSpeed() {
		return Math.sqrt(2.0 * height * gravity());
	}

	/**
	 * 上升再落回地面所用的 tick 数，水平速度要按它来配。
	 */
	private double flightTicks() {
		return 2.0 * Math.sqrt(2.0 * height / gravity());
	}

	/**
	 * 可由技能覆写：重力和空气摩擦决定一次起跳实际能飞多远。
	 */
	protected double gravity() {
		return 0.05;
	}

	protected double friction() {
		return 0.91;
	}

}

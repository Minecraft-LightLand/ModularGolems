package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * 技能在开火前用来拉开距离的后撤步。腾空期间保持 busy 锁，落地释放并消耗冲锋冷却。
 */
public class BackstepHelper {

	private static final double EPS = 1.0E-6;

	public static final double DEFAULT_DISTANCE = 16;
	public static final double DEFAULT_HEIGHT = 1.0;
	public static final double DEFAULT_LEAP_RANGE = 8;

	private final AbstractGolemEntity<?, ?> golem;
	private final double distance;
	private final double height;

	public BackstepHelper(AbstractGolemEntity<?, ?> golem, double distance, double height) {
		this.golem = golem;
		this.distance = distance;
		this.height = height;
	}

	private static boolean isBusy(AbstractGolemEntity<?, ?> golem) {
		return golem.meleeGoal != null && golem.meleeGoal.busy;
	}

	private static void setBusy(AbstractGolemEntity<?, ?> golem, boolean busy) {
		if (golem.meleeGoal != null) golem.meleeGoal.busy = busy;
	}

	private static double meleeReach(AbstractGolemEntity<?, ?> golem) {
		return golem.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) - 0.5;
	}

	public boolean canUse(LivingEntity target, double range) {
		if (!golem.onGround() || isBusy(golem)) return false;
		if (golem.getMode() == GolemModes.STAND) return false;
		double reach = golem.isInRangedMode() ? range : meleeReach(golem);
		return reach <= 0 || golem.distanceTo(target) <= reach;
	}

	public boolean canUseMelee(LivingEntity target) {
		if (!golem.onGround() || isBusy(golem)) return false;
		if (golem.getMode() == GolemModes.STAND) return false;
		double reach = meleeReach(golem);
		return reach <= 0 || golem.distanceTo(target) <= reach;
	}

	public void start() {
		var back = golem.getForward();
		back = new Vec3(back.x, 0, back.z);
		if (back.lengthSqr() < EPS) back = new Vec3(0, 0, 1);
		else back = back.normalize().scale(-1.0);
		var motion = golem.getDeltaMovement();
		golem.hasImpulse = true;
		golem.setDeltaMovement(back.x * speed(), motion.y + verticalSpeed(), back.z * speed());
		golem.getNavigation().stop();
		setBusy(golem, true);
	}

	public boolean isAirborne() {
		return !golem.onGround();
	}

	public boolean tick() {
		if (!golem.onGround()) return false;
		setBusy(golem, false);
		golem.meleeGoal.clearDelay();
		return true;
	}

	public void cancel() {
		setBusy(golem, false);
	}

	private double speed() {
		return distance / (flightTicks() * friction());
	}

	private double verticalSpeed() {
		return Math.sqrt(2.0 * height * gravity());
	}

	private double flightTicks() {
		return 2.0 * Math.sqrt(2.0 * height / gravity());
	}

	protected double gravity() {
		return 0.05;
	}

	protected double friction() {
		return 0.91;
	}

}

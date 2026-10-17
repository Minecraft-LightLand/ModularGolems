package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/**
 * A backward leap that a skill can use to open space before it fires.
 *
 * <p>Only offered to a golem that is standing still on the ground: one already busy with a jump or a
 * charge is moving on its own, a golem told to hold its ground has no business hopping, and a distant
 * target needs no dodging at all.
 *
 * <p>The golem stays busy until it lands, and landing spends the charge skills' cooldown, so a charge
 * does not start the moment the backstep ends. This mirrors what the jump skills do, which stay busy
 * for their whole flight.
 */
public class BackstepHelper {

	private static final double EPS = 1.0E-6;

	/**
	 * Backwards leap distance used by most skills, in blocks.
	 */
	public static final double DEFAULT_DISTANCE = 16;
	/**
	 * Apex of the leap, in blocks; the vertical speed is derived from it.
	 */
	public static final double DEFAULT_HEIGHT = 1.0;
	/**
	 * Distance at which a target stops being worth backing away from.
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
	 * Whether the golem may leap right now, and is close enough to the target for backing away to mean
	 * anything. While ranged it uses the skill's leap range; while fighting hand to hand it measures
	 * against its own reach minus half a block, so it only dodges what is already at its throat. A
	 * non-positive result skips the distance check.
	 */
	public boolean canUse(LivingEntity target, double range) {
		if (!golem.onGround() || EarthquakeHelper.isBusy(golem)) return false;
		if (golem.getMode() == GolemModes.STAND) return false;
		// a ranged golem opens fire from a distance, so backing off makes sense for anything within
		// the skill's leap range (eight blocks by default); a golem fighting hand to hand only dodges
		// once the target is inside its own reach, half a block short of its melee range.
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
	 * Starts the leap and holds the golem busy until {@link #tick} sees it land.
	 */
	public void start() {
		var back = golem.getForward();
		back = new Vec3(back.x, 0, back.z);
		if (back.lengthSqr() < EPS) back = new Vec3(0, 0, 1);
		else back = back.normalize().scale(-1.0);
		var motion = golem.getDeltaMovement();
		golem.hasImpulse = true;
		golem.setDeltaMovement(back.x * speed(), motion.y + verticalSpeed(), back.z * speed());
		// drop the path the golem was following. It was built for the spot it is leaving, and while it is
		// airborne the vanilla navigation cancels any path whose next step sits above it, so keeping it
		// around only produces a path that gets thrown away on the way down.
		golem.getNavigation().stop();
		EarthquakeHelper.setBusy(golem, true);
	}

	/**
	 * Keeps the hold while the golem is in the air and releases it on landing. Returns true on the tick
	 * the golem comes down, so the caller can spend the charge cooldowns exactly once.
	 */
	/** 是否已经真正离地（起跳那一帧速度刚设、实体还没动，需要靠这个判断）。 */
	public boolean isAirborne() {
		return !golem.onGround();
	}

	public boolean tick() {
		if (!golem.onGround()) return false;
		EarthquakeHelper.setBusy(golem, false);
		// the airborne cancellation above leaves navigation idle, and repath only restarts when the
		// target has moved or on a 5% roll. Ask for a fresh path now, or a golem that landed on a still
		// target stands there for the rest of the fight.
		golem.meleeGoal.clearDelay();
		return true;
	}

	/**
	 * Drops the hold without spending the cooldowns. Call this when the skill is cut short mid leap,
	 * otherwise the golem would stay busy and every charge skill would be locked out for good.
	 */
	public void cancel() {
		EarthquakeHelper.setBusy(golem, false);
	}

	/**
	 * Horizontal speed needed to travel {@link #distance} during the flight, made up for by the friction
	 * the entity loses each tick while airborne.
	 */
	private double speed() {
		return distance / (flightTicks() * friction());
	}

	private double verticalSpeed() {
		return Math.sqrt(2.0 * height * gravity());
	}

	/**
	 * Ticks spent up and back down again, which is what the horizontal speed has to be sized against.
	 */
	private double flightTicks() {
		return 2.0 * Math.sqrt(2.0 * height / gravity());
	}

	/**
	 * Overridable per skill: gravity and air friction decide how far a launch actually carries.
	 */
	protected double gravity() {
		return 0.05;
	}

	protected double friction() {
		return 0.91;
	}

}

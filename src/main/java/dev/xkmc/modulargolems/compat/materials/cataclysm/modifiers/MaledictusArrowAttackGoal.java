package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

/**
 * A volley of four ghost arrows, each locking its own target when several are in range and all piling
 * onto the same one otherwise. Shares {@link BurstRangedGoal} with the other burst skills, which spreads
 * the shots over the targets and spaces them out over consecutive ticks.
 */
public class MaledictusArrowAttackGoal extends BurstRangedGoal {

	private static final int ARROW_COUNT = 4;

	public MaledictusArrowAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(0, 20, golem, lv);
	}

	@Override
	protected int getBurstCount() {
		return ARROW_COUNT;
	}

	/** the four arrows leave together, then the skill goes on cooldown */
	@Override
	protected int getBurstDelay() {
		return 0;
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}

	/**
	 * Leaps away from the target so the volley is not fired from right on top of it. The speeds come from
	 * the wanted distance and apex: a jump of {@code h} blocks hangs for {@code 2*sqrt(2h/g)} ticks, and
	 * the horizontal speed needed to cover the ground in that time is corrected for air friction.
	 */
	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		if (target == null || !target.isAlive()) return;
		Vec3 eye = golem.getEyePosition().add(0, -0.1, 0);
		Vec3 dir = target.getEyePosition().subtract(eye).normalize().scale(1.5);
		AbstractArrow arrow = MWCataProxy.createGhostArrow(golem.level(), golem, target);
		if (arrow == null) return;
		arrow.setPos(eye);
		arrow.setDeltaMovement(
				dir.x + (golem.getRandom().nextDouble() - 0.5) * 0.2,
				dir.y + (golem.getRandom().nextDouble() + 2.0) * 0.1,
				dir.z + (golem.getRandom().nextDouble() - 0.5) * 0.2);
		arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		golem.level().addFreshEntity(arrow);
	}

}

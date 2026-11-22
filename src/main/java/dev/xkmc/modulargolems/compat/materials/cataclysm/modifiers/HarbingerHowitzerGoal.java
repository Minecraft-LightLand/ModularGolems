package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Lobs a volley of howitzer shells at the target. Built on {@link BaseRangedAttackGoal} like the other
 * ranged skills, so it shares their cooldown and the melee goal lockout.
 */
public class HarbingerHowitzerGoal extends BaseRangedAttackGoal {

	public HarbingerHowitzerGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 20, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide()) return true;

		double d3 = target.getX() - golem.getX();
		double d4 = target.getY() + target.getEyeHeight() * 0.5 - golem.getEyeY();
		double d5 = target.getZ() - golem.getZ();
		double d6 = Math.sqrt(d3 * d3 + d5 * d5);
		Vec3 dir = new Vec3(d3, d4 + d6 * 6F, d5);

		int count = lv >= 2 ? 7 : 5;
		for (int i = 0; i < count; i++) {
			GolemCataProxy.shootHowitzer(golem, dir);
		}
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

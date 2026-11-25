package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Fires three tracking missiles in a row, spreading them over nearby targets and dumping every missile
 * on the same one when there is nobody else in range. Shares {@link BurstRangedGoal} with the other
 * three shot skills, which is what spreads a burst over several ticks and cycles the target list.
 */
public class HarbingerMissileAttackGoal extends BurstRangedGoal {

	public HarbingerMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(HarbingerMissileModifier.cooldownFor(lv), HarbingerMissileModifier.MIN_RANGE, HarbingerMissileModifier.MAX_RANGE, golem, lv);
	}

	/** the three missiles leave one by one, four ticks apart */
	@Override
	protected int getBurstDelay() {
		return 4;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		var dir = target.getEyePosition().subtract(golem.getEyePosition()).normalize();
		MWCataProxy.shootMissile(golem, dir);
		var flat = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		float yaw = (float) (Mth.atan2(-flat.x, flat.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
	@Override
	protected int getWaitTime() {
		int base = super.getWaitTime();
		return CataArmorHelper.harbingerFinalForm(golem) ? Math.max(1, base / 2) : base;
	}

}

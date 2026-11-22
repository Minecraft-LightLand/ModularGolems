package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaLightningStormAttackGoal extends BaseRangedAttackGoal {

	private static final int CLOUD_TICKS = 60, SPEAR_TICKS = 30;

	private LivingEntity castTarget;
	private int castTicks;
	private boolean casting;

	public ScyllaLightningStormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 30, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTarget = null;
		castTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
		castTarget = null;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}

	@Override
	public void tick() {
		if (casting) {
			if (golem.isRemoved()) {
				casting = false;
				castTarget = null;
			} else {
				castTicks++;
				double x = golem.getX();
				double y = golem.getY() + golem.getBbHeight() * 2.8;
				double z = golem.getZ();
				if (castTicks <= CLOUD_TICKS) {
					GolemCataProxy.scyllaThunderCloudStorm(golem, x, y, z);
				} else if (castTicks <= CLOUD_TICKS + SPEAR_TICKS) {
					LivingEntity target = golem.getTarget();
					if (target == null || target.isRemoved()) {
						target = castTarget;
					}
					if (target != null && !target.isRemoved()) {
						GolemCataProxy.scyllaThunderCloudSpear(golem, target, x, y, z);
					}
				} else {
					casting = false;
					castTarget = null;
				}
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTarget = target;
		castTicks = 0;
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

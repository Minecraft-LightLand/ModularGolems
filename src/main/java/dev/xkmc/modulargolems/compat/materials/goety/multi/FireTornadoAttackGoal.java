package dev.xkmc.modulargolems.compat.materials.goety.multi;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;

import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class FireTornadoAttackGoal extends BurstRangedGoal implements IApostleGoal {

	public FireTornadoAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 16 + lv * 2, golem, lv);
		useMultiTargetSemantics();
	}

	@Override
	protected double searchRange() {
		return 16 + lv * 2;
	}

	@Override
	protected int getBurstCount() {
		return lv + 2;
	}

	@Override
	protected int getBurstDelay() {
		return 10;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		var level = golem.level();
		double d1 = target.getX() - golem.getX();
		double d2 = target.getY(0.5F) - golem.getY(0.5F);
		double d3 = target.getZ() - golem.getZ();
		FireTornado e = new FireTornado(level, golem, d1, d2, d3);
		e.setOwnerId(golem.getUUID());
		e.setTotalLife(140 + 20 * lv);
		e.setPos(golem.getX(), golem.getY(), golem.getZ());
		e.setDamage(9 + 3 * lv);
		level.addFreshEntity(e);
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

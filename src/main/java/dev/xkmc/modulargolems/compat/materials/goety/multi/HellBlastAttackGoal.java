package dev.xkmc.modulargolems.compat.materials.goety.multi;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;

import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class HellBlastAttackGoal extends BurstRangedGoal implements IApostleGoal {

	public HellBlastAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 16, golem, lv);
		useMultiTargetSemantics();
	}

	@Override
	protected int getBurstCount() {
		return lv * 2;
	}

	@Override
	protected double searchRange() {
		return 16;
	}

	@Override
	protected int getBurstDelay() {
		return Math.max(4 - lv, 0) * 2;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		var level = golem.level();
		Vec3 dir = target.position()
				.add(0, target.getBbHeight() / 2, 0)
				.subtract(golem.getEyePosition())
				.normalize();
		HellBlast hellBlast = new HellBlast(
				golem.getX() + dir.x / (double) 2.0F,
				this.golem.getEyeY() - 0.2,
				this.golem.getZ() + dir.z / (double) 2.0F,
				dir.x, dir.y, dir.z, level);
		hellBlast.setDamage(10 + lv * 10);
		hellBlast.setOwner(golem);
		level.addFreshEntity(hellBlast);
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

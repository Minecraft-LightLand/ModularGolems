package dev.xkmc.modulargolems.compat.materials.goety.multi;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;

import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class HellBoltAttackGoal extends BurstRangedGoal implements IApostleGoal {

	public HellBoltAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
		useMultiTargetSemantics();
	}

	@Override
	protected int getBurstCount() {
		return lv * 4;
	}

	@Override
	protected double searchRange() {
		return 35;
	}

	@Override
	protected int getBurstDelay() {
		return Math.max(4 - lv, 0);
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		var level = golem.level();
		Vec3 dir = target.position()
				.add(0, target.getBbHeight() / 2, 0)
				.subtract(golem.getEyePosition()).normalize();
		HellBolt hellBolt = new HellBolt(
				golem.getX() + dir.x / (double) 2.0F,
				golem.getEyeY() - 0.2,
				golem.getZ() + dir.z / (double) 2.0F, dir.x, dir.y, dir.z, level);
		hellBolt.setOwner(golem);
		hellBolt.setDamage(5 + lv * 5);
		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.BLAZE_SHOOT, golem.getSoundSource(), 1.0F, 1.0F);
		level.addFreshEntity(hellBolt);
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

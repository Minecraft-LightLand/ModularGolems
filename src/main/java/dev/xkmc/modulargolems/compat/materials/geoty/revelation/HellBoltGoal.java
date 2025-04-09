package dev.xkmc.modulargolems.compat.materials.geoty.revelation;

import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HellBoltGoal extends MultiTargetRangedGoal {

	public HellBoltGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 1, 35, golem, lv);
	}

	protected int getMaxTarget() {
		return lv * 4;
	}

	@Override
	protected int searchRange() {
		return 35;
	}

	@Override
	protected int cd() {
		return Math.max(3 - lv, 0);
	}

	protected void performAttackImpl(LivingEntity target) {
		var level = golem.level();
		Vec3 dir = target.position()
				.add(0, target.getBbHeight() / 2, 0)
				.subtract(golem.getEyePosition()).normalize();
		HellBolt hellBolt = new HellBolt(
				golem.getX() + dir.x / (double) 2.0F,
				golem.getEyeY() - 0.2,
				golem.getZ() + dir.z / (double) 2.0F, dir.x, dir.y, dir.z, level);
		hellBolt.setOwner(golem);
		level.playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.BLAZE_SHOOT, golem.getSoundSource(), 1.0F, 1.0F);
		level.addFreshEntity(hellBolt);
	}

}

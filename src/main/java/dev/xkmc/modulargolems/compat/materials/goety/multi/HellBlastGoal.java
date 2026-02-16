package dev.xkmc.modulargolems.compat.materials.goety.multi;

import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HellBlastGoal extends MultiTargetRangedGoal implements IApostleGoal {

	public HellBlastGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 16, golem, lv);
	}

	protected int getMaxTarget() {
		return lv * 2;
	}

	@Override
	protected int searchRange() {
		return 16;
	}

	@Override
	protected int cd() {
		return Math.max(4 - lv, 0) * 2;
	}

	protected void performAttackImpl(LivingEntity target) {
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
		hellBlast.setDamage(3 + lv * 2);
		hellBlast.setOwner(golem);
		level.addFreshEntity(hellBlast);
	}

}

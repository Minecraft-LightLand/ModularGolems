package dev.xkmc.modulargolems.compat.materials.goety.multi;

import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class FireTornadoGoal extends MultiTargetRangedGoal implements IApostleGoal {

	public FireTornadoGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 16 + lv * 2, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 16 + lv * 2;
	}

	@Override
	protected int getMaxTarget() {
		return lv + 2;
	}

	@Override
	protected int cd() {
		return 10;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
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
}

package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class FireTornadoGoal extends BaseRangedAttackGoal {

	public FireTornadoGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 1, 16 + lv * 2, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		var level = golem.level();
		double d1 = target.getX() - golem.getX();
		double d2 = target.getY(0.5F) - golem.getY(0.5F);
		double d3 = target.getZ() - golem.getZ();
		FireTornado e = new FireTornado(level, golem, d1, d2, d3);
		e.setOwnerId(golem.getUUID());
		e.setTotalLife(140 + 20 * lv);
		e.setPos(golem.getX(), golem.getY(), golem.getZ());
		level.addFreshEntity(e);
		return true;
	}
}

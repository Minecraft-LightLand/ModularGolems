package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import com.Polarice3.Goety.common.entities.util.FireBlastTrap;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class FireBlastGoal extends BaseRangedAttackGoal {

	private int lv;

	public FireBlastGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 2 + lv, golem, lv);
		this.lv = lv;
	}

	@Override
	protected void performAttack(LivingEntity target) {
		var level = golem.level();
		FireBlastTrap e = new FireBlastTrap(level, target.getX(), target.getY() + (double) 0.25F, target.getZ());
		e.setOwner(golem);
		e.setAreaOfEffect(1.5f * (lv + 1));
		level.addFreshEntity(e);
	}
}

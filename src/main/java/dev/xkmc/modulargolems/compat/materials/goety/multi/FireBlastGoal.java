package dev.xkmc.modulargolems.compat.materials.goety.multi;

import com.Polarice3.Goety.common.entities.util.FireBlastTrap;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class FireBlastGoal extends MultiTargetRangedGoal implements IApostleGoal {

	public FireBlastGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 35, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 35;
	}

	@Override
	protected int getMaxTarget() {
		return lv;
	}

	@Override
	protected int cd() {
		return 10;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		var level = golem.level();
		FireBlastTrap e = new FireBlastTrap(level, target.getX(), target.getY() + (double) 0.25F, target.getZ());
		e.setOwner(golem);
		e.setAreaOfEffect(lv + 3);
		level.addFreshEntity(e);
	}
}

package dev.xkmc.modulargolems.compat.materials.goety.multi;

import com.Polarice3.Goety.common.entities.projectiles.HellCloud;
import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class HellCloudGoal extends MultiTargetRangedGoal implements IApostleGoal {

	public HellCloudGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 35, golem, lv);
	}

	protected int getMaxTarget() {
		return lv + 2;
	}

	@Override
	protected int searchRange() {
		return 35;
	}

	@Override
	protected int cd() {
		return 10;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		HellCloud hellCloud = new HellCloud(golem.level(), golem, target);
		hellCloud.setRadius(3.5f + lv * 0.5f);
		hellCloud.setLifeSpan(100 + 20 * lv);
		hellCloud.setExtraDamage(3 + lv);
		golem.level().addFreshEntity(hellCloud);
	}

}

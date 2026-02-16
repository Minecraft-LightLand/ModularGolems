package dev.xkmc.modulargolems.compat.materials.goety.modifier;

import com.Polarice3.Goety.common.entities.projectiles.HellCloud;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class HellCloudGoal extends BaseRangedAttackGoal implements IApostleGoal {

	public HellCloudGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 35, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		HellCloud hellCloud = new HellCloud(golem.level(), golem, target);
		hellCloud.setRadius(3.5f + lv * 0.5f);
		hellCloud.setLifeSpan(100 + 20 * lv);
		hellCloud.setExtraDamage(1 + (lv - 1) * 0.5f);
		golem.level().addFreshEntity(hellCloud);
		return true;
	}
}

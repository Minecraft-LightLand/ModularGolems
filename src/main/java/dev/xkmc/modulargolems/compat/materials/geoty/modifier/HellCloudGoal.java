package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import com.Polarice3.Goety.common.entities.projectiles.HellCloud;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class HellCloudGoal extends BaseRangedAttackGoal {

	private int lv;

	public HellCloudGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 2, 35, golem, lv);
		this.lv = lv;
	}

	@Override
	protected void performAttack(LivingEntity target) {
		HellCloud hellCloud = new HellCloud(golem.level(), golem, target);
		hellCloud.setRadius(3 + lv);
		hellCloud.setLifeSpan(100 + 40 * lv);
		golem.level().addFreshEntity(hellCloud);
	}
}

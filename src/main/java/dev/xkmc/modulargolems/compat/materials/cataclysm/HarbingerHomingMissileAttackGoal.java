package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HarbingerHomingMissileAttackGoal extends BaseRangedAttackGoal {

	public HarbingerHomingMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
	}

	@Override
	protected void performAttack(LivingEntity target) {
		if (golem.getType() == GolemTypes.ENTITY_GOLEM.get()) {
			if (golem.getMaterials().get(MetalGolemPartType.LEFT.ordinal())
					.modifiers().containsKey(CataCompatRegistry.HARBINGER_MISSILE.get()))
				addMissile(target, 0, -1);
			if (golem.getMaterials().get(MetalGolemPartType.RIGHT.ordinal())
					.modifiers().containsKey(CataCompatRegistry.HARBINGER_MISSILE.get()))
				addMissile(target, 0, -1);
		} else {
			addMissile(target, 1, 0);
		}
	}

	private void addMissile(LivingEntity target, float y, float r) {
		HarbingerHomingMissileModifier.addBeam(golem, target, golem.position().add(
				r * Math.cos(golem.yBodyRot * Mth.DEG_TO_RAD),
				y + golem.getEyeHeight(),
				r * Math.sin(golem.yBodyRot * Mth.DEG_TO_RAD)
		));
	}

}


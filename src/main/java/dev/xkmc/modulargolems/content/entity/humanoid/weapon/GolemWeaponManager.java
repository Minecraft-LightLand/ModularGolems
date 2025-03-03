package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.AbstractWeaponManager;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;

public class GolemWeaponManager extends AbstractWeaponManager<HumanoidGolemEntity> {

	public GolemWeaponManager(HumanoidGolemEntity golem) {
		super(GolemWeaponRegistry.GOLEM, golem, new GolemMeleeGoal(golem));
	}

}

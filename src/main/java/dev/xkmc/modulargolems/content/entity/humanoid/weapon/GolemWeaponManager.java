package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.AbstractWeaponManager;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;

public class GolemWeaponManager<T extends SweepGolemEntity<?, ?>> extends AbstractWeaponManager<T> {

	public GolemWeaponManager(GolemWeaponRegistry<T> reg, T golem, GolemMeleeGoal goal) {
		super(reg, golem, goal);
	}

}

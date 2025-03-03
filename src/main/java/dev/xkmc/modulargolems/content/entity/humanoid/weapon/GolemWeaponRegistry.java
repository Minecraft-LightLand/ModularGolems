package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.WeaponGoalRegistry;
import dev.xkmc.mob_weapon_api.example.goal.SmartBowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartCrossbowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartHoldRangedAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartInstantRangedAttackGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;

public class GolemWeaponRegistry extends WeaponGoalRegistry<HumanoidGolemEntity> {

	public static final GolemWeaponRegistry GOLEM = new GolemWeaponRegistry();

	public static void init() {
		GOLEM.register(ModularGolems.loc("throwable"),
				(golem, stack, hand) -> WeaponStatus.OFFENSIVE.of(GolemShooterHelper.isValidThrowableWeapon(golem, stack, hand)),
				(golem, melee) -> new GolemTridentAttackGoal(golem, 1, 20, 25, melee)
		);
		GOLEM.register(ModularGolems.loc("bow"),
				(golem, stack, hand) -> WeaponRegistry.BOW.getProperties(stack),
				(golem, melee) -> new SmartBowAttackGoal<>(golem, melee, 1.0D, 25)
		);
		GOLEM.register(ModularGolems.loc("crossbow"),
				(golem, stack, hand) -> WeaponRegistry.CROSSBOW.getProperties(stack),
				(golem, melee) -> new SmartCrossbowAttackGoal<>(golem, melee, 1.0D, 25)
		);
		GOLEM.register(ModularGolems.loc("instant"),
				(golem, stack, hand) -> WeaponRegistry.INSTANT.getProperties(stack),
				(golem, melee) -> new SmartInstantRangedAttackGoal<>(golem, melee, 1.0D)
		);
		GOLEM.register(ModularGolems.loc("hold"),
				(golem, stack, hand) -> WeaponRegistry.HOLD.getProperties(stack),
				(golem, melee) -> new SmartHoldRangedAttackGoal<>(golem, melee, 1.0D)
		);
	}

}

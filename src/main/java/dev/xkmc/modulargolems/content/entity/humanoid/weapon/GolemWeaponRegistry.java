package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.WeaponGoalRegistry;
import dev.xkmc.mob_weapon_api.example.goal.SmartBowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartCrossbowAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartHoldRangedAttackGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartInstantRangedAttackGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.ranged.GolemBowBehavior;
import dev.xkmc.modulargolems.content.item.ranged.GolemMechaBowBehavior;
import dev.xkmc.modulargolems.content.item.ranged.MetalGolemBowItem;
import dev.xkmc.modulargolems.init.ModularGolems;

public class GolemWeaponRegistry<T extends SweepGolemEntity<?, ?>> extends WeaponGoalRegistry<T> {

	public static final GolemWeaponRegistry<HumanoidGolemEntity> HUMANOID = new GolemWeaponRegistry<>();
	public static final GolemWeaponRegistry<MetalGolemEntity> LARGE = new GolemWeaponRegistry<>();

	private void initBasic() {
		register(ModularGolems.loc("bow"),
				(golem, stack, hand) -> WeaponRegistry.BOW.getProperties(stack),
				(golem, melee) -> new SmartBowAttackGoal<>(golem, melee, 1.0D, 25)
		);
		register(ModularGolems.loc("crossbow"),
				(golem, stack, hand) -> WeaponRegistry.CROSSBOW.getProperties(stack),
				(golem, melee) -> new SmartCrossbowAttackGoal<>(golem, melee, 1.0D, 25)
		);
		register(ModularGolems.loc("instant"),
				(golem, stack, hand) -> WeaponRegistry.INSTANT.getProperties(stack),
				(golem, melee) -> new SmartInstantRangedAttackGoal<>(golem, melee, 1.0D)
		);
		register(ModularGolems.loc("hold"),
				(golem, stack, hand) -> WeaponRegistry.HOLD.getProperties(stack),
				(golem, melee) -> new SmartHoldRangedAttackGoal<>(golem, melee, 1.0D)
		);
	}

	public static void init() {
		HUMANOID.initBasic();
		LARGE.initBasic();
		WeaponRegistry.BOW.register(ModularGolems.loc("golem_bow"),
				e -> WeaponStatus.OFFENSIVE.of(e.getItem() instanceof MetalGolemBowItem),
				(golem, stack) -> new GolemMechaBowBehavior(), 10);
	}

}

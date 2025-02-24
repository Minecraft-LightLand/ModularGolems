package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IWeaponGoalFactory<T extends Goal & IWeaponGoal> {

	T create(HumanoidGolemEntity golem, GolemMeleeGoal melee);

}

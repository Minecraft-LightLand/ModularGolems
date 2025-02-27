package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.item.ItemStack;

public interface IWeaponGoal {

	default boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		return true;
	}

	double range(HumanoidGolemEntity golem, ItemStack stack);

}

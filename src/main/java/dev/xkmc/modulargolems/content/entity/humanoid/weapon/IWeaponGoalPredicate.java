package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface IWeaponGoalPredicate {

	boolean isValid(HumanoidGolemEntity golem, ItemStack weapon, InteractionHand hand);

}

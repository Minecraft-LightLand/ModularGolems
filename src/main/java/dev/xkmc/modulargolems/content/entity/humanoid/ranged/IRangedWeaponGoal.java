package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IWeaponGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IRangedWeaponGoal extends IWeaponGoal {

	void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand);

}

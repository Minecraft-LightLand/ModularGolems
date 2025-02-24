package dev.xkmc.modulargolems.content.entity.humanoid.crossbow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICrossbowBehavior {

	void release(ItemStack stack);

	boolean tryCharge(HumanoidGolemEntity golem, ItemStack stack);

	void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand);

	boolean hasProjectile(HumanoidGolemEntity mob, ItemStack stack);

	boolean hasLoadedProjectile(ItemStack stack);

}

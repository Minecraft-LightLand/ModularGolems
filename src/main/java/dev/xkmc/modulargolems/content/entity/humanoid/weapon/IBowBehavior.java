package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IBowBehavior {

	boolean hasProjectile(HumanoidGolemEntity golem, ItemStack stack);

	void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand);

	int pullTime(HumanoidGolemEntity golem, ItemStack stack);

	float powerForTime(int i);

}

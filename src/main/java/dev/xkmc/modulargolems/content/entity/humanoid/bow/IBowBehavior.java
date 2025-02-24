package dev.xkmc.modulargolems.content.entity.humanoid.bow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IBowBehavior {

	void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand);

}

package dev.xkmc.modulargolems.content.entity.humanoid.crossbow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class DefaultCrossbowBehavior implements ICrossbowBehavior {

	public void setCharged(ItemStack stack, boolean charged) {
		CrossbowItem.setCharged(stack, charged);
	}

	public int getChargeDuration(ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack);
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		golem.performCrossbowAttack(golem, 3);
	}

}

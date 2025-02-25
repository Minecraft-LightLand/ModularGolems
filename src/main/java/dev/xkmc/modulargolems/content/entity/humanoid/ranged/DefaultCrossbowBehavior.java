package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.ICrossbowBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class DefaultCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(HumanoidGolemEntity golem, ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack);
	}

	@Override
	public boolean hasProjectile(HumanoidGolemEntity mob, ItemStack stack) {
		return !mob.getProjectile(stack).isEmpty();
	}

	@Override
	public boolean hasLoadedProjectile(ItemStack stack) {
		return !CrossbowItem.getChargedProjectiles(stack).isEmpty();
	}

	@Override
	public void release(ItemStack stack) {
		CrossbowItem.setCharged(stack, false);
	}

	@Override
	public boolean tryCharge(HumanoidGolemEntity golem, ItemStack stack) {
		if (CrossbowItem.tryLoadProjectiles(golem, stack)) {
			CrossbowItem.setCharged(stack, true);
			return true;
		}
		return false;
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		golem.performCrossbowAttack(golem, 3);
	}

}

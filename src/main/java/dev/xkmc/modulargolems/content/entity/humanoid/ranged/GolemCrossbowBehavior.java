package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.projectile_api.api.ICrossbowBehavior;
import dev.xkmc.projectile_api.api.ProjectileWeaponContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class GolemCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(LivingEntity golem, ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack);
	}

	@Override
	public boolean hasProjectile(LivingEntity mob, ItemStack stack) {
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
	public boolean tryCharge(LivingEntity golem, ItemStack stack) {
		if (CrossbowItem.tryLoadProjectiles(golem, stack)) {
			CrossbowItem.setCharged(stack, true);
			return true;
		}
		return false;
	}

	@Override
	public void performRangedAttack(LivingEntity golem, ProjectileWeaponContext strategy, float dist, ItemStack stack, InteractionHand hand) {
		if (golem instanceof CrossbowAttackMob mob) mob.performCrossbowAttack(golem, 3);
	}

}

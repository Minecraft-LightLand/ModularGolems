package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.CrossbowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ICrossbowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SimpleCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(LivingEntity user, ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack);
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser mob, ItemStack stack) {
		return !mob.getPreferredProjectile(stack).isEmpty();
	}

	@Override
	public List<ItemStack> getLoadedProjectile(ItemStack stack) {
		return CrossbowItem.getChargedProjectiles(stack);
	}

	@Override
	public void release(ItemStack stack) {
		CrossbowItem.setCharged(stack, false);
	}

	@Override
	public boolean tryCharge(LivingEntity user, ItemStack stack) {
		if (CrossbowItem.tryLoadProjectiles(user, stack)) {
			CrossbowItem.setCharged(stack, true);
			return true;
		}
		return false;
	}

	@Override
	public void performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand) {
		if (user.user() instanceof CrossbowAttackMob mob)
			mob.performCrossbowAttack(user.user(), user.getCrossbowVelocity(getLoadedProjectile(stack)));
	}

}

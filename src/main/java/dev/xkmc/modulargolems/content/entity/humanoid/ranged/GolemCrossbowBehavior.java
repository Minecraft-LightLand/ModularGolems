package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.projectile_api.api.CrossbowUseContext;
import dev.xkmc.projectile_api.example.GeneralCrossbowBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.ItemStack;

public class GolemCrossbowBehavior extends GeneralCrossbowBehavior {

	@Override
	public void performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand) {
		if (user.user() instanceof CrossbowAttackMob mob)
			mob.performCrossbowAttack(user.user(), user.getCrossbowVelocity(getLoadedProjectile(stack)));
		performShooting(user, hand, stack, user.getCrossbowVelocity(getLoadedProjectile(stack)), user.getInitialInaccuracy());
	}

}

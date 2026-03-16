package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.example.behavior.SimpleBowBehavior;
import net.minecraft.world.item.ItemStack;

public class GolemBowBehavior extends SimpleBowBehavior {

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		if (stack.getItem() instanceof MetalGolemBowItem bow && !bow.isFor(user.user().getType()))
			return false;
		return super.hasProjectile(user, stack);
	}

	@Override
	public int getStandardPullTime(BowUseContext ctx, ItemStack stack) {
		return stack.getItem() instanceof MetalGolemBowItem bow ? bow.getPullTime(ctx.user()) : 20;
	}

	@Override
	public float getPowerForTime(BowUseContext ctx, ItemStack stack, int time) {
		return stack.getItem() instanceof MetalGolemBowItem bow ? bow.getPower(ctx.user(), time) : 20;
	}

}

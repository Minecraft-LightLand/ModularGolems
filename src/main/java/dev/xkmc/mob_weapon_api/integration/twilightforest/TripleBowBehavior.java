package dev.xkmc.mob_weapon_api.integration.twilightforest;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.example.SimpleBowBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public class TripleBowBehavior extends SimpleBowBehavior {

	@Override
	public void shootArrow(BowUseContext ctx, float power, ItemStack stack, InteractionHand hand) {
		var user = ctx.user();
		ItemStack ammo = ctx.getPreferredProjectile(stack);
		boolean infinite = ctx.hasInfiniteArrow(stack, ammo);
		if (!infinite && ammo.isEmpty()) return;
		float vel = power * ctx.getInitialVelocityFactor();
		var pos = user.getEyePosition().add(0, -0.1, 0);
		var aim = ctx.aim(pos, vel, 0.05f, ctx.getInitialInaccuracy());
		for (int j = -1; j < 2; j++) {
			AbstractArrow e = ctx.createArrow(ammo, vel);
			aim.shoot(e, 0);
			e.setDeltaMovement(e.getDeltaMovement().add(0.0, 0.15 * (double) j, 0.0));
			if (j != 0) {
				e.setPos(e.getX(), e.getY() + 0.025, e.getZ());
				e.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			} else if (infinite) {
				e.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			}
			e.setCritArrow(true);
			user.level().addFreshEntity(e);
		}
		if (!ctx.bypassAllConsumption()) {
			stack.hurtAndBreak(1, user, (e) -> e.broadcastBreakEvent(user.getUsedItemHand()));
		}
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
				1.0F / (user.level().getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
		if (!infinite) {
			ammo.shrink(1);
		}
	}

}

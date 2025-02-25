package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileProperties;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class SimpleBowBehavior implements IBowBehavior {

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		return BowItem.getPowerForTime(pullTime);
	}

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		return 20;
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		return !user.getPreferredProjectile(stack).isEmpty();
	}

	public void shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof BowItem bow)) return;
		ItemStack arrowStack = user.getPreferredProjectile(stack);
		if (arrowStack.isEmpty()) return;
		AbstractArrow arrowEntity = bow.customArrow(user.createArrow(arrowStack, power));
		boolean infinite = user.bypassAllConsumption() || user.hasInfiniteArrow(stack, arrowStack);
		var prop = new ProjectileProperties(
				power * user.getInitialVelocityFactor(),
				arrowEntity.isNoGravity() ? 0 : 0.05f,
				user.getInitialInaccuracy(),
				infinite
		);
		if (prop.infinite()) {
			arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		} else {
			arrowStack.shrink(1);
			arrowEntity.pickup = AbstractArrow.Pickup.ALLOWED;
		}
		user.aim(arrowEntity.position(), prop.velocity(), prop.gravity(), prop.inaccuracy()).shoot(arrowEntity, 0);
		user.user().playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (user.user().getRandom().nextFloat() * 0.4F + 0.8F));
		user.user().level().addFreshEntity(arrowEntity);
		if (!user.bypassAllConsumption()) {
			stack.hurtAndBreak(1, user.user(), e -> e.broadcastBreakEvent(hand));
		}
	}

}

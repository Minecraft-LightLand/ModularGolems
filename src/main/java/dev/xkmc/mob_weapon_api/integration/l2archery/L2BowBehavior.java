package dev.xkmc.mob_weapon_api.integration.l2archery;

import dev.xkmc.l2archery.content.controller.BowFeatureController;
import dev.xkmc.l2archery.content.entity.GenericArrowEntity;
import dev.xkmc.l2archery.content.feature.bow.DoubleChargeFeature;
import dev.xkmc.l2archery.content.item.GenericBowItem;
import dev.xkmc.l2library.util.code.GenericItemStack;
import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.example.SimpleBowBehavior;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class L2BowBehavior extends SimpleBowBehavior {

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		if (!(stack.getItem() instanceof GenericBowItem bow)) return 20;
		return bow.config.pull_time();
	}

	@Override
	public int getPreferredPullTime(BowUseContext user, ItemStack stack, double distToTarget) {
		int ans = super.getPreferredPullTime(user, stack, distToTarget);
		if (distToTarget > 10 && stack.getItem() instanceof GenericBowItem bow) {
			var charge = bow.getFeatures(stack).all().stream()
					.filter(e -> e instanceof DoubleChargeFeature).findFirst();
			if (charge.isPresent()) {
				return ans * 2;
			}
		}
		return ans;
	}

	@Override
	public void startUsingBow(ProjectileWeaponUser user, ItemStack stack) {
		if (!(stack.getItem() instanceof GenericBowItem bow)) return;
		BowFeatureController.startUsing(user.user(), new GenericItemStack<>(bow, stack));
	}

	public void shootArrow(BowUseContext ctx, float power, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof GenericBowItem bow)) return;
		ItemStack arrowStack = ctx.getPreferredProjectile(stack, bow.getSupportedHeldProjectiles(), bow.getAllSupportedProjectiles());
		if (arrowStack.isEmpty()) return;
		AbstractArrow proj = bow.customArrow(ctx.createArrow(arrowStack, power));
		boolean infinite = ShootUtils.arrowIsInfinite(arrowStack, stack);
		float speed = ctx.getInitialVelocityFactor();
		float gravity = 0.05f;
		Optional<AbstractArrow> opt = bow.releaseUsingAndShootArrow(stack, ctx.user().level(), ctx.user(), ctx.user().getUseItemRemainingTicks());
		if (opt.isPresent()) {
			proj = opt.get();
			if (proj instanceof GenericArrowEntity entity) {
				speed = entity.data.bow().getConfig().speed();
				gravity = entity.features.flight().gravity;
			}
		}
		if (infinite) {
			proj.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		} else {
			proj.pickup = AbstractArrow.Pickup.ALLOWED;
		}
		ctx.aim(proj.position(), speed, gravity, ctx.getInitialInaccuracy()).shoot(proj, 0);
		ctx.user().playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (ctx.user().getRandom().nextFloat() * 0.4F + 0.8F));
		ctx.user().level().addFreshEntity(proj);
		stack.hurtAndBreak(1, ctx.user(), e -> e.broadcastBreakEvent(hand));
	}

}

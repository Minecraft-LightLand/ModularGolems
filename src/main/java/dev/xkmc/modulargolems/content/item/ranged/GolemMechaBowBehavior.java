package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileProperties;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GolemMechaBowBehavior extends GolemBowBehavior {

	public int shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		Item arrow = stack.getItem();
		if (!(arrow instanceof BowItem bow)) return 20;
		if (!(user.user() instanceof MetalGolemEntity golem)) return 20;
		var target = golem.getTarget();
		if (target == null) return 20;
		if (!shoot(user, bow, power, stack, hand, target)) return 20;
		user.user().playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (user.user().getRandom().nextFloat() * 0.4F + 0.8F));
		if (!(arrow instanceof IMultiShotBow shot)) return 20;
		var list = getPreferableTargets(golem, 35, Math.PI / 8);
		int n = Math.min(list.size(), shot.getMaxShoot(user.user(), stack) - 1);
		for (int i = 0; i < n; i++) {
			if (!shoot(user, bow, power, stack, hand, list.get(i))) return 10;
		}
		return 10;
	}

	private List<LivingEntity> getPreferableTargets(MetalGolemEntity golem, int range, double span) {
		var list = golem.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(range),
				e -> {
					if (!golem.canAttack(e) || !golem.hasLineOfSight(e)) return false;
					if (golem.getTarget() == e) return false;
					var diff = e.position().add(0, e.getBbHeight() / 2, 0)
							.subtract(golem.getEyePosition()).normalize();
					return Math.acos(golem.getViewVector(1).normalize().dot(diff)) < span;
				});
		list.sort(Comparator.comparing(ke -> Optional.ofNullable(
				TargetManager.predicateTarget(golem, ke)
		).map(Enum::ordinal).orElse(100)));
		return list;
	}

	private boolean shoot(BowUseContext user, BowItem bow, float power, ItemStack stack, InteractionHand hand, LivingEntity target) {
		ItemStack arrowStack = user.getPreferredProjectile(stack);
		if (arrowStack.isEmpty()) return false;
		AbstractArrow proj = bow.customArrow(user.createArrow(arrowStack, power));
		boolean infinite = user.bypassAllConsumption() || user.hasInfiniteArrow(stack, arrowStack);
		ProjectileProperties prop = new ProjectileProperties(
				power * user.getInitialVelocityFactor(),
				proj.isNoGravity() ? 0.0F : 0.05F,
				user.getInitialInaccuracy(), infinite);
		if (prop.infinite()) {
			proj.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		} else {
			arrowStack.shrink(1);
			proj.pickup = AbstractArrow.Pickup.ALLOWED;
		}
		ShootUtils.getShootVector(target, proj.position(), prop.velocity(), prop.gravity(), prop.inaccuracy()).shoot(proj, 0f);
		user.user().level().addFreshEntity(proj);
		return true;
	}

}

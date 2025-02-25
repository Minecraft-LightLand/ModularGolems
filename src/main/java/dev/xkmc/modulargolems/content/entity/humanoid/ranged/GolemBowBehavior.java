package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemBowAttackEvent;
import dev.xkmc.projectile_api.api.IBowBehavior;
import dev.xkmc.projectile_api.api.ProjectileWeaponContext;
import dev.xkmc.projectile_api.api.ProjectileWeaponUser;
import dev.xkmc.projectile_api.util.ShootUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class GolemBowBehavior implements IBowBehavior {

	@Override
	public float getPowerForTime(ProjectileWeaponContext user, ItemStack stack, int pullTime) {
		return 1;
	}

	@Override
	public int getStandardPullTime(ProjectileWeaponContext user, ItemStack stack) {
		return 20;
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		return !user.getPreferredProjectile(stack).isEmpty();
	}

	public void shootArrow(ProjectileWeaponContext user, float power, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof BowItem bow) || !(user.user() instanceof HumanoidGolemEntity g)) return;
		ItemStack arrowStack = user.getPreferredProjectile(stack, bow.getSupportedHeldProjectiles(), bow.getAllSupportedProjectiles());
		if (arrowStack.isEmpty()) return;
		AbstractArrow arrowEntity = bow.customArrow(user.createArrow(arrowStack, power));
		boolean infinite = ShootUtils.arrowIsInfinite(arrowStack, stack);
		GolemBowAttackEvent event = new GolemBowAttackEvent(g, stack, hand, arrowEntity, infinite);
		MinecraftForge.EVENT_BUS.post(event);
		arrowEntity = event.getArrow();
		if (event.isNoPickup()) {
			arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		} else {
			arrowEntity.pickup = AbstractArrow.Pickup.ALLOWED;
		}
		if (!event.isNoConsume()) {
			arrowStack.shrink(1);
		}
		user.aim(arrowEntity.position(), (float) event.speed(), (float) event.gravity(), 0).shoot(arrowEntity, 0);
		user.user().playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (user.user().getRandom().nextFloat() * 0.4F + 0.8F));
		user.user().level().addFreshEntity(arrowEntity);
		stack.hurtAndBreak(1, user.user(), e -> e.broadcastBreakEvent(hand));
	}

}

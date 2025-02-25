package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemBowAttackEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class DefaultBowBehavior implements IBowBehavior {

	@Override
	public float powerForTime(int i) {
		return 1;
	}

	@Override
	public int pullTime(HumanoidGolemEntity golem, ItemStack stack) {
		return 20;
	}

	@Override
	public boolean hasProjectile(HumanoidGolemEntity golem, ItemStack stack) {
		return !golem.getProjectile(stack).isEmpty();
	}

	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof BowItem bow)) return;
		ItemStack arrowStack = golem.getProjectile(stack);
		if (arrowStack.isEmpty()) return;
		AbstractArrow arrowEntity = bow.customArrow(golem.getArrow(arrowStack, dist));
		boolean infinite = GolemShooterHelper.arrowIsInfinite(arrowStack, stack);
		GolemBowAttackEvent event = new GolemBowAttackEvent(golem, stack, hand, arrowEntity, infinite);
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
		GolemShooterHelper.shootAimHelper(target, arrowEntity, (float) event.speed(), event.gravity());
		golem.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (golem.getRandom().nextFloat() * 0.4F + 0.8F));
		arrowEntity.getPersistentData().putInt("DespawnFactor", 20);
		golem.level().addFreshEntity(arrowEntity);
		stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
	}

}

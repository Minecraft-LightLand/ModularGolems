package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;

public class GolemShooterHelper {

	private static final HashSet<Class<?>> BLACKLIST = new HashSet<>();

	public static GolemThrowableEvent isValidThrowableWeapon(HumanoidGolemEntity golem, ItemStack stack, InteractionHand hand) {
		if (stack.getEnchantmentLevel(Enchantments.LOYALTY) > 0) {
			stack = stack.copy();
			var map = stack.getAllEnchantments();
			map.remove(Enchantments.LOYALTY);
			EnchantmentHelper.setEnchantments(map, stack);
		}//TODO find a cleaner approach
		GolemThrowableEvent event = new GolemThrowableEvent(golem, stack, hand);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	@SuppressWarnings("ConstantConditions")
	public static boolean arrowIsInfinite(ItemStack arrow, ItemStack bow) {
		if (!(arrow.getItem() instanceof ArrowItem item)) {
			return false;
		}
		if (BLACKLIST.contains(item.getClass())) {
			return false;
		}
		try {
			return item.isInfinite(arrow, bow, null);
		} catch (NullPointerException npe) {
			BLACKLIST.add(item.getClass());
		}
		return false;
	}

	public static void shootAimHelper(LivingEntity target, Projectile arrow) {
		shootAimHelper(target, arrow, 3, 0.05);
	}

	public static void shootAimHelper(LivingEntity target, Projectile arrow, double v, double g) {
		double dx = target.getX() - arrow.getX();
		double dy = target.getY(0.5) - arrow.getY();
		double dz = target.getZ() - arrow.getZ();

		double c = dx * dx + dz * dz + dy * dy;
		boolean completed = false;
		if (target instanceof Slime) {
			var clip = target.level.clip(new ClipContext(target.position(), target.position().add(0, -3, 0),
					ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, target));
			if (clip.getType() == HitResult.Type.BLOCK) {
				dy += clip.getLocation().y() - target.getY();
				completed = true;
			}
		}
		if (!completed) {
			double rt = Math.sqrt(c) / v;
			var tv = target.getDeltaMovement();
			dx += tv.x * rt;
			dy += tv.y * rt;
			dz += tv.z * rt;
		}

		c = dx * dx + dz * dz + dy * dy;

		if (g > 0 && c > v * v * 4) {
			double a = g * g / 4;
			double b = dy * g - v * v;

			double delta = b * b - 4 * a * c;
			if (delta > 0) {
				double t21 = (-b + Math.sqrt(delta)) / (2 * a);
				double t22 = (-b - Math.sqrt(delta)) / (2 * a);
				if (t21 > 0 || t22 > 0) {
					double t2 = t21 > 0 ? t22 > 0 ? Math.min(t21, t22) : t21 : t22;
					arrow.shoot(dx, dy + g * t2 / 2, dz, (float) v, 0);
					return;
				}
			}
		}
		arrow.shoot(dx, dy, dz, (float) v, 0);
	}

}

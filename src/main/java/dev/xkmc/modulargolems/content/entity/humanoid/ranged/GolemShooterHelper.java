package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;

public class GolemShooterHelper {

	private static final HashSet<Class<?>> BLACKLIST = new HashSet<>();

	public static GolemThrowableEvent isValidThrowableWeapon(HumanoidGolemEntity golem, ItemStack stack) {
		GolemThrowableEvent event = new GolemThrowableEvent(golem, stack);
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
		double d0 = target.getX() - arrow.getX();
		double d1 = target.getY(0.5) - arrow.getY();
		double d2 = target.getZ() - arrow.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		arrow.shoot(d0, d1 + d3 * 0.05F, d2, 3f, 0);
	}

}

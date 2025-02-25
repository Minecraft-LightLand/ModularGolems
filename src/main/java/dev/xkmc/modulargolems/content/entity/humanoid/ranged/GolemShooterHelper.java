package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import dev.xkmc.projectile_api.util.ShootUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public class GolemShooterHelper {


	public static boolean isValidThrowableWeapon(HumanoidGolemEntity golem, ItemStack stack, @Nullable InteractionHand hand) {
		if (hand == null) return false;
		return throwWeapon(golem, stack, hand).isThrowable();
	}

	public static GolemThrowableEvent throwWeapon(HumanoidGolemEntity golem, ItemStack stack, InteractionHand hand) {
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

	public static void shootAimHelper(LivingEntity target, Projectile arrow) {
		ShootUtils.shootAimHelper(target, arrow, 3, 0.05f);
	}

}

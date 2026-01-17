package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class GolemShooterHelper {

	@Deprecated
	public static boolean isValidThrowableWeapon(LivingEntity user, ItemStack stack, @Nullable InteractionHand hand) {
		if (hand == null) return false;
		var ans = throwWeapon(user, stack, hand);
		return ans != null && ans.isThrowable();
	}

	@Deprecated
	public static @Nullable GolemThrowableEvent throwWeapon(LivingEntity user, ItemStack stack, InteractionHand hand) {
		if (!(user instanceof HumanoidGolemEntity golem)) return null;
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

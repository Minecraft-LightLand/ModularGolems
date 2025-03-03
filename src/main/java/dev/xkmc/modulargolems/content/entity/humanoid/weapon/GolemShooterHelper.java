package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class GolemShooterHelper {

	public static boolean isValidThrowableWeapon(LivingEntity user, ItemStack stack, @Nullable InteractionHand hand) {
		if (hand == null) return false;
		var ans = throwWeapon(user, stack, hand);
		return ans != null && ans.isThrowable();
	}

	public static @Nullable GolemThrowableEvent throwWeapon(LivingEntity user, ItemStack stack, InteractionHand hand) {
		if (!(user instanceof HumanoidGolemEntity golem)) return null;
		var reg = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
		if (reg != null) {
			var loyalty = reg.get(Enchantments.LOYALTY);
			if (loyalty.isPresent()) {
				if (stack.getEnchantmentLevel(loyalty.get()) > 0) {
					stack = stack.copy();
					ItemEnchantments map = stack.getAllEnchantments(reg);
					var mutable = new ItemEnchantments.Mutable(map);
					mutable.set(loyalty.get(), 0);
					EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
				}
			}
		}

		GolemThrowableEvent event = new GolemThrowableEvent(golem, stack, hand);
		NeoForge.EVENT_BUS.post(event);
		return event;
	}

	public static void shootAimHelper(LivingEntity target, Projectile arrow) {
		ShootUtils.shootAimHelper(target, arrow, 3, 0.05f);
	}

}

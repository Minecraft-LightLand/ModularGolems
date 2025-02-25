package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public record CompanionUser(Mob user, LivingEntity target) implements BowUseContext {

	@Override
	public ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general) {
		ItemStack ans = user.getProjectile(weapon);
		if (!special.test(ans)) return ItemStack.EMPTY;
		return ans;
	}

	@Override
	public boolean bypassAllConsumption() {
		return false;
	}

	@Override
	public boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo) {
		if (ShootUtils.arrowIsInfinite(ammo, weapon)) return true;
		if (ammo.getItem().getClass() != ArrowItem.class) return false;
		return weapon.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
	}

	@Override
	public float getInitialVelocityFactor() {
		return 3;
	}

	@Override
	public float getInitialInaccuracy() {
		return 0;
	}

	@Override
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		return ShootUtils.getShootVector(target, arrowOrigin, velocity, gravity, inaccuracy);
	}

}

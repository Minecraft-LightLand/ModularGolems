package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record GolemUser(
		LivingEntity user, @Nullable LivingEntity target
) implements ISmartUser {

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
		return ShootUtils.arrowIsInfinite(ammo, weapon);
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
	public Vec3 viewVector() {
		assert target != null;
		return ShootUtils.getShootVector(target, user.getEyePosition(), 1, 0, 0).vec3();
	}

	@Override
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		assert target != null;
		return ShootUtils.getShootVector(target, arrowOrigin, velocity, gravity, inaccuracy);
	}

}

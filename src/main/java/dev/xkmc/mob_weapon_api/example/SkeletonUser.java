package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public record SkeletonUser(AbstractSkeleton mob, LivingEntity target) implements BowUseContext {

	@Override
	public LivingEntity user() {
		return mob;
	}

	@Override
	public ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general) {
		ItemStack ans = mob.getProjectile(weapon);
		if (!special.test(ans)) return ItemStack.EMPTY;
		return ans;
	}

	@Override
	public boolean bypassAllConsumption() {
		return true;
	}

	@Override
	public boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo) {
		return true;
	}

	@Override
	public float getInitialVelocityFactor() {
		return 1.6f;
	}

	@Override
	public float getInitialInaccuracy() {
		return 14 - mob.level().getDifficulty().getId() * 4;
	}

	@Override
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		double dx = target.getX() - mob.getX();
		double dz = target.getZ() - mob.getZ();
		double dy = target.getY(1 / 3d) - arrowOrigin.y() + Math.sqrt(dx * dx + dz * dz) * 0.2;
		return (e, a) -> e.shoot(dx, dy, dz, velocity, inaccuracy);
	}

}

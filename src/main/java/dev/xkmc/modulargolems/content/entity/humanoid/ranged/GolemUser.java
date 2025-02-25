package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.projectile_api.api.projectile.BowUseContext;
import dev.xkmc.projectile_api.api.projectile.CrossbowUseContext;
import dev.xkmc.projectile_api.util.ShootUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record GolemUser(HumanoidGolemEntity user,
						@Nullable LivingEntity target) implements BowUseContext, CrossbowUseContext {

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
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		assert target != null;
		return ShootUtils.getShootVector(target, arrowOrigin, velocity, gravity, inaccuracy);
	}

}

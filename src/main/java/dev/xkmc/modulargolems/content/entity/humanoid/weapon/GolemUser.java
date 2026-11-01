package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record GolemUser(
		AbstractGolemEntity<?, ?> user, @Nullable LivingEntity target
) implements ISmartUser {

	@Override
	public ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general) {
		ItemStack ans = user.getProjectile(weapon);
		if (!special.test(ans)) return ItemStack.EMPTY;
		return ans;
	}

	/**
	 * 弩走原版 {@code CrossbowItem.performShooting}，那里会给箭 {@code setCritArrow(true)}，
	 * 所以弩射出的箭带原版暴击轨迹粒子。弓走 {@code ProjectileUtil.getMobArrow}，不会设这个标记，
	 * 于是弓射出的箭没有任何轨迹粒子。这里统一补上，让弓与弩表现一致。
	 */
	@Override
	public AbstractArrow createArrow(ItemStack ammo, float power) {
		AbstractArrow arrow = ProjectileUtil.getMobArrow(user, ammo, power);
		arrow.setCritArrow(true);
		return arrow;
	}

	@Override
	public boolean bypassAllConsumption() {
		return user.isHostile();
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

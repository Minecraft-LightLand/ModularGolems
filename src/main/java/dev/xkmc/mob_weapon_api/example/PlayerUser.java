package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public record PlayerUser(Player player) implements BowUseContext {

	@Override
	public LivingEntity user() {
		return player;
	}

	@Override
	public ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general) {
		ItemStack ans = player.getProjectile(weapon);
		if (!special.test(ans)) return ItemStack.EMPTY;
		return ans;
	}

	@Override
	public boolean bypassAllConsumption() {
		return player.getAbilities().instabuild;
	}

	@Override
	public boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo) {
		return ammo.getItem() instanceof ArrowItem arrow && arrow.isInfinite(ammo, weapon, player);
	}

	@Override
	public float getInitialVelocityFactor() {
		return 3;
	}

	@Override
	public float getInitialInaccuracy() {
		return 1;
	}

	@Override
	public AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy) {
		return (e, a) -> e.shootFromRotation(player, player.getXRot(), player.getYRot() + a, 0, velocity, inaccuracy);
	}

}

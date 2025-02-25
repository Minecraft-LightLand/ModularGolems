package dev.xkmc.mob_weapon_api.example;

import dev.xkmc.mob_weapon_api.api.projectile.CrossbowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUseContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeneralCrossbowBehavior extends SimpleCrossbowBehavior {

	@Override
	public void performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand) {
		performShooting(user, hand, stack, user.getCrossbowVelocity(getLoadedProjectile(stack)), user.getInitialInaccuracy());
	}

	protected void performShooting(CrossbowUseContext user, InteractionHand hand, ItemStack stack, float velocity, float inaccuracy) {
		List<ItemStack> list = getLoadedProjectile(stack);
		float[] rand = getShotPitches(user.user().getRandom());
		ProjectileWeaponUseContext.AimResult aim = null;
		for (int i = 0; i < list.size(); ++i) {
			ItemStack ammo = list.get(i);
			boolean creative = user.hasInfiniteArrow(stack, ammo);
			if (!ammo.isEmpty()) {
				float angle = i == 0 ? 0 : i == 1 ? -10 : 10;
				aim = shootProjectile(user, aim, hand, stack, ammo, rand[i], creative, velocity, inaccuracy, angle);
			}
		}
		clearChargedProjectiles(stack);
	}

	private static ProjectileWeaponUseContext.AimResult shootProjectile(
			CrossbowUseContext ctx, @Nullable ProjectileWeaponUseContext.AimResult aim, InteractionHand hand, ItemStack stack, ItemStack ammo,
			float rand, boolean infinite, float velocity, float inaccuracy, float angle) {
		var user = ctx.user();
		var level = user.level();
		boolean rocket = ammo.is(Items.FIREWORK_ROCKET);
		Projectile projectile;
		if (rocket) {
			projectile = new FireworkRocketEntity(level, ammo, user, user.getX(), user.getEyeY() - (double) 0.15F, user.getZ(), true);
		} else {
			projectile = getArrow(ctx, stack, ammo, velocity);
			if (infinite || angle != 0.0F) {
				((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			}
		}
		if (aim == null) {
			aim = ctx.aim(projectile.position(), velocity, rocket ? 0 : 0.05f, inaccuracy);
		}
		aim.shoot(projectile, angle);
		projectile.setDeltaMovement(projectile.getDeltaMovement().normalize().scale(velocity));
		if (!ctx.bypassAllConsumption()) {
			stack.hurtAndBreak(rocket ? 3 : 1, user, (e) -> e.broadcastBreakEvent(hand));
		}
		level.addFreshEntity(projectile);
		level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, rand);
		return aim;
	}

	private static AbstractArrow getArrow(CrossbowUseContext user, ItemStack bow, ItemStack ammo, float velocity) {
		var ans = user.createArrow(ammo, velocity);
		ans.setCritArrow(true);
		ans.setSoundEvent(SoundEvents.CROSSBOW_HIT);
		ans.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, bow);
		if (i > 0) {
			ans.setPierceLevel((byte) i);
		}
		return ans;
	}

	private static float[] getShotPitches(RandomSource p_220024_) {
		boolean flag = p_220024_.nextBoolean();
		return new float[]{1.0F, getRandomShotPitch(flag, p_220024_), getRandomShotPitch(!flag, p_220024_)};
	}

	private static float getRandomShotPitch(boolean p_220026_, RandomSource p_220027_) {
		float f = p_220026_ ? 0.63F : 0.43F;
		return 1.0F / (p_220027_.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void clearChargedProjectiles(ItemStack stack) {
		CompoundTag compoundtag = stack.getTag();
		if (compoundtag != null) {
			ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
			listtag.clear();
			compoundtag.put("ChargedProjectiles", listtag);
		}

	}

}

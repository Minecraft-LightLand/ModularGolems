package dev.xkmc.projectile_api.integration.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Arrow_Entity;
import dev.xkmc.projectile_api.api.BowUseContext;
import dev.xkmc.projectile_api.api.IBowBehavior;
import dev.xkmc.projectile_api.api.ProjectileWeaponUser;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CursedBowBehavior implements IBowBehavior {

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		return 20;
	}

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		return 1;
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		return !user.getPreferredProjectile(stack).isEmpty();
	}

	@Override
	public void shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand) {
		var player = user.user();
		var level = player.level();
		ItemStack ammo = player.getProjectile(stack);
		boolean infinite = user.hasInfiniteArrow(stack, ammo);
		LivingEntity target = player instanceof Mob mob ? mob.getTarget() : null;
		if (target == null) return;
		boolean homing = ammo.is(Items.ARROW);
		float angle = ammo.is(Items.ARROW) ? 6F : 1.5F;
		float vel = power * user.getInitialVelocityFactor();
		var pos = player.getEyePosition().add(0, -0.1, 0);
		var aim = user.aim(pos, vel, 0.05f, user.getInitialInaccuracy());
		for (int j = -1; j <= 1; ++j) {
			var arrow = user.createArrow(ammo, vel);
			if (homing) {
				Phantom_Arrow_Entity hommingArrowEntity;
				hommingArrowEntity = new Phantom_Arrow_Entity(level, player, target);
				hommingArrowEntity.setBaseDamage(CMConfig.PlayerPhantomArrowbasedamage);
				arrow = hommingArrowEntity;
			}
			if (j != 0 || infinite) {
				arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			} else {
				arrow.pickup = AbstractArrow.Pickup.ALLOWED;
			}
			aim.shoot(arrow, j * angle);
			arrow.setCritArrow(true);
			level.addFreshEntity(arrow);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
				1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
		if (!infinite)
			ammo.shrink(1);
	}

}

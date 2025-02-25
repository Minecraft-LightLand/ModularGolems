package dev.xkmc.mob_weapon_api.integration.cataclysm;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class WrathBowBehavior implements IBowBehavior {

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
		return true;
	}

	@Override
	public void shootArrow(BowUseContext ctx, float power, ItemStack stack, InteractionHand hand) {
		var user = ctx.user();
		var level = user.level();
		LivingEntity target = user instanceof Mob mob ? mob.getTarget() : null;
		if (target == null) return;
		Vec3 diff = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		for (int j = -1; j <= 1; ++j) {
			Vec3 rot = diff.yRot(j * 15 * Mth.DEG_TO_RAD);
			Vec3 pos = user.position().add(rot);
			Entity e = CataclysmProxy.createGhostStorm(user, pos, rot, target);
			if (e != null)
				level.addFreshEntity(e);
		}
		level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
				1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
	}

}

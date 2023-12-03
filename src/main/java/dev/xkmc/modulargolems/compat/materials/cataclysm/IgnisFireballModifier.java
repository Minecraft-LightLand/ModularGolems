package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class IgnisFireballModifier extends GolemModifier {

	private static final int[] TIME = {45, 61, 77, 93, 109}, ANGLE = {-5, -2, 0, 2, 5};

	public IgnisFireballModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new IgnisFireballAttackGoal(entity, lv));
	}

	public static void addFireball(LivingEntity user, int lv) {
		user.level().playLocalSound(user.getX(), user.getY(), user.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
				user.getSoundSource(), 5.0F, 1.4F + user.getRandom().nextFloat() * 0.1F, false);
		int index = user.getRandom().nextInt(5);
		lv = Mth.clamp(lv, 0, 2);
		for (int i = 2 - lv; i < 3 + lv; i++) {
			shootFireball(user, new Vec3(ANGLE[i], 3.0D, 0.0D), TIME[i], index == i);
		}
	}

	private static void shootFireball(LivingEntity user, Vec3 shotAt, int timer, boolean abyss) {
		shotAt = shotAt.yRot(-user.getYRot() * 0.017453292F);
		Projectile shot;
		if (abyss) {
			var bullet = new Ignis_Abyss_Fireball_Entity(user.level(), user);
			bullet.setUp(timer);
			shot = bullet;
		} else {
			var bullet = new Ignis_Fireball_Entity(user.level(), user);
			bullet.setUp(timer);
			if (user.getHealth() < user.getMaxHealth() / 2) {
				bullet.setSoul(true);
			}
			shot = bullet;
		}
		float rot = user.yBodyRot * 0.017453292F;
		double width = (user.getBbWidth() + 1.0F) * 0.15D;
		shot.setPos(user.getX() - width * Mth.sin(rot),
				user.getY() + 1.0D,
				user.getZ() + width * Mth.cos(rot));
		double d0 = shotAt.x;
		double d1 = shotAt.y;
		double d2 = shotAt.z;
		float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
		shot.shoot(d0, d1 + f, d2, 0.25F, 3.0F);
		user.level().addFreshEntity(shot);
	}

}

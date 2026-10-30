package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class GumShootAttackGoal extends BaseRangedAttackGoal {

	private static final int DEFAULT_GUMBALLS = 2;
	private static final float NORMAL_SPEED = 1.5F;
	private static final float EXPLOSIVE_SPEED = 0.5F;
	private static final float RICOCHET_CHANCE = 0.25F;
	private static final float SPLIT_CHANCE = 0.25F;
	private static final float EXPLOSIVE_CHANCE = 0.15F;
	private static final float BOUNCY_CHANCE = 0.3F;
	private static final float SPREAD_DEGREES = 10F;

	public GumShootAttackGoal(int waitTime, int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		super(waitTime, near, far, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		RandomSource random = golem.getRandom();
		boolean explosive = random.nextFloat() < EXPLOSIVE_CHANCE;
		int count = explosive ? 1 : DEFAULT_GUMBALLS;
		for (int i = 0; i < count; i++) {
			var proj = new GumballEntity(target.level(), golem);
			proj.setPos(golem.getEyePosition());
			proj.setDamage((float) (MGConfig.COMMON.candyDamage.get() * lv));
			applyShotGumEnchants(proj, random, explosive);
			var aim = ShootUtils.getShootVector(target, proj.position(), explosive ? EXPLOSIVE_SPEED : NORMAL_SPEED, 0.08f, 0);
			if (i > 0) aim = aim.rotate((random.nextFloat() * 2F - 1F) * SPREAD_DEGREES);
			aim.apply(proj);
			golem.level().addFreshEntity(proj);
		}
		return true;
	}

	private static void applyShotGumEnchants(GumballEntity proj, RandomSource random, boolean explosive) {
		boolean applied = explosive;
		if (explosive) proj.setExplosive(true);
		float roll = random.nextFloat();
		if (roll < RICOCHET_CHANCE) {
			proj.setTargetsOnBounce(true);
			applied = true;
		} else if (roll < RICOCHET_CHANCE + SPLIT_CHANCE) {
			proj.setSplitsOnHit(true);
			applied = true;
		}
		if (random.nextFloat() < BOUNCY_CHANCE) {
			proj.setMaximumBounces(4 + (1 + random.nextInt(3)) * 2);
			applied = true;
		}
		if (!applied) {
			switch (random.nextInt(3)) {
				case 0 -> proj.setTargetsOnBounce(true);
				case 1 -> proj.setSplitsOnHit(true);
				default -> proj.setMaximumBounces(4 + (1 + random.nextInt(3)) * 2);
			}
		}
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}

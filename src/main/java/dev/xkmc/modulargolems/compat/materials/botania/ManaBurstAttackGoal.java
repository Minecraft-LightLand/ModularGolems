package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.common.entity.ManaBurstEntity;

public class ManaBurstAttackGoal extends BaseRangedAttackGoal {

	public ManaBurstAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(40, 4, 48, golem, lv);
	}

	@Override
	protected void performAttack(LivingEntity target) {
		var manaCost = MGConfig.COMMON.manaBurstCost.get() * lv;
		var bot = new BotUtils(golem);
		if (bot.getMana() < manaCost) return;
		bot.consumeMana(manaCost);
		var burst = getBurst(golem);
		golem.level().addFreshEntity(burst);
	}

	public ManaBurstEntity getBurst(LivingEntity golem) {
		ManaBurstEntity burst = new GolemManaBurstEntity(golem, lv);
		Vec3 pos = new Vec3(golem.getX(), golem.getEyeY() - (double) 0.1F, golem.getZ());
		burst.setPos(pos);
		Vec3 forward = golem.getForward();
		if (golem instanceof Mob mob) {
			var target = mob.getTarget();
			if (target != null) {
				forward = new Vec3(target.getX(), target.getY(0.5), target.getZ()).subtract(pos).normalize();
			}
		}
		double d0 = forward.horizontalDistance();
		burst.setYRot((float) (180 - Mth.atan2(forward.x, forward.z) * Mth.RAD_TO_DEG));
		burst.setXRot((float) (Mth.atan2(forward.y, d0) * Mth.RAD_TO_DEG));
		burst.setDeltaMovement(ManaBurstEntity.calculateBurstVelocity(burst.getXRot(), burst.getYRot()));

		float motionModifier = 7F;

		int manaCost = MGConfig.COMMON.manaBurstCost.get() * lv;
		burst.setColor(0x20FF20);
		burst.setMana(manaCost);
		burst.setStartingMana(manaCost);
		burst.setMinManaLoss(40);
		burst.setManaLossPerTick(manaCost / 40f);
		burst.setGravity(0F);
		burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));

		return burst;
	}

}


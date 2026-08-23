package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import com.eeeab.eeeabsmobs.sever.entity.effect.projectile.EntityAnnihilatorMissile;
import com.eeeab.eeeabsmobs.sever.init.EffectInit;
import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Multi-target goal for {@link AnnihilatorMissileModifier}.
 * See {@code EntityRelicAnnihilator:performRangedAttack} for original logic.
 */
public class AnnihilatorMissileAttackGoal extends MultiTargetRangedGoal {

	public AnnihilatorMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		// wait 40, search 32, cd between targets 5, mirrors EnderGuardianVoidRune (100,0,15) but faster volley
		super(40, 0, 32, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 32;
	}

	@Override
	protected int getMaxTarget() {
		return Math.max(1, lv * 2);
	}

	@Override
	protected int cd() {
		return 5;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		if (golem.level().isClientSide) return;
		// muzzle approx eye + forward 1.2 (simplified from width*2.4 / height*0.96)
		Vec3 muzzle = golem.getEyePosition().add(golem.getForward().scale(1.2)).add(0, -0.2, 0);
		Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.4, 0);
		Vec3 projectileMid = muzzle.add(0, 0.25, 0);
		Vec3 shootVec = targetPos.subtract(projectileMid).normalize();

		EntityAnnihilatorMissile.ElementType element = target.hasEffect(EffectInit.ELECTRIFIED_EFFECT.get())
				? EntityAnnihilatorMissile.ElementType.BLAZE
				: EntityAnnihilatorMissile.ElementType.VOLT;
		// sparkferno 20% when low health (mirrors EntityRelicAnnihilator:1058-1060)
		if (golem.getHealth() / golem.getMaxHealth() < 0.5F && golem.getRandom().nextFloat() < 0.2F) {
			element = EntityAnnihilatorMissile.ElementType.SPARKFERNO;
		}
		EntityAnnihilatorMissile missile = new EntityAnnihilatorMissile(golem.level(), golem, element);
		missile.moveTo(muzzle.x, muzzle.y, muzzle.z, golem.getYRot(), golem.getXRot());
		// blind check omitted for golem (no blind); keep spread 0
		missile.shoot(shootVec.x, shootVec.y, shootVec.z, 1.6F, 0.0F);
		golem.level().addFreshEntity(missile);
		golem.getNavigation().stop();
	}

}

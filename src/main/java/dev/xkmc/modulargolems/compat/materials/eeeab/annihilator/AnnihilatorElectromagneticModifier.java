package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityElectromagnetic;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * Relic Annihilator - Electromagnetic Ground Pound (earthquake)
 * Original: {@code GROUND_POUND_ANIMATION2} keyframe 20-24
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/mob/relicron/EntityRelicAnnihilator.java:721-755
 * <pre>
 *  tick20: doGroundPoundEffect(2.25, false, true)
 *  if (!client && health<=0.5) {
 *    count=6; offset=toRadians(random*360-180)
 *    for i 0..5:
 *      f1 = yRot + (i+offset)*PI*0.333 // 60deg
 *      pos = checkSummonEntityPoint(entity, pos.x, pos.z, pos.y, pos.y+1.0)
 *      EntityElectromagnetic.shoot(level,caster,pos,2.0F,10,5, f1*57.29-90, false)
 *  }
 * </pre>
 * This modifier <b>always</b> creates the intermediate {@code EntityElectromagnetic}
 * (ignoring the original health condition as requested).
 * Uses {@code EarthquakeHelper.Modifier} pattern like {@code MaledictusEarthquakeModifier}.
 */
public class AnnihilatorElectromagneticModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public AnnihilatorElectromagneticModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		// original pound hybridDistance 8,0,4 with GROUND_POUND 400,50,50
		// for golem use 8 blocks frontal
		return 8 * 8;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		if (golem.level().isClientSide) return;
		// frontal pound position mirrors getPosOffset(false,2.25,0.2,0)
		Vec3 look = golem.getForward().normalize();
		Vec3 poundPos = golem.position().add(look.scale(2.25)).add(0, 0.2, 0);
		// optional ground effect already handled via handleEvent FLAG
		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);

		// always spawn 6 electromagnetics (original only if health<=0.5)
		// scale count slightly with lv if desired, keep 6 for fidelity
		int count = 6 + Math.max(0, lv - 1); // 6..8
		float offset = (float) Math.toRadians(golem.getRandom().nextFloat() * 360.0F - 180.0F);
		for (int i = 0; i < 6; i++) {
			float f1 = (float) (golem.getYRot() + (i + offset) * Math.PI * 0.3333333333333333);
			// original uses ModEntityUtils.checkSummonEntityPoint to find ground; simplify to poundPos
			// use same Vec3 for all (EntityElectromagnetic will be placed there and move by yaw)
			Vec3 spawnPos = new Vec3(poundPos.x, poundPos.y, poundPos.z);
			float yawDeg = f1 * Mth.RAD_TO_DEG - 90.0F;
			EntityElectromagnetic.shoot(golem.level(), golem, spawnPos, 2.0F, 10, 5, yawDeg, false);
		}
		// slight extra for lv scaling: spawn additional 2 if lv==3 near center
		if (lv >= 3) {
			for (int i = 0; i < 2; i++) {
				float f1 = (float) (golem.getYRot() + (i + offset + 3) * Math.PI * 0.3333333333333333);
				Vec3 spawnPos = new Vec3(poundPos.x, poundPos.y, poundPos.z);
				float yawDeg = f1 * Mth.RAD_TO_DEG - 90.0F;
				EntityElectromagnetic.shoot(golem.level(), golem, spawnPos, 1.5F, 8, 6, yawDeg, false);
			}
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// original HealthScaledCooldown 400,50,50 -> ~400 at high health
		int base = 400 - 40 * (lv - 1);
		return Math.max(200, base);
	}

}

package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.world.entity.LivingEntity;

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
		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);
		EEEABProxy.spawnElectromagneticBurst(golem, lv);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// original HealthScaledCooldown 400,50,50 -> ~400 at high health
		int base = 400 - 40 * (lv - 1);
		return Math.max(200, base);
	}

}

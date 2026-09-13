package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 * Earthquake modifier for Obliterator Jump Ground Charge (state 22:3023).
 * Original: AreaAttack(5.0f,4.0f,180,23.0) + SideAreaAttack(4.25f,5.0f,90) + spawnFlames 6x multiplier 4.0 + clone + plasma.
 * Large AoE ground smash with controlledSmashParticles / CameraShake. No proxy required for golem quake.
 * Reference: TheObliteratorEntity:3036-3047, IgnisJumpModifier / RealmWardenJumpSmashModifier pattern.
 */
public class ObliteratorJumpGroundChargeModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public ObliteratorJumpGroundChargeModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		// replicate visuals + proxies: 6 flames circular, 1 armed clone, 5 plasma orbs, CameraShake, RingData, doSmashEffects
		LMProxy.spawnObliteratorJumpGroundChargeQuake(golem, level);
		if (golem.level() instanceof ServerLevel sl) {
			// range 5.0 matches original AreaAttack(5.0f) :3023
			var list = LMProxy.stun(sl, golem.getX(), golem.getY(), golem.getZ(), golem, 5.0f, level);
			for (var e : list) {
				EarthquakeHelper.launch(golem, e, 1.2f);
			}
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25.0;
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part.getEntityType() != GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
	}

}

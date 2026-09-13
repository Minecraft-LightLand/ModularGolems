package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 *
 * Earthquake modifier for Obliterator Ultimate (state 53:3976).
 * Original: SideAreaAttack(6.0f,4.0f,380) + spawnFlames GroundNuke + AnnihilationPortalEntity doPortalEffect 5+7.
 * Large full-circle quake (6.0f 380° overfull) with huge radius.
 * Reference: TheObliteratorEntity:3980-4012, EarthquakeHelper.Modifier pattern.
 */
public class ObliteratorUltimateEarthquakeModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public ObliteratorUltimateEarthquakeModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		// replicate visuals + proxies: central portal + 10 outer portals, 6 flames, Sphereparticle, shockwave flames, CameraShake, RingData
		LMProxy.spawnObliteratorUltimateQuake(golem, level);
		if (golem.level() instanceof ServerLevel sl) {
			// range 6.0 matches original SideAreaAttack(6.0f) :3980, full circle 380°
			var list = LMProxy.stun(sl, golem.getX(), golem.getY(), golem.getZ(), golem, 6.0f, level * 2);
			for (var e : list) {
				EarthquakeHelper.launch(golem, e, 1.5f);
			}
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		// 6.0f range -> 36, ultimate larger than jump (5.0 -> 25)
		return 36.0;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 200;
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part.getEntityType() == GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
	}

}

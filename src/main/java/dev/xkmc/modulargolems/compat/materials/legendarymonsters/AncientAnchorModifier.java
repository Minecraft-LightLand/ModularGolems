package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class AncientAnchorModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public AncientAnchorModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.addDealtModifier(DamageModifier.multTotal(EarthquakeHelper.LEAP_DAMAGE_MULT, getRegistryName()));
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		LMProxy.shake(golem, golem.position());
		if (golem.level() instanceof ServerLevel sl) {
			LMProxy.anchorParticle(sl, golem.getX(), golem.getY(), golem.getZ(), 5f);
			var list = LMProxy.stun(sl, golem.getX(), golem.getY(), golem.getZ(), golem, 5f, level);
			for (var e : list) {
				EarthquakeHelper.launch(golem, e, 1);
			}
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 8 * 8;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}

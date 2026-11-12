package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

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

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import java.util.function.Consumer;

public class AncientAnchorModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public static final int COOLDOWN = 200;

	public AncientAnchorModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
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
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
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

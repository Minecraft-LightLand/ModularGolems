package dev.xkmc.modulargolems.compat.materials.l2hostility;

import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.List;

public class RegenModifier extends GolemModifier {

	public RegenModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		return super.onHealTick(heal, entity, level);
	}

	@Override
	public List<MutableComponent> getDetail(int i) {
		List<MutableComponent> ans = new ArrayList<>();
		ans.add(Component.translatable(LHTraits.REGEN.get().getDescriptionId() + ".desc",
						Component.literal((int) Math.round(LHConfig.COMMON.regen.get() * 100 * i) + "")
								.withStyle(ChatFormatting.AQUA))
				.withStyle(ChatFormatting.GREEN));
		return ans;
	}

}

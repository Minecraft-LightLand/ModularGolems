package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class AncientMeltdownModifier extends GolemModifier {

	public AncientMeltdownModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.REFORGE);
	}

	/**
	 * 遗骸重组：半血以下每秒回复生命值；远古金属胸甲把傀儡钉在最终形态，所以穿上胸甲后一直生效。
	 * <p>
	 * onAiStep 每 tick 跑一次，这里按 20 tick 累积，避免每帧都治疗。
	 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		if (!CataArmorHelper.ancientFinalForm(golem)) return;
		if (golem.tickCount % 20 != 0) return;
		golem.repair(MGConfig.COMMON.reformationArmorRegen.get().floatValue());
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH),
				CataArmorHelper.number(MGConfig.COMMON.reformationArmorRegen.get().floatValue())).withStyle(ChatFormatting.GREEN));
	}

}

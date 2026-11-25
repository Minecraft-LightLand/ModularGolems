package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

import java.util.function.Consumer;

public class AncientMeltdownModifier extends GolemModifier {

	public AncientMeltdownModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.REFORGE);
	}

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

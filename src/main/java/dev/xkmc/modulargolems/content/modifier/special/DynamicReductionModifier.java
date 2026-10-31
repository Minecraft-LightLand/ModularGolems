package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class DynamicReductionModifier extends GolemModifier {

	public DynamicReductionModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.DYNAMIC_REDUCTION);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var cap = Component.literal("20%")
				.withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", cap).withStyle(ChatFormatting.GREEN));
	}

}

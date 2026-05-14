package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum HumanoidGolemPartType implements IGolemPart<HumanoidGolemPartType> {
	BODY, ARMS, LEGS;

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.humanoid_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public GolemPart<?, HumanoidGolemPartType> toItem() {
		return switch (this) {
			case BODY -> GolemItems.HUMANOID_BODY.get();
			case ARMS -> GolemItems.HUMANOID_ARMS.get();
			case LEGS -> GolemItems.HUMANOID_LEGS.get();
		};
	}

}

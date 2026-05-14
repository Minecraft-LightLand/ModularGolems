package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum DogGolemPartType implements IGolemPart<DogGolemPartType> {
	BODY, LEGS;

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.dog_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public GolemPart<?, DogGolemPartType> toItem() {
		return switch (this) {
			case BODY -> GolemItems.DOG_BODY.get();
			case LEGS -> GolemItems.DOG_LEGS.get();
		};
	}

}

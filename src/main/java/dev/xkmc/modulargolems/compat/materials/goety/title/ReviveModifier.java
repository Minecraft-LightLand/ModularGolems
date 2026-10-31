package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class ReviveModifier extends GolemModifier {

	public ReviveModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.RECYCLE);
		addFlag.accept(GolemFlags.REVIVE);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var health = Component.literal("0").withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", health, health).withStyle(ChatFormatting.GREEN));
	}

}

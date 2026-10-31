package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class SimpleFlagModifier extends GolemModifier {

	private final GolemFlags flag;

	public SimpleFlagModifier(StatFilterType type, GolemFlags flag) {
		super(type, 1);
		this.flag = flag;
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(flag);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		// only RECYCLE's description mentions a number; the other flags stay on the plain text template
		if (flag != GolemFlags.RECYCLE) return super.getDetail(v);
		var health = Component.literal("" + (int) AbstractGolemEntity.DEAD_HEALTH).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", health).withStyle(ChatFormatting.GREEN));
	}

}

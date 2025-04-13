package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

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

}

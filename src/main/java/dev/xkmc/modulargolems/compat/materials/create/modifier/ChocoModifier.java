package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

import java.util.function.Consumer;

public class ChocoModifier extends GolemModifier {

	public ChocoModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.REFORGE);
	}

}

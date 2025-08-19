package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import java.util.function.Consumer;

public class AncientMeltdownModifier extends GolemModifier {

	public AncientMeltdownModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public boolean fitsOn(GolemType<?, ?> type) {
		return type == GolemTypes.TYPE_GOLEM.get();
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.REFORGE);
	}

}

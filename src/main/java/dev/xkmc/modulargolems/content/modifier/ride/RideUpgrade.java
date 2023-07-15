package dev.xkmc.modulargolems.content.modifier.ride;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import java.util.function.Consumer;

public class RideUpgrade extends AttributeGolemModifier {

	public RideUpgrade(int max, AttrEntry... entries) {
		super(max, entries);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.PASSIVE);
	}

	@Override
	public boolean fitsOn(GolemType<?, ?> type) {
		return type == GolemTypes.TYPE_DOG.get();
	}

}

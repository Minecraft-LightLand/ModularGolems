package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.PotionDefenseModifier;

import java.util.function.Consumer;

public class CleanseModifier extends PotionDefenseModifier {

	public CleanseModifier() {
		super(1, LCEffects.CLEANSE::get);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EFFECT_IMMUNE);
	}

}

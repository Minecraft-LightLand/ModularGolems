package dev.xkmc.modulargolems.compat.materials.tinker.effect;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.modulargolems.compat.materials.common.StackableEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HepatizonDefenseEffect extends InherentEffect implements StackableEffect<HepatizonDefenseEffect> {

	public HepatizonDefenseEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

}

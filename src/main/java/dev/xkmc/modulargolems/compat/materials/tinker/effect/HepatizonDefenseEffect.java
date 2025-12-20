package dev.xkmc.modulargolems.compat.materials.tinker.effect;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.compat.materials.common.StackableEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HepatizonDefenseEffect extends InherentEffect implements StackableEffect<HepatizonDefenseEffect> {

	public HepatizonDefenseEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

}

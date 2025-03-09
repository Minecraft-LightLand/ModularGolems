package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2core.base.effects.api.InherentEffect;
import dev.xkmc.modulargolems.compat.materials.common.StackableEffect;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RageEffect extends InherentEffect implements StackableEffect<RageEffect> {

	public RageEffect(MobEffectCategory category, int color) {
		super(category, color);
		var uuid = ModularGolems.loc("rage");
		addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

}

package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.compat.materials.common.StackableEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RageEffect extends InherentEffect implements StackableEffect<RageEffect> {

	public RageEffect(MobEffectCategory category, int color) {
		super(category, color);
		var uuid = MathHelper.getUUIDFromString("modulargolems:rage");
		addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid.toString(), 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

}

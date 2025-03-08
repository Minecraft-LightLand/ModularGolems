package dev.xkmc.modulargolems.compat.materials.tinker.effect;

import dev.xkmc.l2complements.content.effect.skill.StackingEffect;
import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.l2library.util.math.MathHelper;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ManyullynEffect extends InherentEffect implements StackingEffect<ManyullynEffect> {

	public ManyullynEffect(MobEffectCategory category, int color) {
		super(category, color);
		var uuid = MathHelper.getUUIDFromString("modulargolems:manyullyn");
		addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid.toString(), 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

}

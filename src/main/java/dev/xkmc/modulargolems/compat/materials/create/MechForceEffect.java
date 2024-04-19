package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MechForceEffect extends InherentEffect {

	protected MechForceEffect(MobEffectCategory category, int color) {
		super(category, color);
		var uuid = MathHelper.getUUIDFromString("modulargolems:mech_force");
		addAttributeModifier(Attributes.ATTACK_DAMAGE, uuid.toString(), 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override
	public double getAttributeModifierValue(int lv, AttributeModifier val) {
		return MGConfig.COMMON.mechAttack.get() * (lv + 1);
	}

}

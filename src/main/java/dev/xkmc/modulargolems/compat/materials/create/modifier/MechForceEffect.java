package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.l2core.base.effects.api.InherentEffect;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MechForceEffect extends InherentEffect {

	public MechForceEffect(MobEffectCategory category, int color) {
		super(category, color);
		addAttributeModifier(Attributes.ATTACK_DAMAGE, ModularGolems.loc("mech_force"),
				AttributeModifier.Operation.ADD_MULTIPLIED_BASE, this::val);
	}

	public double val(int lv) {
		var e = MGConfig.COMMON.mechAttack;
		if (MGConfig.COMMON.getSpec() instanceof ModConfigSpec spec && spec.isLoaded()) {
			return e.get() * (lv + 1);
		}
		return e.getDefault() * (lv + 1);
	}


}

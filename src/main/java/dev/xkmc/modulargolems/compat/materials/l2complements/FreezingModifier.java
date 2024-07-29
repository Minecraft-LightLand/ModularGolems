package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.content.enchantment.legacy.IceBladeEnchantment;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2core.init.reg.ench.LegacyEnchantment;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class FreezingModifier extends GolemModifier {

	public FreezingModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		IceBladeEnchantment.doPostAttack(entity, event.getEntity(), level);
	}

	@Override
	public void postDamaged(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		if (event.getSource().getDirectEntity() instanceof LivingEntity attacker)
			LCEnchantments.ICE_THORN.get().doPostHurt(entity, attacker, level);
	}

	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		return event.getSource().is(DamageTypeTags.IS_FREEZING);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.FREEZE_IMMUNE);
	}

}

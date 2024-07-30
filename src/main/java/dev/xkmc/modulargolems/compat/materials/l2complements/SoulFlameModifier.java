package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.data.LCDamageTypes;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;

public class SoulFlameModifier extends GolemModifier {

	public SoulFlameModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		LCEnchantments.HELLFIRE_BLADE.legacy().get().onAttack(entity, event.getTarget(), level);
	}

	@Override
	public void postDamaged(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		if (event.getSource().getDirectEntity() instanceof LivingEntity attacker)
			LCEnchantments.HELLFIRE_THORN.legacy().get().onDamage(entity, attacker, level);
	}

	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		return event.getSource().is(LCDamageTypes.SOUL_FLAME);
	}

}

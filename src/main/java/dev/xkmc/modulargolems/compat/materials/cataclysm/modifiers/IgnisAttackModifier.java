package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;

public class IgnisAttackModifier extends GolemModifier {

	public IgnisAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> golem, DamageData.Offence event, int level) {
		LivingEntity target = event.getTarget();
		CataclysmProxy.stackBlazingBrand(golem, target, level);
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (golem.getHealth() < golem.getMaxHealth() / 2) {
			if (event.getResult() != null && event.getResult().toRoot() == L2DamageTypes.MOB_ATTACK) {
				event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

}

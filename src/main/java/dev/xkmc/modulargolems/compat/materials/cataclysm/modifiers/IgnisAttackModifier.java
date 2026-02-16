package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.LivingEntity;

public class IgnisAttackModifier extends GolemModifier {

	public IgnisAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		LivingEntity target = cache.getAttackTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue() * level;
		CataclysmProxy.stackBlazingBrand(golem, target, rate * cache.getDamageDealt(), 1);
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (CataDispatch.ignisBlue(golem)) {
			if (event.getResult() != null && event.getResult().toRoot() == L2DamageTypes.MOB_ATTACK) {
				event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

}

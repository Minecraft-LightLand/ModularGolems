package dev.xkmc.modulargolems.compat.materials.tinker.modifier;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.compat.materials.tinker.TCCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ManyullynAttackModifier extends GolemModifier {

	public ManyullynAttackModifier() {
		super(StatFilterType.ATTACK, 5);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		TCCompatRegistry.EFF_MANYULLYN.get().addTo(entity, 100, level - 1, EffectUtil.AddReason.SELF, entity);
	}

}

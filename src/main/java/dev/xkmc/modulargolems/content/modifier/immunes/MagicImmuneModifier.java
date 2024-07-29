package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.neoforged.neoforge.common.Tags;

public class MagicImmuneModifier extends GolemModifier {

	public MagicImmuneModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		return level > 0 && event.getSource().is(Tags.DamageTypes.IS_MAGIC);
	}

}

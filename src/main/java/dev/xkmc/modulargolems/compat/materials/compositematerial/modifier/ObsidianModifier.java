package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class ObsidianModifier extends GolemModifier {

	public ObsidianModifier() {
		super(StatFilterType.MASS, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		event.setAmount((float) Math.max(0, event.getAmount() - level * 2));//TODO config
	}

}

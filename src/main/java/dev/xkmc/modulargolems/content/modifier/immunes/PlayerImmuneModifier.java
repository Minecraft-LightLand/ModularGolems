package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.data.TagGen;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class PlayerImmuneModifier extends GolemModifier {

	public PlayerImmuneModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level == 0) return;
		Entity source = event.getSource().getEntity();
		if (source == null) return;
		if (source.getType().is(TagGen.GOLEM_FRIENDLY) || entity.isAlliedTo(source)) {
			event.setCanceled(true);
		}
	}

}

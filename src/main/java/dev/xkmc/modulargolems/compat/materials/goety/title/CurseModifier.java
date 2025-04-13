package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.ArrayList;
import java.util.List;

public class CurseModifier extends GolemModifier {

	public CurseModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var e = event.getEntity();
		List<MobEffect> list = new ArrayList<>();
		for (var eff : e.getActiveEffectsMap().keySet()) {
			if (eff.isBeneficial())
				list.add(eff);
		}
		var rand = entity.getRandom();
		for (int i = 0; i < level; i++) {
			if (list.isEmpty()) return;
			var sel = list.remove(rand.nextInt(list.size()));
			e.removeEffect(sel);
		}
	}

}

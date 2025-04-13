package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemItems;

public class FastBowModifier extends GolemModifier {

	public FastBowModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (!golem.isUsingItem()) return;
		if (WeaponRegistry.BOW.isValidItem(golem.getUseItem())) {
			golem.speedUpUseItem(level);
		}
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part == GolemItems.HUMANOID_ARMS.get();
	}

}

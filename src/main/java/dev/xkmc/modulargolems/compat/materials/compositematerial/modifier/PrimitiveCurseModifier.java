package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class PrimitiveCurseModifier extends GolemModifier {

	public PrimitiveCurseModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		float fac = (float) Math.pow(0.8, level);//TODO config
		event.setAmount(event.getAmount() * fac);
	}

	@Override
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		return -lv;
	}

}

package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class PrimitiveCurseModifier extends GolemModifier {

	public PrimitiveCurseModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		double multiplier = MGConfig.COMMON.primitiveDamageMultiplier.get();
		float fac = (float) Math.pow(multiplier, level);//TODO config
		event.setAmount(event.getAmount() * fac);
	}

	@Override
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		return -lv;
	}

	public List<MutableComponent> getDetail(int v) {
		float multiplier = (float) Math.pow(MGConfig.COMMON.primitiveDamageMultiplier.get(), v);
		int perc = Math.round(100 * multiplier);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc));
	}
}

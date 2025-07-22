package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HarbingerDeathBeamModifier extends GolemModifier {

	public HarbingerDeathBeamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerDeathBeamAttackGoal(entity, lv));
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		var source = cache.getSource();
		if (CataclysmProxy.isLaser(source) && entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.HARBINGER_HELMET.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.laserArmorBonus.get().floatValue(), ModularGolems.loc("harbinger_armor")));
		}
	}

}

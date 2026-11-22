package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HarbingerHomingMissileModifier extends GolemModifier {

	public HarbingerHomingMissileModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerHomingMissileAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	private static boolean isBoosted(DamageSource source) {
		return GolemCataProxy.isMissile(source) || GolemCataProxy.isWitherMissile(source) || GolemCataProxy.isHowitzer(source);
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (!isBoosted(event.getSource())) return;
		if (!entity.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.HARBINGER_CHESTPLATE.get())) return;
		event.getTarget().invulnerableTime = 0;
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		var source = cache.getSource();
		if (isBoosted(source) && entity.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.HARBINGER_CHESTPLATE.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.missileArmorBonus.get().floatValue(), ModularGolems.loc("harbinger_armor")));
		}
	}

}

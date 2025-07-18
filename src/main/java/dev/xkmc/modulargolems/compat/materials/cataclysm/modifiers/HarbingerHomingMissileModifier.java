package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HarbingerHomingMissileModifier extends GolemModifier {

	public HarbingerHomingMissileModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerHomingMissileAttackGoal(entity, lv));
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		var source = event.getSource();
		if (CataclysmProxy.isMissile(source) && entity.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.HARBINGER_CHESTPLATE.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(3));
		}
	}

}

package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class ThunderAttackModifier extends GolemModifier {

	public ThunderAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ThunderAttackGoal(entity, lv));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		var attacker = event.getSource().getDirectEntity();
		if (attacker == null) return;
		float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * (0.2f + level * 0.2f);
		int n = 4 + level;
		LMProxy.spawnElectricShock(attacker, entity, damage, n);
	}
}

package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class AncientRemnantSandstormModifier extends GolemModifier {

	public AncientRemnantSandstormModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AncientRemnantSandstormAttackGoal(entity, lv));
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (GolemCataProxy.isSandstorm(event.getSource())) {
			event.getTarget().invulnerableTime = 0;
		}
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		int lv = GolemCataProxy.getSandCurseLevel(cache.getTarget());
		if (lv > 0) {
			float factor = 1 + level * MGConfig.COMMON.sandCurseBonus.get().floatValue();
			cache.addHurtModifier(DamageModifier.multTotal(factor, ModularGolems.loc("curse_of_desert") ));
		}
	}

}

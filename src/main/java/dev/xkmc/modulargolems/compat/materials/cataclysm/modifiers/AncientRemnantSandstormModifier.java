package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class AncientRemnantSandstormModifier extends GolemModifier {

	public AncientRemnantSandstormModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AncientRemnantSandstormAttackGoal(entity, lv).withName(this.getDescriptionId()));
		addGoal.accept(6, new AncientRemnantSandstormTrackGoal(entity));
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		if (event == null) return;
		int lv = GolemCataProxy.getSandCurseLevel(cache.getAttackTarget());
		if (lv > 0) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + level * MGConfig.COMMON.sandCurseBonus.get().floatValue()));
		}
	}

}

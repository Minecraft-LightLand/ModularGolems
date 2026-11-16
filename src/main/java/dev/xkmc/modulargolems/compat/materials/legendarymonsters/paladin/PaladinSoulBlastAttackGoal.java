package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 灵魂爆破（Boss state31 的 SoulStrike(1.0F, 15, 0.0F)）：一次放射 15 道灵魂冲击。
 */
public class PaladinSoulBlastAttackGoal extends BaseRangedAttackGoal {

	public PaladinSoulBlastAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(PaladinSoulBlastModifier.COOLDOWN, PaladinSoulBlastModifier.MIN_RANGE,
				PaladinSoulBlastModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		LMProxy.paladinSoulShockwave(golem, lv);
		return true;
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}

	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}

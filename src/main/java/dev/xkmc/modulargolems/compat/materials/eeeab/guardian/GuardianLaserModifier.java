package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 无名守卫 - 招式7：激光束。
 * 对应 {@code GuardianShootLaserGoal} 的 {@code LASER_ANIMATION}（120 tick），使用 {@code EntityGuardianLaser}。
 */
public class GuardianLaserModifier extends GolemModifier {	public static final int COOLDOWN = 100;
	public static final int MIN_RANGE = 4, MAX_RANGE = 32;
	/** 等级只缩短冷却（每级减半，最低 1），与其它技能一致。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}


	public GuardianLaserModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new GuardianLaserAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}

package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 湮灭者 - 激光（单体）。
 * 对应 {@code EntityRelicAnnihilator} 的 {@code LASER_ANIMATION}：tick9 生成红外预警射线 {@code EntityInfraredRay}，
 * tick49 生成 {@code EntityGuardianLaser}（{@code setCountDown(1)}、type=RELIC_ANNIHILATOR）。
 */
public class AnnihilatorLaserModifier extends GolemModifier {	public static final int COOLDOWN = 200;
	public static final int MIN_RANGE = 0, MAX_RANGE = 24;
	public static final int CHARGE_TICKS = 49, WARN_TICK = 9;

	/** 等级只缩短冷却（每级减半，最低 1），与其它技能一致。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}


	public AnnihilatorLaserModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AnnihilatorLaserAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}

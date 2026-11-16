package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * 控制气旋：Boss state23 一次甩出两股气旋（tick 23 与 tick 38 各一股），分别朝不同目标。
 * <p>
 * 用 {@link MultiTargetRangedGoal} 而不是单目标 Goal，和灾变鸣雷槊（{@code ScyllaLightningAttackGoal}）
 * 是同一套：先收集最近的若干个目标，每个目标各吃一发；目标不足两个时两股气旋都锁同一个目标。
 */
public class CycloneAttackGoal extends MultiTargetRangedGoal {

	/** 一次施放的气旋数量：1 级 2 个（对应 Boss 的两股），2 级 4 个。 */
	private static int cyclonesFor(int lv) {
		return lv >= 2 ? 4 : 2;
	}

	public CycloneAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(CycloneModifier.COOLDOWN, CycloneModifier.MIN_RANGE, CycloneModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected int searchRange() {
		return CycloneModifier.MAX_RANGE;
	}

	@Override
	protected int getMaxTarget() {
		return cyclonesFor(lv);
	}

	@Override
	protected int cd() {
		return 2;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		if (golem.level().isClientSide) return;
		double dx = target.getX() - golem.getX();
		double dz = target.getZ() - golem.getZ();
		float yaw = (float) (Mth.atan2(-dx, dz) * Mth.RAD_TO_DEG);
		LMProxy.spawnCloudGolemTornado(golem, yaw,
				GolemCombatHelper.getAttackDamage(golem) * CycloneModifier.DAMAGE,
				CycloneModifier.LIFE);
		SkillSound.playChargeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
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

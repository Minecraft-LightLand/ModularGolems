package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 天穹激光的施放骨架：起手后召唤一道 {@link CloudLaserEntity} 并让它挂在傀儡头部，
 * 光束存活期间技能不结束（{@code canContinueToUse}），死亡/被打断时由 stop() 清理引用。
 */
public class SkyLaserAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public SkyLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(SkyLaserModifier.COOLDOWN, SkyLaserModifier.MIN_RANGE, SkyLaserModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		if (beam != null) return true;
		return super.canContinueToUse();
	}

	@Override
	public void stop() {
		super.stop();
		beam = null;
	}

	@Override
	public void tick() {
		if (beam != null) {
			if (beam.isRemoved()) {
				beam = null;
			} else {
				LivingEntity target = golem.getTarget();
				// 光束自己每 tick 跟随头部朝向，这里只负责让头抬起来瞄准
				if (target != null) golem.getLookControl().setLookAt(target, 30, 90);
			}
		}
		super.tick();
	}

	/** 二阶段（≤65% 生命）才开放，对应 Boss 激光只在 angry 状态使用。 */
	@Override
	public boolean canUse() {
		return CloudPhaseHelper.inSecondPhase(golem) && super.canUse();
	}
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		beam = new CloudLaserEntity(LMCompatRegistry.CLOUD_LASER.get(), golem.level(), golem,
				SkyLaserModifier.BEAM_DURATION,
				GolemCombatHelper.getAttackDamage(golem) * SkyLaserModifier.BEAM_DAMAGE);
		golem.level().addFreshEntity(beam);
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

package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMTeleportBackstepHelper;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 克隆八向炮：Boss AmbushGoal 的节奏 ——
 * t29 瞬移到目标背后、t33 第一轮 8 个虚影、二阶段 t49 再补一轮。
 */
public class ObliteratorCloneBarrageAttackGoal extends BaseRangedAttackGoal {

	private boolean casting;
	private int castTicks;
	private boolean secondDone;

	public ObliteratorCloneBarrageAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ObliteratorCloneBarrageModifier.COOLDOWN, ObliteratorCloneBarrageModifier.MIN_RANGE,
				ObliteratorCloneBarrageModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
		secondDone = false;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}
	@Override
	public void stop() {
		super.stop();
		casting = false;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (casting) {
			LivingEntity target = golem.getTarget();
			if (target == null || golem.isRemoved()) {
				casting = false;
			} else {
				castTicks++;
				if (castTicks == 29) LMProxy.cloneBarrageStart(golem);
				if (castTicks == 33) LMProxy.spawnCloneBarrage(golem, target, lv, false);
				if (castTicks == 49 && !secondDone && LMArmorHelper.obliteratorPhaseTwo(golem)) {
					LMProxy.spawnCloneBarrage(golem, target, lv, true);
					secondDone = true;
				}
				if (castTicks >= 60) casting = false;
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTicks = 0;
		secondDone = false;
		casting = true;
		return true;
	}

	@Override
	protected BackstepHelper backstep() {
		return new LMTeleportBackstepHelper(golem);
	}

	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}

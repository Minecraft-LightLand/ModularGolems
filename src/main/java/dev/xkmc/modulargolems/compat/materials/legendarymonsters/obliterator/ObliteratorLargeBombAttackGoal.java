package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMTeleportBackstepHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 湮灭巨弹（Boss state 6 / 55）：照 Boss 的节奏起手 ——
 * t17 蓄力音 DIMENSIONAL_SHOOT_CHARGE、t20/t30 双 SHRINK 环、t37 爆发粒子 + 弹体 + THE_WARPED_ONE_SHOOT。
 * 伤害 10（按傀儡攻击力等比缩放），弹体自带 16 发子弹。
 */
public class ObliteratorLargeBombAttackGoal extends BaseRangedAttackGoal {

	private boolean casting;
	private int castTicks;

	public ObliteratorLargeBombAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ObliteratorLargeBombModifier.cooldownFor(lv), ObliteratorLargeBombModifier.MIN_RANGE,
				ObliteratorLargeBombModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
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
				if (castTicks == 11) LMProxy.obliteratorTeleportBehind(golem, -6.0);
				if (castTicks == 17) LMProxy.obliteratorBombCharge(golem);
				if (castTicks == 20 || castTicks == 30) LMProxy.sendRing(golem, 80.0f, 20, true, 0.0);
				if (castTicks == 37) {
					LMProxy.obliteratorBombFire(golem, target, lv);
				}
				if (castTicks >= 44) casting = false;
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTicks = 0;
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

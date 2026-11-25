package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMTeleportBackstepHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 湮灭小弹：齐射方式参考灾变鸣雷槊（{@code ScyllaLightningAttackGoal}）——
 * 一次齐射 {@link ObliteratorSmallBombModifier#BURST_COUNT} 发，
 * 目标足够时每发锁定一个目标；只有一个目标时全部打向它，并按 lateral 横向错开，
 * 避免所有炸弹从同一点射出。
 * <p>
 * 冷却随等级减半，见 {@link ObliteratorSmallBombModifier#cooldownFor(int)}。
 */
public class ObliteratorSmallBombAttackGoal extends BurstRangedGoal {

	private boolean casting;

	public ObliteratorSmallBombAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ObliteratorSmallBombModifier.cooldownFor(lv), ObliteratorSmallBombModifier.MIN_RANGE,
				ObliteratorSmallBombModifier.MAX_RANGE, golem, lv);
	}

	/**
	 * 齐射期间不因目标暂时漂出射程 / 后撤步把自己推远而中断。
	 * 基类默认的 canContinueToUse 直接复用 canUse，会让 3 发齐射只打出 1~2 发。
	 */
	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}

	@Override
	public void start() {
		super.start();
		casting = false;
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
	}

	@Override
	protected int getBurstCount() {
		return ObliteratorSmallBombModifier.BURST_COUNT;
	}

	/** 1+1+1：每发之间都隔 {@link ObliteratorSmallBombModifier#BURST_DELAY} tick，逐发打出。 */
	@Override
	protected int getBurstDelay() {
		return ObliteratorSmallBombModifier.BURST_DELAY;
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		casting = true;
		boolean done = super.performAttack(target);
		if (done) casting = false;
		return done;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		LMProxy.spawnObliteratorLargeBomb(golem, target, lv);
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

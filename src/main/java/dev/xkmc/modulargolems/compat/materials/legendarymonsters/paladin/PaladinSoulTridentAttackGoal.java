package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 堕魂掷戟（Boss state38）：t28 投掷灵魂三叉戟，投掷完成后即结束（纯远程技能）。
 * Boss 原版后续的 t54 / t69 两次 90° 斩与 t69~83 灵魂柱冲击波均未复刻——
 * 前者按需求裁掉，后者已独立成「灵魂爆破」技能。
 */
public class PaladinSoulTridentAttackGoal extends BaseRangedAttackGoal {

	private boolean casting;
	private int castTicks;
	private float castYaw;
	private double centerX, centerZ;

	public PaladinSoulTridentAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(PaladinSoulTridentModifier.COOLDOWN, PaladinSoulTridentModifier.MIN_RANGE,
				PaladinSoulTridentModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		return golem.getHealth() < golem.getMaxHealth() * PaladinSoulTridentModifier.PHASE_TWO && super.canUse();
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
				if (castTicks == 28) LMProxy.paladinThrowTrident(golem, target, lv);
				if (castTicks >= 34) casting = false;   // 投掷完成即结束（纯远程，不再有后续招式）
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		// 锁定起手朝向与中心：后续冲击波固定在释放点，不再跟随傀儡
		castYaw = (float) Math.toDegrees(Math.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()));
		golem.setYRot(castYaw);
		golem.yBodyRot = castYaw;
		golem.yHeadRot = castYaw;
		centerX = golem.getX();
		centerZ = golem.getZ();
		castTicks = 0;
		casting = true;
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

package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 克隆爆发：真·冲锋技能（{@link BaseChargeGoal}）。
 * 起冲时生成 3 只持械虚影，本体随即以标准冲锋逻辑朝目标冲刺（{@link ChargeHelper#moveOneTick}），
 * 撞击造成臂刃伤害，冲刺结束（跑够距离/超时/受阻）补一记臂刃范围斩。仅二阶段开放。
 */
public class ObliteratorCloneBurstAttackGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "ObliteratorCloneBurstCharging";

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public ObliteratorCloneBurstAttackGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	@Override
	public boolean canUse() {
		// 额外门槛：仅二阶段开放（Boss 的 getPhase() >= 2）
		if (!ObliteratorPhaseHelper.isPhaseTwo(golem)) return false;
		return canUseCharge(golem, ChargeHelper.findChargeTarget(golem, ObliteratorCloneBurstModifier.MAX_RANGE),
				ObliteratorCloneBurstModifier.MIN_RANGE, ObliteratorCloneBurstModifier.MAX_RANGE);
	}
	@Override
	public void start() {
		LivingEntity target = ChargeHelper.findChargeTarget(golem, ObliteratorCloneBurstModifier.MAX_RANGE);
		if (target == null) {
			blocked = true;
			return;
		}
		ticks = 0;
		blocked = false;
		golem.getNavigation().stop();
		golem.setTransientFlag(KEY_ACTIVE, true);
		EarthquakeHelper.setBusy(golem, true);
		markUsed(golem);
		begin(target);
	}

	private void begin(LivingEntity target) {
		start = golem.position();
		dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		total = golem.distanceTo(target) + 2;
		// 起冲即生成三只持械虚影 + 冲锋拖尾
		LMProxy.spawnCloneBurst(golem, target, lv);
		ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.SOUL_FIRE_FLAME);
		ChargeHelper.playChargeEasterEgg(golem);   // 与其它 AI 冲锋一致：起手播冲锋彩蛋音
	}

	@Override
	public boolean canContinueToUse() {
		return canContinueCharge(golem, start, total, ticks, ChargeHelper.MAX_TICKS, blocked);
	}

	@Override
	public void stop() {
		// 冲刺收尾：臂刃范围斩
		LMProxy.cloneBurstFinish(golem, lv);
		finishCharge(golem, KEY_ACTIVE);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (tickBackstep()) return;
		if (pendingChargeTarget != null) {
			LivingEntity t = pendingChargeTarget;
			pendingChargeTarget = null;
			ticks = 0;
			EarthquakeHelper.setBusy(golem, true);
			golem.setTransientFlag(KEY_ACTIVE, true);
			begin(t);
			return;
		}
		ticks++;
		if (ticks % 4 == 0) ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.SOUL_FIRE_FLAME);
		float cy = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(cy);
		golem.yBodyRot = cy;
		golem.yHeadRot = cy;
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			blocked = true;
			return;
		}
		// 撞到范围内敌人造成臂刃伤害 + 命中打击音（与其它冲锋一致）
		if (LMProxy.cloneBurstChargeHit(golem, lv, dir)) SkillSound.playChargeHit(golem);
	}
}

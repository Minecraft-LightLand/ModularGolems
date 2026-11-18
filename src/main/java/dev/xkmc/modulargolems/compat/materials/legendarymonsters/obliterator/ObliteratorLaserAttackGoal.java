package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMTeleportBackstepHelper;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 湮灭光束（Boss state 21 / 42 / 43）：t1 蓄力音、t5–22 吸入粒子、t23 开火，
 * 光束 duration=10。激光结束后（t33）沿激光方向左右两侧各一排、平行飞出的小型湮灭炸弹。
 * 二阶段起开火后有概率衔接四向湮灭激光。
 */
public class ObliteratorLaserAttackGoal extends BaseRangedAttackGoal {

	/** 蓄力音（Boss t1）。 */
	private static final int CHARGE_SOUND_TICK = 1;
	/** 吸入粒子区间（Boss t5–22）。 */
	private static final int CHARGE_PARTICLE_START = 5, CHARGE_PARTICLE_END = 22;
	/** 开火（Boss t23）。 */
	private static final int FIRE_TICK = 23;
	/** 激光结束后若目标超出此距离则瞬移到其身边（Boss t35）。 */
	private static final float TELEPORT_RANGE = 16.0F;
	private static final int TELEPORT_TICK = 35;
	/** 二阶段四向激光衔接：起手（t32）与发射（t46）。 */
	private static final int QUAD_CHARGE_TICK = 32, QUAD_FIRE_TICK = 46;
	private static final int QUAD_PARTICLE_START = 33;
	/** 整招结束 tick：带四向 / 不带四向。 */
	private static final int END_TICK_QUAD = 70, END_TICK_SINGLE = 50;

	private Entity beam;
	private boolean casting;
	private int castTicks;
	private boolean quadChain;
	private boolean quadRight;
	private float castYaw;
	/** 本次施法是否已经真正发射了光束（用于未发射就中断时退还冷却）。 */
	private boolean beamFired;

	public ObliteratorLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ObliteratorLaserModifier.COOLDOWN, ObliteratorLaserModifier.MIN_RANGE,
				ObliteratorLaserModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
		quadChain = false;
		beamFired = false;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || beam != null || super.canContinueToUse();
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
		// 蓄力/施法未真正发射就被中断（如目标丢失/被打断）：退还冷却，避免「听到音效却没放出来就进冷却」
		if (!beamFired) this.attackTime = golem.level().getGameTime();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (beam != null && beam.isRemoved()) beam = null;
		if (casting) {
			LivingEntity target = golem.getTarget();
			if (target == null || golem.isRemoved()) {
				casting = false;
			} else {
				castTicks++;
				golem.setYRot(castYaw);
				golem.yBodyRot = castYaw;
				golem.yHeadRot = castYaw;
				if (castTicks == CHARGE_SOUND_TICK) LMProxy.obliteratorLaserCharge(golem);
				if (castTicks >= CHARGE_PARTICLE_START && castTicks <= CHARGE_PARTICLE_END) LMProxy.obliteratorLaserChargeParticles(golem);
				if (castTicks == FIRE_TICK) {
					beam = LMProxy.obliteratorLaserFire(golem, target, lv, castYaw);
					beamFired = true;
					golem.specialAttackCoolDown = 20;
					quadChain = LMArmorHelper.obliteratorPhaseTwo(golem) && golem.getRandom().nextFloat() < ObliteratorLaserModifier.QUAD_CHANCE;
					if (quadChain) quadRight = golem.getRandom().nextBoolean();
				}
				// 小型炸弹由 AnnihilationBeamEntity 自身在 onAddedToWorld 里沿光束生成（无需这里额外处理）
				// 激光结束后：若目标过远且不是远程模式，瞬移到目标身边（Boss 的「瞬移至目标身边」）
				if (castTicks == TELEPORT_TICK && target != null && golem.distanceTo(target) > TELEPORT_RANGE && !golem.isInRangedMode()) {
					LMProxy.obliteratorTeleportBehind(golem, -3.0);
				}
				// 二阶段衔接四向激光
				if (quadChain && castTicks == QUAD_CHARGE_TICK) {
					LMProxy.quadLaserCharge(golem);
					LMProxy.obliteratorTeleportBehind(golem, 5.0);
				}
				if (quadChain && castTicks > QUAD_PARTICLE_START
					&& castTicks < QUAD_FIRE_TICK) LMProxy.obliteratorLaserChargeParticles(golem);
				if (quadChain && castTicks == QUAD_FIRE_TICK) LMProxy.spawnQuadLaser(golem, lv, quadRight, castYaw);
				if (quadChain && castTicks >= END_TICK_QUAD) casting = false;
				if (!quadChain && castTicks >= END_TICK_SINGLE) casting = false;
			}
		}
		if (beam != null) {
			var target = golem.getTarget();
			if (target != null) golem.getLookControl().setLookAt(target, 30, 90);
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTicks = 0;
		quadChain = false;
		beamFired = false;
		castYaw = (float) (Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()) * Mth.RAD_TO_DEG);
		golem.setYRot(castYaw);
		golem.yBodyRot = castYaw;
		golem.yHeadRot = castYaw;
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

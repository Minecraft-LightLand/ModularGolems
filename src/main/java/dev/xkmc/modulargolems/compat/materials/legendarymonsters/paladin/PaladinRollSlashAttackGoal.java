package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 翻滚斩（Boss state29）：照灾变「风暴旋冲」{@code ScyllaSpinChargeAttackGoal} 的旋转冲锋写法——
 * 位移方向独立于朝向，冲锋期间朝向持续旋转，因此看起来是边转边冲；
 * 沿途每 3 tick 留下灵魂柱，中途打出两次 360° 斩。
 */
public class PaladinRollSlashAttackGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "PaladinRollSlashing";
	/** 每 tick 累加的水平旋转角度（度）。 */
	private static final float SPIN_SPEED = 40.0f;
	private static final int SLASH1_TICK = 10;
	private static final int SLASH2_TICK = 24;
	private static final int PILLAR_INTERVAL = 3;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;
	private boolean slash1Done, slash2Done;
	private float spinYaw;
	/** 起手时锁定的冲锋朝向（度）：位移与灵魂柱都用它，不跟随旋转。 */
	private float castYaw;

	public PaladinRollSlashAttackGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	@Override
	public boolean canUse() {
		return canUseCharge(golem, ChargeHelper.findChargeTarget(golem, PaladinRollSlashModifier.MAX_RANGE),
				PaladinRollSlashModifier.MIN_RANGE, PaladinRollSlashModifier.MAX_RANGE);
	}
	@Override
	public void start() {
		LivingEntity target = ChargeHelper.findChargeTarget(golem, PaladinRollSlashModifier.MAX_RANGE);
		if (target == null) {
			blocked = true;
			return;
		}
		ticks = 0;
		blocked = false;
		slash1Done = slash2Done = false;
		spinYaw = 0;
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
		castYaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		LMProxy.paladinRollStart(golem);
		ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.SOUL);
		ChargeHelper.playChargeEasterEgg(golem);
	}

	@Override
	public boolean canContinueToUse() {
		return canContinueCharge(golem, start, total, ticks, ChargeHelper.MAX_TICKS, blocked);
	}

	@Override
	public void stop() {
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
		// start() 里目标可能已死、dir 未初始化：直接收尾，别推进位移
		if (dir == null) {
			blocked = true;
			return;
		}
		ticks++;
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			blocked = true;
			return;
		}
		// 旋转：位移走独立 dir，朝向叠加累计角（照风暴旋冲，必须用「基准 + 累计」）
		float prevSpin = spinYaw;
		spinYaw = (spinYaw + SPIN_SPEED) % 360.0f;
		float baseYaw = castYaw;
		golem.yBodyRotO = baseYaw + prevSpin;
		golem.yBodyRot = baseYaw + spinYaw;
		golem.setYRot(golem.yBodyRot);
		golem.yRotO = golem.yBodyRotO;
		golem.yHeadRot = golem.yBodyRot;
		golem.yHeadRotO = golem.yBodyRotO;
		// 沿途灵魂柱 + 撞击伤害
		if (ticks % PILLAR_INTERVAL == 0) LMProxy.paladinRollPillar(golem, lv, castYaw);
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		float atk = (float) golem.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (e.hurt(golem.damageSources().mobAttack(golem), atk)) {
				ChargeHelper.pushChargeTarget(golem, e, dir);
				SkillSound.playChargeHit(golem);
			}
		}
		if (!slash1Done && ticks >= SLASH1_TICK) {
			slash1Done = true;
			LMProxy.paladinRollSlash(golem, lv);
		}
		if (!slash2Done && ticks >= SLASH2_TICK) {
			slash2Done = true;
			LMProxy.paladinRollSlash(golem, lv);
		}
	}
}

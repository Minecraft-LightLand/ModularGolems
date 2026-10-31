package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.miauczel.legendary_monsters.Particle.custom.Circle;

import java.util.EnumSet;

/**
 * 浮云冲锋（对应 Cloud_Golem 的 charge，state 27/28/30）：
 * 只取冲刺与撞击，无前摇/后摇。撞击 5 格内目标，伤害 = 22 + 3% 目标最大生命，禁用盾牌。
 * 可连续冲锋两次（参考咒魂冲锋）。
 */
public class CloudChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "CloudCharging";
	public static final int MAX_DASH = 2;
	private static final int MAX_TICKS = 60;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;
	private int dashDone, maxDash;
	/** 段间等待期间暂存的下一段目标。 */
	private LivingEntity pendingDashTarget;

	public CloudChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		if (EarthquakeHelper.isBusy(golem)) return false;
		if (onCooldown(golem)) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		return golem.distanceTo(target) <= RANGE;
	}

	@Override
	public void start() {
		ticks = 0; blocked = false; dashDone = 0; maxDash = MAX_DASH;
		golem.getNavigation().stop();
		golem.setTransientFlag(KEY_ACTIVE, true);
		EarthquakeHelper.setBusy(golem, true);
		markUsed(golem);
		LivingEntity target = findTarget();
		if (target == null) {
			blocked = true;
			return;
		}
		// 目标太近先起后撤步，落地后由 tick 接手开始冲锋
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		ticks = 0;
		start = golem.position();
		dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		total = golem.distanceTo(target) + 4;
		setFacing();
		// 通用冲锋彩蛋音（每个冲锋技能都触发）
		ChargeHelper.playChargeEasterEgg(golem);
		// 冲锋释放音（Boss state30 tick3 的 PLAYER_ATTACK_SWEEP），每段冲锋一次
		SkillSound.playChargeOnly(golem, SoundEvents.PLAYER_ATTACK_SWEEP, 3.0F, 0.25F);
	}

	private void setFacing() {
		float yaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yRotO = yaw;
		golem.yBodyRotO = yaw;
		var tgt = golem.getTarget();
		if (tgt != null) golem.getLookControl().setLookAt(tgt, 30.0F, 30.0F);
		else { golem.yHeadRot = yaw; golem.yHeadRotO = yaw; }
	}

	@Override
	public boolean canContinueToUse() {
		return dashDone < maxDash && !blocked;
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
			// 后撤步刚落地：重新占锁并开始冲锋
			LivingEntity t = pendingChargeTarget;
			pendingChargeTarget = null;
			this.ticks = 0;
			EarthquakeHelper.setBusy(golem, true);
			golem.setTransientFlag(KEY_ACTIVE, true);
			begin(t);
			return;
		}
		if (tickDashGap()) {
			// 段间等待：原地不动，等无敌帧过去再起下一段
			if (dashGap == 0 && pendingDashTarget != null) {
				var t = pendingDashTarget;
				pendingDashTarget = null;
				if (t.isAlive() && golem.canAttack(t)) begin(t);
				else { dashDone = maxDash; blocked = true; }
			}
			return;
		}
		ticks++;
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			blocked = true;
			return;
		}
		setFacing();
		spawnChargeRing();
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		var src = golem.damageSources().mobAttack(golem);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = 22.0F + e.getMaxHealth() * 0.03F;
			boolean hit = e.hurt(src, dmg);
			GolemCombatHelper.breakShield(golem, e, src);
			// 撞到目标音（Boss state30 AreaAttack 命中时的 ANVIL_PLACE）
			if (hit) SkillSound.playChargeOnly(golem, SoundEvents.ANVIL_PLACE, 2.0F, 1.0F);
		}
		if (golem.position().distanceToSqr(start) >= total * total || ticks >= MAX_TICKS) {
			nextDash();
		}
	}

	private void nextDash() {
		dashDone++;
		if (dashDone >= maxDash) { blocked = true; return; }
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) {
			dashDone = maxDash;
			blocked = true;
			return;
		}
		// 先进入段间等待，tick 倒计时结束后再起下一段
		startDashGap();
		pendingDashTarget = target;
	}

	/** Boss 冲锋环：复刻 Cloud_Golem state28 冲刺每 tick 生成的竖直环（pitch=0，环面朝冲锋方向，
	 *  位于身体前方 1.5 格、高度 +1，GROW_THEN_SHRINK，scale 40）。 */
	private void spawnChargeRing() {
		// 复刻 Boss state28 冲刺时的单个竖直环（pitch=0，yaw=-getYRot()+180），位置在傀儡自身、半身高（不前伸）。
		float yaw = (float) Math.toRadians(-golem.getYRot() + 180);
		double x = golem.getX();
		double y = golem.getY() + golem.getBbHeight() * 0.5;
		double z = golem.getZ();
		if (golem.level() instanceof ServerLevel sl) {
			sl.sendParticles(new Circle.RingData(yaw, 0.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
					x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}

package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PoseidonRiptideChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "PoseidonRiptideCharging";
	private static final String KEY_TIME = "PoseidonRiptideChargeTime";

	private static final int MAX_TICKS = 60;
	private static final int COOLDOWN = PoseidonRiptideModifier.COOLDOWN;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public PoseidonRiptideChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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

		if (!golem.isInWaterRainOrBubble()) return false;
		long time = golem.level().getGameTime();
		if (onCooldown(golem)) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		double dist = golem.distanceTo(target);
		return dist <= RANGE;
	}

	@Override
	public void start() {
		LivingEntity target = findTarget();
		// 同 IgnisChargeGoal#start：canUse 与 start 之间目标可能已死
		if (target == null) {
			this.blocked = true;
			return;
		}
		this.ticks = 0;
		this.blocked = false;
		this.golem.getNavigation().stop();
		this.golem.setTransientFlag(KEY_ACTIVE, true);
		EarthquakeHelper.setBusy(this.golem, true);
		markUsed(golem);
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		this.start = golem.position();

		Vec3 diff = target.getEyePosition().subtract(golem.getEyePosition());
		Vec3 flat = new Vec3(diff.x, 0, diff.z);
		if (diff.y > 2.0 && flat.lengthSqr() > 1e-8) {
			this.dir = diff.normalize();
		} else if (flat.lengthSqr() > 1e-8) {
			this.dir = flat.normalize();
		} else {
			this.dir = diff.y >= 0 ? new Vec3(0, 1, 0) : new Vec3(0, -1, 0);
		}
		this.total = golem.distanceTo(target) + 2;
		golem.playSound(SoundEvents.TRIDENT_RIPTIDE_1, 1.0F, 1.0F);
		ChargeHelper.playChargeEasterEgg(golem);
		golem.setRiptideSpin(true);
		golem.fallDistance = 0;
	}

	@Override
	public void stop() {
		finishCharge(this.golem, KEY_ACTIVE);
		golem.setRiptideSpin(false);
	}

	@Override
	public boolean canContinueToUse() {
		return !blocked && ticks < MAX_TICKS && golem.position().distanceToSqr(start) < total * total;
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
		this.ticks++;
		golem.fallDistance = 0;
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float bonus = 1F;
		if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
			bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			e.hurt(golem.damageSources().mobAttack(golem), atk * bonus);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}

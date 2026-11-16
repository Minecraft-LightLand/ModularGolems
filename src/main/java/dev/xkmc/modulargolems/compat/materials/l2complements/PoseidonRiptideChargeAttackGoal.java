package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.util.SkillSound;
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

public class PoseidonRiptideChargeAttackGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "PoseidonRiptideCharging";
	private static final String KEY_TIME = "PoseidonRiptideChargeTime";

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public PoseidonRiptideChargeAttackGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	/** riptide 是水中/雨中冲锋，覆写掉基类的「必须落地」判定。 */
	@Override
	protected boolean canStartCharge(AbstractGolemEntity<?, ?> golem) {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		return golem.isInWaterRainOrBubble();
	}

	@Override
	public boolean canUse() {
		return canUseCharge(golem, ChargeHelper.findChargeTarget(golem, PoseidonRiptideModifier.MAX_RANGE), PoseidonRiptideModifier.MIN_RANGE, PoseidonRiptideModifier.MAX_RANGE);
	}

	@Override
	public void start() {
		LivingEntity target = ChargeHelper.findChargeTarget(golem, PoseidonRiptideModifier.MAX_RANGE);
		// 同 IgnisChargeAttackGoal#start：canUse 与 start 之间目标可能已死
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
		return canContinueCharge(golem, start, total, ticks, PoseidonRiptideModifier.MAX_TICKS, blocked);
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
			boolean chargeHit = e.hurt(golem.damageSources().mobAttack(golem), atk * bonus);
			if (chargeHit) {
				ChargeHelper.pushChargeTarget(golem, e, dir);
				SkillSound.playChargeHit(golem);
			}
		}
	}

}

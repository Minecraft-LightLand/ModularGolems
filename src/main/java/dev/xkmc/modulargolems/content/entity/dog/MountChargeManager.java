package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.impl.GolemImplBase;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mezz.jei.gui.input.MouseUtil.getX;

public class MountChargeManager {

	private final DogGolemEntity golem;

	// 骑乘冲锋状态。chargeRider 是身上那只傀儡的技能，chargeMount 是自身的，
	// 这样双傀儡叠罗汉时可以同时用两半的技能冲刺。
	ChargeHelper.Instance chargeRider, chargeMount;
	int chargeTotal = 0;
	double chargeMoved = 0;
	int chargeTicks;
	boolean chargeBlocked;
	boolean chargeTimeLimit;
	Vec3 chargeDir;
	boolean wasSprinting;

	private static final double RIDE_CHARGE_RANGE = 16;

	/**
	 * 骑乘冲锋的防刷闸门。各技能自身冷却长得多，这里只防止按住按键在两个技能
	 * 冷却同时结束时被反复触发。
	 */
	private static final long RIDE_CHARGE_INTERVAL = 20;
	private static final String LAST_CHARGE_KEY = "lastmountcharge";

	private final List<RideCooldownIndicator> rideIndicators;

	public MountChargeManager(DogGolemEntity golem) {
		this.golem = golem;
		rideIndicators = List.of(
				new RideCooldownIndicator(new RideCooldownIndicator.Entry(
						MGLangData.RIDE_CHARGE_READY::get, golem::getChargeCd, MGLangData.RIDE_CHARGE_CD::get)),
				new RideCooldownIndicator(new RideCooldownIndicator.Entry(
						MGLangData.RIDE_JUMP_READY::get, golem::getJumpCd, MGLangData.RIDE_JUMP_CD::get)));
	}

	void startMountCharge(ChargeHelper.Instance ins) {
		Vec3 dir = new Vec3(golem.getLookAngle().x, 0, golem.getLookAngle().z);
		if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
		double total = ins.modifier().getChargeDistance(ins);
		if (golem.getGolemController() != null) {
			LivingEntity t = golem.getGolemController().getTarget();
			if (t != null && (!t.isAlive() || !golem.predicateTarget(t))) t = null;
			if (t == null && golem.predicateTarget(golem.getTarget())) t = golem.getTarget();
			if (t != null) {
				dir = new Vec3(t.getX() - getX(), 0, t.getZ() - golem.getZ());
				if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
				if (ins.owner() != golem) total = Math.sqrt(t.distanceToSqr(golem)) + 2;
			}
		}
		startMountCharge(ins, dir.normalize(), total);
	}

	void startMountCharge(ChargeHelper.Instance ins, Vec3 dir, double total) {
		long lastCharge = golem.getPersistentData().getLong(LAST_CHARGE_KEY);
		if (golem.level().getGameTime() < lastCharge + RIDE_CHARGE_INTERVAL) return;
		golem.getPersistentData().putLong(LAST_CHARGE_KEY, golem.level().getGameTime());
		if (ins.owner() == golem) {
			this.chargeMount = ins;
		} else {
			this.chargeRider = ins;
		}
		this.chargeTicks = 0;
		this.chargeTotal = (int) Math.ceil(total);
		this.chargeMoved = 0;
		// 玩家手动操控，冲锋跟随视角，只由 tick 上限终止
		this.chargeTimeLimit = golem.getControllingPassenger() instanceof Player;
		this.chargeDir = dir;
		this.chargeBlocked = false;
		golem.getNavigation().stop();
		golem.entityData().set(DogGolemEntity.DATA_CHARGE_ACTIVE, true);
		golem.entityData().set(DogGolemEntity.DATA_CHARGE_DIR_X, (float) dir.x);
		golem.entityData().set(DogGolemEntity.DATA_CHARGE_DIR_Z, (float) dir.z);
		ins.modifier().setCharging(ins, true);
		ins.modifier().performCharge(ins);
		ins.modifier().onChargeStart(ins.owner());
	}

	public void tickMountCharge() {
		this.chargeTicks++;
		if (chargeTicks > ChargeHelper.MAX_TICKS || chargeBlocked) {
			endMountCharge();
			return;
		}
		Vec3 dir = chargeDir;
		if (chargeTimeLimit) {
			Vec3 look = new Vec3(golem.getLookAngle().x, 0, golem.getLookAngle().z);
			if (look.lengthSqr() > 1e-8) dir = look.normalize();
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			chargeBlocked = true;
			endMountCharge();
			return;
		}
		var active = chargeRider != null ? chargeRider : chargeMount;
		if (active != null && active.modifier().hasChargeParticle() && golem.level().getRandom().nextInt(3) == 0) {
			ChargeHelper.spawnChargeParticles(golem, dir, active.modifier().getChargeParticle(), active.modifier().hasChargeSmoke());
		}
		chargeMoved += golem.getAttributeValue(Attributes.MOVEMENT_SPEED) * ChargeHelper.MOVE_SPEED_MULT;
		if (chargeMoved >= chargeTotal && !chargeTimeLimit) {
			endMountCharge();
			return;
		}
		if (chargeRider != null) chargeRider.modifier().tickCharge(chargeRider);
		if (chargeMount != null) chargeMount.modifier().tickCharge(chargeMount);
		AbstractGolemEntity<?, ?> attacker = chargeRider != null ? chargeRider.owner() : golem;
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!attacker.predicateTarget(e)) continue;
			float dmg = 0;
			if (chargeRider != null) {
				dmg += chargeRider.modifier().chargeDamage(chargeRider, e);
			}
			if (chargeMount != null) {
				dmg += chargeMount.modifier().chargeDamage(chargeMount, e);
			}
			if (dmg > 0) {
				// 只在真正造成伤害时才触发 onChargeTouch：受击无敌帧会让 hurt 返回 false，
				// 否则贴着目标冲锋时每 tick 都会播一次撞击音（听起来像连成一片）。
				boolean hit = e.hurt(attacker.damageSources().mobAttack(attacker), dmg);
				if (hit) {
					if (chargeRider != null) chargeRider.modifier().onChargeTouch(chargeRider, e);
					if (chargeMount != null) chargeMount.modifier().onChargeTouch(chargeMount, e);
				}
			}
		}
	}

	private void endMountCharge() {
		if (chargeRider != null) chargeRider.modifier().endCharge(chargeRider);
		if (chargeMount != null) chargeMount.modifier().endCharge(chargeMount);
		if (chargeRider != null) {
			chargeRider.modifier().setCharging(chargeRider, false);
			chargeRider = null;
		}
		if (chargeMount != null) {
			chargeMount.modifier().setCharging(chargeMount, false);
			chargeMount = null;
		}
		this.chargeTotal = 0;
		this.chargeMoved = 0;
		golem.entityData().set(DogGolemEntity.DATA_CHARGE_ACTIVE, false);
	}

	private int calcJumpCd() {
		int ans = -1;
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof EarthquakeHelper.Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long left = last + m.getCoolDown(golem, e.getValue()) - time;
				ans = Math.max(ans, (int) Math.max(0, Math.min(left, Integer.MAX_VALUE)));
			}
		}
		return ans;
	}

	private int calcChargeCd() {
		int ans = -1;
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof ChargeHelper.Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long left = last + m.getCooldown(golem, e.getValue()) - time;
				ans = Math.max(ans, (int) Math.max(0, Math.min(left, Integer.MAX_VALUE)));
			}
		}
		return ans;
	}


	/**
	 * AI 骑乘冲锋：AI 傀儡先从自身、再从坐骑的修饰符里挑一个冲锋技能，用坐骑的身体跑。
	 */
	private void tickRiddenCharge() {
		if (golem.jumpAttack != null || golem.jumpAttackDelay > 0 || !golem.onGround()) return;
		var rider = golem.getGolemController();
		if (rider == null || rider.isInRangedMode() || golem.isInRangedMode()) return;
		LivingEntity target = rider.getTarget();
		if (target == null || !target.isAlive() || !golem.predicateTarget(target)) {
			target = golem.getTarget();
		}
		if (target == null || !target.isAlive() || !golem.predicateTarget(target)) return;
		double dist = golem.distanceTo(target);
		if (dist <= 2 || dist > RIDE_CHARGE_RANGE) return;
		var all = findValidCharges(rider);
		all.addAll(findValidCharges(golem));
		if (all.isEmpty()) return;
		var ins = all.get(golem.getRandom().nextInt(all.size()));
		ins.addCD();
		startMountCharge(ins);
	}

	private List<ChargeHelper.Instance> findValidCharges(AbstractGolemEntity<?, ?> golem) {
		List<ChargeHelper.Instance> ans = new ArrayList<>();
		if (golem.hasFlag(GolemFlags.PASSIVE)) return ans;
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof ChargeHelper.Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long cd = m.getCooldown(golem, e.getValue());
				if ((last + cd < time || last > time) && m.canCharge(golem, e.getValue())) {
					ans.add(new ChargeHelper.Instance(golem, m, e.getValue()));
				}
			}
		}
		return ans;
	}

	private void updateRideIndicators(Player player) {
		RideCooldownIndicator.render(player, rideIndicators);
	}


	public interface View extends GolemImplBase {

		MountChargeManager manager();

		default void chargeDataSync() {
			setJumpCd(manager().calcJumpCd());
			setChargeCd(manager().calcChargeCd());
		}

		default Vec3 getChargeDirClient() {
			Vec3 d = new Vec3(entityData().get(DogGolemEntity.DATA_CHARGE_DIR_X), 0, entityData().get(DogGolemEntity.DATA_CHARGE_DIR_Z));
			if (d.lengthSqr() < 1e-8) d = new Vec3(0, 0, 1);
			return d.normalize();
		}

		default void controlledChargeTick(Player player) {
			var self = asGolem();
			manager().updateRideIndicators(player);
			// 骑乘冲锋由客户端权威：服务端不回推位置，本地实例必须跑同样的位移，
			// 否则骑乘者眼前傀儡会站着不动
			if (entityData().get(DogGolemEntity.DATA_CHARGE_ACTIVE)) {
				Vec3 d = new Vec3(self.getLookAngle().x, 0, self.getLookAngle().z);
				if (d.lengthSqr() < 1e-8) d = getChargeDirClient();
				d = d.normalize();
				ChargeHelper.moveOneTick(self, d);
				// 正在跑的技能此时已在冷却中，必须不带冷却过滤来反查
				var ins = ChargeHelper.findRunningInstance(self);
				if (ins != null && ins.modifier().hasChargeParticle() && self.level().getRandom().nextInt(2) == 0) {
					ChargeHelper.spawnChargeParticles(self, d, ins.modifier().getChargeParticle(), ins.modifier().hasChargeSmoke());
				}
			}
			boolean sprint = player.isSprinting() || DogRideClient.isSprintKeyDown();
			if (sprint && !manager().wasSprinting) {
				this.executeRidersCharge();
			}
			manager().wasSprinting = sprint;
		}


		default boolean isMountCharging() {
			return manager().chargeRider != null || manager().chargeMount != null;
		}

		@Nullable
		default ChargeHelper.Instance getChargeRider() {
			return manager().chargeRider;
		}

		@Nullable
		default ChargeHelper.Instance getChargeMount() {
			return manager().chargeMount;
		}

		default void executeRidersCharge() {
			var ins = ChargeHelper.findInstance(asGolem());
			if (ins == null) return;
			ModularGolems.HANDLER.toServer(DogSkillToServer.of(ins));
		}

		default int getJumpCd() {
			return entityData().get(DogGolemEntity.DATA_JUMP_CD);
		}

		default void setJumpCd(int cd) {
			entityData().set(DogGolemEntity.DATA_JUMP_CD, cd);
		}

		default int getChargeCd() {
			return entityData().get(DogGolemEntity.DATA_CHARGE_CD);
		}

		default void setChargeCd(int cd) {
			entityData().set(DogGolemEntity.DATA_CHARGE_CD, cd);
		}

	}
}
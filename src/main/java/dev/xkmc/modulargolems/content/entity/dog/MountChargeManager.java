package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.impl.GolemImplBase;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 骑乘冲锋 / 大跳的共用状态机。犬形和大型傀儡共用这一份实现，行为完全一致：
 * <ul>
 *   <li>位移由客户端本地预测（骑乘实体服务端不回推位置），服务端只做伤害、冷却和结束判定；</li>
 *   <li>大跳由客户端本地起跳，服务端在落点结算震地；</li>
 *   <li>冷却写进同步字段，客户端只读，HUD 才能显示倒计时。</li>
 * </ul>
 * 具体实体通过 {@link View} 提供自己的同步字段和跳跃状态。
 */
public class MountChargeManager<T extends AbstractGolemEntity<?, ?> & MountChargeManager.View> {

	private final T golem;

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

	public MountChargeManager(T golem) {
		this.golem = golem;
		rideIndicators = List.of(
				new RideCooldownIndicator(golem::getChargeCd),
				new RideCooldownIndicator(golem::getJumpCd));
	}

	public void startMountCharge(ChargeHelper.Instance ins) {
		Vec3 dir = new Vec3(golem.getLookAngle().x, 0, golem.getLookAngle().z);
		if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
		double total = ins.modifier().getChargeDistance(ins);
		AbstractGolemEntity<?, ?> controller = ins.owner() != golem ? ins.owner() : golem.getGolemController();
		if (controller != null) {
			LivingEntity t = controller.getTarget();
			if (t != null && (!t.isAlive() || !golem.predicateTarget(t))) t = null;
			if (t == null && golem.getTarget() != null && golem.predicateTarget(golem.getTarget())) t = golem.getTarget();
			if (t != null) {
				dir = new Vec3(t.getX() - golem.getX(), 0, t.getZ() - golem.getZ());
				if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
				if (ins.owner() != golem) total = Math.sqrt(t.distanceToSqr(golem)) + 2;
			}
		}
		startMountCharge(ins, dir.normalize(), total);
	}

	public void startMountCharge(ChargeHelper.Instance ins, Vec3 dir, double total) {
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
		golem.setChargeActive(true);
		golem.setChargeDir((float) dir.x, (float) dir.z);
		ins.modifier().setCharging(ins, true);
		ins.modifier().performCharge(ins);
		ins.modifier().onChargeStart(ins.owner());
	}

	public void startMountJump(EarthquakeHelper.Instance ins) {
		if (golem.getJumpAttack() != null) return;
		golem.setJumpAttack(ins);
		golem.setJumpDelay(10);
		ins.addCD();
		// 默认由客户端本地预测负责起跳；技能显式声明要服务端执行时走这里
		if (ins.modifier().useServerJump()) {
			ins.modifier().performJump(golem, ins.lv());
		}
	}

	private void tickMountCharge() {
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
					// 撞击推力（击退 + 上抬），与 AI 冲锋路径同一个来源
					ChargeHelper.pushChargeTarget(attacker, e, dir);
					// 所有骑乘冲锋共用同一声命中音效，和 AI 冲锋路径保持一致
					SkillSound.playChargeHit(attacker);
					if (chargeRider != null) chargeRider.modifier().onChargeTouch(chargeRider, e);
					if (chargeMount != null) chargeMount.modifier().onChargeTouch(chargeMount, e);
				}
			}
		}
	}

	/**
	 * 大跳落地结算。和冲锋一样放服务端；客户端只负责本地起跳。
	 */
	private void tickMountJump() {
		EarthquakeHelper.Instance jump = golem.getJumpAttack();
		if (jump == null) return;
		int delay = golem.getJumpDelay();
		if (delay > 0) {
			golem.setJumpDelay(delay - 1);
			return;
		}
		if (!golem.onGround()) return;
		jump.modifier().performEarthQuake(golem, jump.lv());
		jump.modifier().onEarthQuakeLanded(golem, jump.lv());
		// 落点计入冲锋技能冷却，否则冲锋会看见刚跳到的目标，一落地就冲过去
		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);
		golem.setJumpAttack(null);
	}

	public void tickCharge() {
		// 骑乘冲锋由服务端权威推进（伤害/结束判定都在这里），漏掉这个调用会让
		// DATA_CHARGE_ACTIVE 永远清不掉 → 客户端一直向前冲且没有伤害。
		golem.setJumpCd(calcJumpCd());
		golem.setChargeCd(calcChargeCd());
		tickMountJump();
		if (chargeRider != null || chargeMount != null) {
			tickMountCharge();
		} else if (!golem.hasPlayerPassenger() && hasGolemRider()) {
			// 玩家骑着时技能交给玩家，AI 不再自行为背上的傀儡放冲锋
			tickRiddenCharge();
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
		golem.setChargeActive(false);
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
	private boolean hasGolemRider() {
		for (var e : golem.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?>) return true;
		}
		return false;
	}

	private void tickRiddenCharge() {
		if (golem.getJumpAttack() != null || golem.getJumpDelay() > 0 || !golem.onGround()) return;
		List<AbstractGolemEntity<?, ?>> riders = new ArrayList<>();
		for (var e : golem.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> rider && rider != golem) {
				riders.add(rider);
			}
		}
		if (riders.isEmpty() || golem.isInRangedMode()) return;
		LivingEntity target = null;
		for (var rider : riders) {
			var t = rider.getTarget();
			if (t != null && t.isAlive() && golem.predicateTarget(t)) {
				target = t;
				break;
			}
		}
		if (target == null || !target.isAlive() || !golem.predicateTarget(target)) {
			target = golem.getTarget();
		}
		if (target == null || !target.isAlive() || !golem.predicateTarget(target)) return;
		double dist = golem.distanceTo(target);
		if (dist <= 2 || dist > RIDE_CHARGE_RANGE) return;
		List<ChargeHelper.Instance> all = new ArrayList<>();
		for (var rider : riders) {
			if (!rider.isInRangedMode()) all.addAll(findValidCharges(rider));
		}
		if (!golem.isInRangedMode()) all.addAll(findValidCharges(golem));
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
		RideCooldownIndicator.render(player, rideIndicators, golem.rideCooldownPing());
	}

	public interface View extends GolemImplBase {

		MountChargeManager<?> chargeManager();

		void setChargeActive(boolean active);

		boolean isChargeActive();

		void setChargeDir(float x, float z);

		float getChargeDirX();

		float getChargeDirZ();

		int getJumpCd();

		void setJumpCd(int cd);

		int getChargeCd();

		void setChargeCd(int cd);

		@Nullable EarthquakeHelper.Instance getJumpAttack();

		void setJumpAttack(@Nullable EarthquakeHelper.Instance ins);

		int getJumpDelay();

		void setJumpDelay(int delay);

		default SoundEvent rideCooldownPing() {
			return SoundEvents.WOLF_GROWL;
		}

		default Vec3 getChargeDirClient() {
			Vec3 d = new Vec3(getChargeDirX(), 0, getChargeDirZ());
			if (d.lengthSqr() < 1e-8) d = new Vec3(0, 0, 1);
			return d.normalize();
		}

		default void controlledChargeTick(Player player) {
			var self = asGolem();
			chargeManager().updateRideIndicators(player);
			// 骑乘冲锋由客户端权威：服务端不回推位置，本地实例必须跑同样的位移，
			// 否则骑乘者眼前傀儡会站着不动
			if (isChargeActive()) {
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
			if (sprint && !chargeManager().wasSprinting) {
				this.executeRidersCharge();
			}
			chargeManager().wasSprinting = sprint;
		}

		default boolean isMountCharging() {
			return chargeManager().chargeRider != null || chargeManager().chargeMount != null;
		}

		@Nullable
		default ChargeHelper.Instance getChargeRider() {
			return chargeManager().chargeRider;
		}

		@Nullable
		default ChargeHelper.Instance getChargeMount() {
			return chargeManager().chargeMount;
		}

		default void executeRidersCharge() {
			// 客户端只发"我要冲锋"；用坐骑还是乘客的哪只冲锋交给服务端按真实冷却挑
			ModularGolems.HANDLER.toServer(DogSkillToServer.autoCharge());
		}

	}
}

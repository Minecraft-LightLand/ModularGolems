package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChargeHelper {

	public static final int MAX_TICKS = 60;

	/**
	 * 多段冲锋的段间延迟（tick）。原版受击无敌帧是 20 tick，段间隔短于它时下一段会被无敌帧吞掉，
	 * 所以每段之间至少停这么久。所有冲锋技能共用这一个值。
	 */
	public static final int DASH_GAP = 5;
	public static final double MOVE_SPEED_MULT = 3;
	public static final double STEP_HEIGHT = 1.0;

	/**
	 * 冲锋能扑到多高的目标，对应 {@link Modifier#getMaxTargetHeight}。按体型比例而非固定常数，
	 * 三种体型才不会失配。
	 */
	public static final double HEIGHT_REACH_RATIO = 0.25;

	/**
	 * 冲锋期间保留的伤害比例，共享常量，保证 tooltip 与实际减伤不会各说各话。
	 */
	public static final float CHARGE_DAMAGE_MULT = 0.2F;

	/**
	 * tooltip 里显示的冲锋减伤，如 {@code 80%}。
	 */
	public static MutableComponent chargeReduction() {
		return Component.literal(Math.round((1 - CHARGE_DAMAGE_MULT) * 100) + "%")
				.withStyle(ChatFormatting.AQUA);
	}

	/**
	 * 冲锋技能共用的彩蛋，避免每个技能各写一遍。在冲锋开始处调用一次；多段冲锋按段数调用。
	 */
	public static void playChargeEasterEgg(AbstractGolemEntity<?, ?> golem) {
		SkillSound.playChargeEasterEgg(golem, 1.0F, 1.0F);
	}

	@Nullable
	public static Instance findInstance(AbstractGolemEntity<?, ?> golem) {
		if (golem.hasFlag(GolemFlags.PASSIVE)) return null;
		List<Instance> list = new ArrayList<>();
		long time = golem.level().getGameTime();
		collect(list, golem, time);
		if (!list.isEmpty()) {
			return list.get(golem.getRandom().nextInt(list.size()));
		}
		return null;
	}

	/**
	 * 同 {@link #findInstance} 但忽略冷却。只用于反查正在跑的冲锋属于哪个技能——起手已写入冷却，
	 * 此时被冷却过滤掉的正是它自己。
	 */
	public static Instance findRunningInstance(AbstractGolemEntity<?, ?> golem) {
		if (golem.hasFlag(GolemFlags.PASSIVE)) return null;
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof Modifier m && e.getValue() >= 1 && m.canCharge(golem, e.getValue())) {
				return new Instance(golem, m, e.getValue());
			}
		}
		return null;
	}

	/**
	 * 偏离冲锋线多远仍算被撞到。各冲锋 Goal 造成伤害时用的就是这个 inflate，
	 * 保证「选出来的就是这次真会打到的」。
	 */
	private static final double PATH_MARGIN = 0.5;

	/**
	 * 选冲锋目标：范围内选「冲过去能撞到最多其他目标」的那个。冲锋扫的是一条走廊而不是一个点，
	 * 所以对上群比任何单一距离都值钱。
	 * <p>
	 * 锁定目标只用于平局裁决，人群里不会输给任何人，但平局时仍然优先。
	 * <p>
	 * 候选必须先有视野：冲锋是直线突进，若墙上/隔壁房间里的敌人也被算进来，
	 * {@link #countOnPath} 反而会因为「墙后一堆人」而更偏好朝墙冲，傀儡只会一头撞墙。
	 */
	@Nullable
	public static LivingEntity findChargeTarget(AbstractGolemEntity<?, ?> golem, double range) {
		List<LivingEntity> candidates = new ArrayList<>();
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(range))) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (golem.distanceToSqr(e) > range * range) continue;
			// 用 golem.hasLineOfSight 而非直接射线，以保留 SEE_THROUGH 傀儡的穿墙索敌能力。
			if (!golem.hasLineOfSight(e)) continue;
			candidates.add(e);
		}
		if (candidates.isEmpty()) return null;
		LivingEntity locked = golem.getTarget();
		Vec3 start = golem.getBoundingBox().getCenter();
		LivingEntity best = null;
		int bestHits = -1;
		for (var c : candidates) {
			int hits = countOnPath(start, c, candidates);
			if (hits > bestHits || (hits == bestHits && c == locked && best != locked)) {
				bestHits = hits;
				best = c;
			}
		}
		return best;
	}

	/**
	 * 直冲到 end 会经过多少其他实体。不计候选自身，读数才是「能对上多少人」而不是恒为 1。
	 */
	private static int countOnPath(Vec3 start, LivingEntity end, List<LivingEntity> pool) {
		Vec3 stop = end.getBoundingBox().getCenter();
		int hits = 0;
		for (var e : pool) {
			if (e == end) continue;
			if (e.getBoundingBox().inflate(PATH_MARGIN).clip(start, stop).isPresent()) hits++;
		}
		return hits;
	}

	private static void collect(List<Instance> list, AbstractGolemEntity<?, ?> golem, long time) {
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				if ((last + m.getCooldown(golem, e.getValue()) < time || last > time) &&
						m.canCharge(golem, e.getValue())) {
					list.add(new Instance(golem, m, e.getValue()));
				}
			}
		}
	}

	public static boolean moveOneTick(AbstractGolemEntity<?, ?> golem, Vec3 dir) {
		return moveOneTick(golem, dir, 1.0);
	}

	/**
	 * @param climbBoost 放大这一步的向上分量。斜向冲锋只有一小部分速度用于上升，
	 *                   真正爬升的技能靠它抬升得比水平速度看起来更快。
	 */
	public static boolean moveOneTick(AbstractGolemEntity<?, ?> golem, Vec3 dir, double climbBoost) {
		double speed = golem.getAttributeValue(Attributes.MOVEMENT_SPEED) * MOVE_SPEED_MULT;
		Vec3 step = dir.scale(speed);
		if (climbBoost != 1.0 && step.y > 0) step = new Vec3(step.x, step.y * climbBoost, step.z);
		AABB next = golem.getBoundingBox().move(step);
		if (hasCollisions(golem, next)) {
			double dy = STEP_HEIGHT;
			boolean lifted = false;
			if (dy > 0) {
				AABB up = next.move(0, dy, 0);
				if (!hasCollisions(golem, up) && hasSupport(golem, up)) {
					step = step.add(0, dy, 0);
					next = up;
					lifted = true;
				}
			}
			if (!lifted) return false;
		}
		if (step.y < 0 && golem.onGround()) {
			Vec3 flat = new Vec3(step.x, 0, step.z);
			if (!hasCollisions(golem, golem.getBoundingBox().move(flat))) step = flat;
		}
		golem.setPos(golem.position().add(step));
		golem.setDeltaMovement(0, golem.onGround() ? 0 : golem.getDeltaMovement().y, 0);
		float yaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		// 只锁身体朝向；头部解放——有目标就朝目标（含俯仰），否则跟随身体。
		// 这样冲锋途中仍能抬头/低头用激光等按视线结算的技能命中目标。
		var tgt = golem.getTarget();
		if (tgt != null) golem.getLookControl().setLookAt(tgt, 30.0F, 30.0F);
		else golem.yHeadRot = yaw;
		return true;
	}

	private static boolean hasCollisions(AbstractGolemEntity<?, ?> golem, AABB aabb) {
		return golem.level().getBlockCollisions(golem, aabb).iterator().hasNext();
	}

	private static boolean hasSupport(AbstractGolemEntity<?, ?> golem, AABB aabb) {
		AABB feet = aabb.move(0, -0.2, 0).inflate(-0.15, 0, -0.15);
		return golem.level().getBlockCollisions(golem, feet).iterator().hasNext();
	}

	public static void spawnChargeParticles(AbstractGolemEntity<?, ?> golem, Vec3 dir, ParticleOptions type) {
		spawnChargeParticles(golem, dir, type, false);
	}

	public static void spawnChargeParticles(AbstractGolemEntity<?, ?> golem, Vec3 dir, ParticleOptions type, boolean smoke) {
		// 无拖尾的技能传 null，烟雾是单独的开关
		if (type == null && !smoke) return;
		Vec3 right = new Vec3(-dir.z, 0, dir.x).normalize();
		var rand = golem.level().getRandom();
		Vec3 center = golem.position().add(dir.scale(1.2)).add(0, golem.getBbHeight() * 0.35, 0);
		int n = 12;
		for (int i = 0; i < n && type != null; i++) {
			double ang = i * 2 * Math.PI / n + rand.nextDouble() * 0.5;
			Vec3 radial = right.scale(Math.cos(ang)).add(0, Math.sin(ang) * 0.6, 0).scale(1.0);
			Vec3 pos = center.add(radial);
			addParticle(golem, type,
					pos.x, pos.y, pos.z,
					dir.x * 0.3 + rand.nextGaussian() * 0.05,
					0.06 + rand.nextGaussian() * 0.03,
					dir.z * 0.3 + rand.nextGaussian() * 0.05);
		}
		if (!smoke) return;
		for (int i = 0; i < 4; i++) {
			Vec3 pos = golem.position().add(0, rand.nextDouble() * golem.getBbHeight(), 0);
			addParticle(golem, ParticleTypes.SMOKE,
					pos.x, pos.y, pos.z,
					-dir.x * 0.5 + rand.nextGaussian() * 0.05,
					0.3 + rand.nextGaussian() * 0.1,
					-dir.z * 0.5 + rand.nextGaussian() * 0.05);
		}
	}

	private static void addParticle(AbstractGolemEntity<?, ?> golem, ParticleOptions type,
			double x, double y, double z, double dx, double dy, double dz) {
		if (golem.level() instanceof ServerLevel sl) {
			sl.sendParticles(type, x, y, z, 1, dx, dy, dz, 0);
		} else {
			golem.level().addParticle(type, x, y, z, dx, dy, dz);
		}
	}
	/**
	 * 目标是否低到能被自身冲锋技能扑倒。爬升型技能会抬高自己的 {@link Modifier#getMaxTargetHeight}。
	 * 没有冲锋技能时不拦截。
	 */
	public static boolean isTargetReachable(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		boolean any = false;
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (!(e.getKey() instanceof Modifier m) || e.getValue() < 1) continue;
			any = true;
			if (m.isTargetInReach(golem, target, e.getValue())) return true;
		}
		return !any;
	}

	/**
	 * 把自身所有冲锋技能置入冷却。跳跃落地时调用，否则傀儡会立刻从刚跳到的地方冲走：
	 * 跳跃替冲锋技能交掉了冷却。
	 */
	public static void addChargeCooldown(AbstractGolemEntity<?, ?> golem) {
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (!(e.getKey() instanceof Modifier m) || e.getValue() < 1) continue;
			golem.getPersistentData().putLong(m.self().getID() + ":timestamp", golem.level().getGameTime());
		}
	}

	public record Instance(AbstractGolemEntity<?, ?> owner, Modifier modifier, int lv) {

		public boolean isValid() {
			var time = owner().level().getGameTime();
			long last = owner().getPersistentData().getLong(modifier().self().getID() + ":timestamp");
			return last + modifier().getCooldown(owner(), lv()) < time || last > time;
		}

		public void addCD() {
			owner().getPersistentData().putLong(modifier().self().getID() + ":timestamp", owner().level().getGameTime());
		}

	}

	public interface Modifier {

		/**
		 * 冲锋拖尾粒子，不留拖尾则传 null。不设默认值：共用粒子在各种体型上都显得平庸，
		 * 反而和自带粒子的技能打架，要拖尾就自己指定。
		 */
		default ParticleOptions getChargeParticle() {
			return null;
		}

		default boolean hasChargeSmoke() {
			return false;
		}

		default boolean hasChargeParticle() {
			return false;
		}

		default void performCharge(Instance ins) {
		}

		/**
		 * 冲锋开始时调用，让共用彩蛋每段只播一次。
		 */
		default void onChargeStart(AbstractGolemEntity<?, ?> golem) {
			playChargeEasterEgg(golem);
		}

		default void setCharging(Instance ins, boolean on) {
		}

		default void tickCharge(Instance ins) {
		}

		default void onChargeTouch(Instance ins, LivingEntity target) {
		}

		default void endCharge(Instance ins) {
		}

		default boolean canCharge(AbstractGolemEntity<?, ?> golem, int lv) {
			return true;
		}

		/**
		 * 以体型高度为比例，技能最多能扑到多高。按体型算才能让同一个数值在金属、人形、犬形
		 * 三种截然不同的高度下都合理；爬升型技能会抬高这个值。
		 */
		default double getMaxTargetHeight(AbstractGolemEntity<?, ?> golem, int lv) {
			return golem.getBbHeight() * HEIGHT_REACH_RATIO;
		}

		/**
		 * 冲锋能否够到目标高度。水平方向由各技能自己的射程负责，这里只比垂直：
		 * 贴地冲锋永远扑不到悬空目标，爬升型技能则抬高上限。
		 * raise the ceiling with {@link #getMaxTargetHeight}.
		 */
		default boolean isTargetInReach(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
			double reach = golem.getBbHeight() + Math.max(getMaxTargetHeight(golem, lv), 0);
			return target.getBoundingBox().minY <= golem.getY() + reach;
		}

		default float chargeDamage(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
			return GolemCombatHelper.getAttackDamage(golem);
		}

		default float chargeDamage(Instance ins, LivingEntity target) {
			return GolemCombatHelper.getAttackDamage(ins.owner());
		}

		default int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
			return 200;
		}

		default int getCooldown(Instance ins) {
			return getCooldown(ins.owner(), ins.lv());
		}

		default double getChargeDistance(AbstractGolemEntity<?, ?> golem, int lv) {
			return 8;
		}

		default double getChargeDistance(Instance ins) {
			return getChargeDistance(ins.owner(), ins.lv());
		}

		default GolemModifier self() {
			return Wrappers.cast(this);
		}

	}

}

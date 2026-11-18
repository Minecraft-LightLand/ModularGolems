package dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * 传奇怪物护甲特性共用的投射物追踪工具：
 * <p>
 * LM 的炸弹 / 烈波都没有制导能力，这里每 tick 把速度方向掰向目标（保持恒定速度不衰减）。
 * {@code distribute=true} 时每枚投射物单独分配一个目标（照遗魂沙暴：目标不足时多余的不追踪）。
 */
public class LMArmorEffects {

	/** 追踪扫描半径（格）。 */
	private static final int SEARCH = 64;
	/** 单包实体持久化数据里存「分配到的目标」的 key。 */
	private static final String TARGET_KEY = "LMArmorTrackTarget";

	/** 通用投射物（湮灭炸弹）追踪时固定不变的初始速度，存到实体持久化数据里避免逐 tick 衰减。 */
	private static final String SPEED_TAG = "lm_armor_steer_speed";
	/** 单道烈波「已命中、停止追踪」的持久化标记。 */
	private static final String WAVE_HIT_FLAG = "wave_hit";
	/** 烈波命中判定半径（水平，格）。 */
	private static final double WAVE_HIT_DIST = 1.0;

	// ===== 投射物追踪 =====

	/**
	 * 让傀儡周围 {@code cls} 类型的已发射投射物追踪目标。
	 * <p>
	 * {@code distribute=false}：全部追主目标（用于湮灭炸弹）。
	 * {@code distribute=true}：每把投射物分配一个不同的目标（照遗魂沙暴：目标不足时多余的投射物不追踪）；
	 * 已分配且仍有效的保留，失效的重新分。
	 */
	public static <T extends Entity> void steer(AbstractGolemEntity<?, ?> golem, Class<T> cls, boolean distribute,
			Predicate<T> owned) {
		if (golem.level().isClientSide()) return;
		LivingEntity main = golem.getTarget();
		if (!distribute && (main == null || !main.isAlive())) return;
		List<T> list = golem.level().getEntitiesOfClass(cls, golem.getBoundingBox().inflate(SEARCH), owned);
		if (list.isEmpty()) return;
		List<LivingEntity> pool = distribute ? collectTargets(golem, list.size()) : List.of();
		List<LivingEntity> taken = new ArrayList<>();
		if (distribute) {
			for (T e : list) {
				LivingEntity t = resolve(golem, e);
				if (t != null && t.isAlive() && GolemTargetUtil.canTarget(golem, t)) taken.add(t);
			}
		}
		for (T e : list) {
			LivingEntity target;
			if (distribute) {
				target = resolve(golem, e);
				if (target == null || !target.isAlive() || !GolemTargetUtil.canTarget(golem, target)) {
					LivingEntity free = firstFree(pool, taken);
					if (free != null) {
						target = free;
						taken.add(free);
						store(e, free);
					} else {
						// 目标不足：多余的投射物不追踪（照遗魂沙暴：只有一个目标时只有一把去追）
						target = null;
					}
				}
			} else {
				target = main;
			}
			if (target == null || !target.isAlive()) continue;
			steerOne(e, target);
		}
	}

	/**
	 * 烈波的地面追踪：照灾变远古金属护腿的冲击波 —— 只在地面（只改水平方向，Y 不动）追踪目标，
	 * 且一旦靠近目标（视为命中）就停止追踪，让烈波继续直飞、自然消失。
	 * <p>
	 * 每道烈波分配一个不同目标（照遗魂沙暴：目标不足时多余的烈波不追踪）。
	 */
	public static <T extends Entity> void steerWaves(AbstractGolemEntity<?, ?> golem, Class<T> cls, Predicate<T> owned) {
		if (golem.level().isClientSide()) return;
		List<T> list = golem.level().getEntitiesOfClass(cls, golem.getBoundingBox().inflate(SEARCH), owned);
		if (list.isEmpty()) return;
		LivingEntity main = golem.getTarget();
		List<LivingEntity> pool = collectTargets(golem, list.size());
		List<LivingEntity> taken = new ArrayList<>();
		for (T e : list) {
			if (e.getPersistentData().getBoolean(WAVE_HIT_FLAG)) continue;
			LivingEntity target = resolve(golem, e);
			if (target == null || !target.isAlive() || !GolemTargetUtil.canTarget(golem, target)) {
				LivingEntity free = firstFree(pool, taken);
				if (free != null) {
					target = free;
					taken.add(free);
					store(e, free);
				} else {
					// 目标不足：多余的烈波不追踪（照遗魂沙暴）
					target = null;
				}
			}
			if (target == null || !target.isAlive()) continue;
			double dx = target.getX() - e.getX();
			double dz = target.getZ() - e.getZ();
			double horizSqr = dx * dx + dz * dz;
			// 已贴近目标 = 视为命中，停止追踪（只在地面追踪，不追身体高低）
			if (horizSqr < WAVE_HIT_DIST * WAVE_HIT_DIST) {
				e.getPersistentData().putBoolean(WAVE_HIT_FLAG, true);
				continue;
			}
			// 追踪时保持恒定速度（用实体自身惯性下的巡航速度），不要用已经衰减过的当前速度，
			// 否则每个 tick 都会在上一帧变慢的基础上再变慢。
			double speed = waveCruiseSpeed(e);
			double len = Math.sqrt(horizSqr);
			e.setDeltaMovement(dx / len * speed, 0.0, dz / len * speed);
		}
	}

	/** ElectricityEntity 在自身惯性(0.85)下 deltaMovement 收敛到的水平巡航速度。 */
	private static double waveCruiseSpeed(Entity e) {
		if (e instanceof net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity ee) {
			double p = Math.sqrt(ee.xPower * ee.xPower + ee.zPower * ee.zPower);
			if (p > 1.0E-6) return p * 0.85 / 0.15;
		}
		Vec3 cur = e.getDeltaMovement();
		double s = Math.sqrt(cur.x * cur.x + cur.z * cur.z);
		return s < 1.0E-3 ? 0.5 : s;
	}

	private static void steerOne(Entity e, @Nullable LivingEntity target) {
		if (target == null) return;
		Vec3 dir = target.getEyePosition().subtract(e.position());
		if (dir.lengthSqr() < 1.0E-6) return;
		e.setDeltaMovement(dir.normalize().scale(steerSpeed(e)));
	}

	/** 追踪保持恒定速度：首次追踪时记下初始速度并一直复用，避免逐 tick 变慢（同烈波）。 */
	private static double steerSpeed(Entity e) {
		var tag = e.getPersistentData();
		if (tag.contains(SPEED_TAG)) return tag.getDouble(SPEED_TAG);
		double s = e.getDeltaMovement().length();
		if (s < 1.0E-3) s = 1.0;
		tag.putDouble(SPEED_TAG, s);
		return s;
	}

	@Nullable
	private static <T extends Entity> LivingEntity resolve(AbstractGolemEntity<?, ?> golem, T e) {
		var tag = e.getPersistentData();
		if (!tag.hasUUID(TARGET_KEY)) return null;
		UUID id = tag.getUUID(TARGET_KEY);
		if (golem.level() instanceof ServerLevel sl) {
			Entity found = sl.getEntity(id);
			if (found instanceof LivingEntity le) return le;
		}
		return null;
	}

	private static <T extends Entity> void store(T e, LivingEntity target) {
		e.getPersistentData().putUUID(TARGET_KEY, target.getUUID());
	}

	@Nullable
	private static LivingEntity firstFree(List<LivingEntity> pool, List<LivingEntity> taken) {
		for (LivingEntity t : pool) {
			if (!taken.contains(t)) return t;
		}
		return null;
	}

	/** 可追踪目标：主目标排最前，其余按傀儡自己的目标优先级排。 */
	private static List<LivingEntity> collectTargets(AbstractGolemEntity<?, ?> golem, int cap) {
		var list = golem.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(SEARCH),
				e -> GolemTargetUtil.canTarget(golem, e));
		var main = golem.getTarget();
		list.sort(Comparator.comparing((LivingEntity e) -> e == main ? 0 : 1)
				.thenComparing(e -> Optional.ofNullable(TargetManager.predicateTarget(golem, e))
						.map(Enum::ordinal).orElse(100)));
		List<LivingEntity> ans = new ArrayList<>();
		for (LivingEntity e : list) {
			if (ans.size() >= Math.max(1, cap)) break;
			ans.add(e);
		}
		if (ans.isEmpty() && main != null && main.isAlive()) ans.add(main);
		return ans;
	}
}

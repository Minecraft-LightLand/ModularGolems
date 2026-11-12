package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class IgnisJumpModifier extends GolemModifier implements EarthquakeHelper.Modifier {
	public static final int COOLDOWN = 200;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;

	/** 腾炎护腿：飞跃猛击的烈焰轰击持续时间放大比例（0.5 = +50%）。 */
	public static final float FLAME_STRIKE_DURATION_BONUS = 1.0F;

	private static final String KEY_QUAKE_RADIUS = "IgnisQuakeRadius";
	private static final String KEY_QUAKE_TICK = "IgnisQuakeTimer";
	private static final String KEY_QUAKE_CENTER = "IgnisQuakeCenter";
	private static final String KEY_SMASH_STEP = "IgnisSmashStep";

	/**
	 * 本体 REINFORCED_SMASH 的落地震地是一圈一圈由内向外扩的：animationTick 5..19 每 2 tick 调一次
	 * {@code ShieldSmashDamage(2, j, 2.5f, 2f, false, 80, 1.1F, 0.06f, 0.075f)}，j 从 3 递增。
	 * 这里照搬：从 2 到 10，每 2 tick 外扩一圈。
	 */
	private static final int SMASH_START = 2, SMASH_END = 10, SMASH_INTERVAL = 2;

	/**
	 * Radius of the {@code Flame_Strike_Entity} runes dropped under the nearby targets.
	 */
	private static final float RUNE_RADIUS = 2.0F;

	/**
	 * The landing point gets its own, noticeably wider rune so the centre of the quake reads as the boss
	 * slamming down rather than as one more target marker. Both spreads below are derived from these two,
	 * so changing a radius here moves the spacing with it.
	 */
	private static final float LANDING_RUNE_RADIUS = 5.0F;

	/**
	 * Runes closer than this to one another overlap into mush, so the candidates are spaced apart. Two
	 * ordinary runes just touch at this distance, since each is {@link #RUNE_RADIUS} wide.
	 */
	private static final double RUNE_SPREAD = RUNE_RADIUS * 2;

	public IgnisJumpModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/**
	 * The quake wave counts down over several ticks, so its radius and parity need to survive between
	 * ticks. It is scratch state, not something the world should remember: keeping it in the persistent
	 * data dirties the NBT every other tick and writes it to the save file for no reason.
	 */
	private static int quakeRadius(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_RADIUS);
		return v == null ? 0 : v;
	}

	private static int quakeTick(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_TICK);
		return v == null ? 0 : v;
	}

	/**
	 * The point the collapsing wave pulls in on, recorded when the golem touched down. The wave runs for
	 * well after the landing, by which point the golem has already resumed pathing, so leaving the centre
	 * on the golem would drag the whole ring across the map behind it.
	 */
	private static Vec3 quakeCenter(AbstractGolemEntity<?, ?> golem) {
		return golem.getTransientState(KEY_QUAKE_CENTER);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		// 第一段：方块由内向外一圈圈震起（本体 tick 5..19，j 从 3 递增，每圈整圆）
		Integer stepObj = golem.getTransientState(KEY_SMASH_STEP);
		if (stepObj != null) {
			int step = stepObj;
			int dist = SMASH_START + step / SMASH_INTERVAL;
			if (step % SMASH_INTERVAL == 0 && dist <= SMASH_END) {
				spawnSmashRing(golem, dist);
			}
			if (dist >= SMASH_END && step % SMASH_INTERVAL == 0) {
				// 扩张结束，接上收束段
				golem.setTransientState(KEY_SMASH_STEP, null);
				golem.setTransientState(KEY_QUAKE_RADIUS, 16);
				golem.setTransientState(KEY_QUAKE_TICK, 2);
			} else {
				golem.setTransientState(KEY_SMASH_STEP, step + 1);
			}
			return;
		}
		int radius = quakeRadius(golem);
		if (radius <= 0) return;
		int timer = quakeTick(golem) + 1;
		if (timer < 2) {
			golem.setTransientState(KEY_QUAKE_TICK, timer);
			return;
		}
		golem.setTransientState(KEY_QUAKE_TICK, 0);
		boolean soul = CataDispatch.ignisBlue(golem);
		// forward offset is zero: that argument is how far the original swings its greatsword ahead of its
		// body, and these golems smash with their own legs instead, so there is nothing to reach past. Keeping
		// it at the original 2 would also shift the collapsing ring off the landing rune it belongs to.
		// 常规状态就带大范围伤害并聚怪；腾炎护腿加成整段飞跃猛击伤害。
		// 生命值低于 2/3（或被胸甲钉住）= 焰魔二阶段震地数值（1.5 / 0.15），常规为 1.1 / 0.06。
		float legs = quakeLegsMul(golem);
		boolean heavy = isHeavy(golem);
		float dmg = heavy ? 1.5f : 1.1f, hp = heavy ? 0.15f : 0.06f;
		GolemCataProxy.ignisQuakeWaveAt(golem, quakeCenter(golem), radius, 2.5f, 0.0f, true, dmg * legs, hp * legs, 0.075f, soul);
		golem.setTransientState(KEY_QUAKE_RADIUS, radius <= 6 ? null : radius - 1);
	}

	private static float quakeLegsMul(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.ignisLegs(golem) ? 1f + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue() : 1f;
	}

	private static boolean isHeavy(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.ignisFinalForm(golem);
	}

	/**
	 * 一圈整圆震地：抛起该半径上的地面方块并造成伤害，照搬本体
	 * {@code ShieldSmashDamage(2, dist, 2.5f, 2f, false, 80, 1.1F, 0.06f, 0.075f)}。
	 */
	private static void spawnSmashRing(AbstractGolemEntity<?, ?> golem, int dist) {
		float legs = quakeLegsMul(golem);
		float dmg = (isHeavy(golem) ? 1.5f : 1.1f) * legs;
		float hp = (isHeavy(golem) ? 0.15f : 0.06f) * legs;
		GolemCataProxy.ignisQuakeWaveAt(golem, quakeCenter(golem), dist, 2.5f, 0.0f, false,
				dmg, hp, 0.075f, CataDispatch.ignisBlue(golem));
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return (double) MAX_RANGE * MAX_RANGE;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var level = golem.level();
		boolean soul = CataDispatch.ignisBlue(golem);
		boolean chest = CataArmorHelper.ignisChest(golem);
		GolemCataProxy.ignisEarthquakeFX(golem, 20);
		// 中心冲击环 + 内圈初始方块（白色冲击波保留）；外扩与伤害由 onAiStep 的逐圈震地负责
		GolemCataProxy.ignisSmashBlocks(golem, 2.0f, 1, 2.5f);
		// 中心落点：只有脚下一小圈（本体 REINFORCED_SMASH 的 getBoundingBox().inflate(1.25) 一击），
		// 大范围伤害由下面由内向外逐圈的震地承担，数值照本体 1.5×攻击 + 0.15×目标最大生命
		GolemCataProxy.ignisAreaPunch(golem, 1.25f, 360f);
		// 由内向外逐圈震起方块（本体 tick 5..19 的 ShieldSmashDamage 序列），结束后自动接收束段
		golem.setTransientState(KEY_SMASH_STEP, 0);
		var self = golem.position();
		Vec3 landing = findFloor(level, self, 4);
		golem.setTransientState(KEY_QUAKE_CENTER, landing != null ? landing : self);
		// 烈焰轰击（落点 + 附近每个目标脚下）只在完整形态（腾炎胸甲，或生命值低于 1/3）触发
		if (!(chest || CataArmorHelper.isBelow(golem, CataArmorHelper.ONE_THIRD))) return;
		var aabb = golem.getBoundingBox().inflate(16, 6, 16);
		List<Vec3> list = new ArrayList<>();
		if (landing != null) list.add(landing);
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			var ans = findFloor(level, e.position(), 4);
			if (ans == null) continue;
			boolean tooClose = false;
			for (var other : list) {
				double spread = other == landing ? LANDING_RUNE_RADIUS + RUNE_RADIUS : RUNE_SPREAD;
				if (other.distanceTo(ans) < spread) {
					tooClose = true;
					break;
				}
			}
			if (!tooClose) list.add(ans);
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (soul) atk *= 4f / 3;
		list.sort(Comparator.comparingDouble(self::distanceToSqr));
		for (int i = 0; i < list.size(); i++) {
			boolean isLanding = list.get(i) == landing;
			int dur = CataArmorHelper.ignisLegs(golem)
					? Math.round(40 * (1 + FLAME_STRIKE_DURATION_BONUS)) : 40;
			GolemCataProxy.createBlast(golem, list.get(i), dur, i * 2,
					isLanding ? LANDING_RUNE_RADIUS : RUNE_RADIUS, atk, soul);
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		cache.addHurtModifier(DamageModifier.multTotal(MGConfig.COMMON.ignisSkillDamageFactor.get().floatValue()));
		if (CataArmorHelper.ignisLegs(entity)) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue()));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && GolemCataProxy.isIgnisStrike(direct)) {
			if (GolemCataProxy.isSoul(direct)) {
				event.getEntity().invulnerableTime = 0;
			}
		}
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		var event = cache.getLivingDamageEvent();
		assert event != null;
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		LivingEntity target = cache.getAttackTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue();
		CataDispatch.stackBlazingBrand(golem, target, rate * cache.getDamageDealt(), GolemCataProxy.isSoul(direct) ? 3 : 1);
	}

	@Nullable
	private static Vec3 findFloor(Level level, Vec3 p, int diff) {
		double x = p.x, maxY = p.y, z = p.z, minY = maxY - diff;
		BlockPos pos = BlockPos.containing(x, maxY, z);
		double dy = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState lowState = level.getBlockState(low);
			if (lowState.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState state = level.getBlockState(pos);
					VoxelShape shape = state.getCollisionShape(level, pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				return new Vec3(x, pos.getY() + dy, z);
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return null;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				EarthquakeHelper.leapReduction(),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS),
				CataArmorHelper.healthPct(CataArmorHelper.ONE_THIRD))
				.withStyle(ChatFormatting.GREEN));
	}

}

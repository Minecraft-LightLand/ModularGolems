package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;

import java.util.List;
import java.util.function.Consumer;

/**
 * 怒吼砸击（对应 Boss 的 state 9→10→11/12）：
 * 起手怒吼 + 推开周围实体 → 瞬移到目标上方 +10 → 砸落。
 * 落地造成 16 + 目标最大生命值 5% 的扇形伤害，并向锁定朝向的左右两侧各喷 6 颗等离子球
 * （等离子球伤害 8、无视护甲）。坠落后有概率再次瞬移砸落一次。
 * <p>
 * 湮灭浩劫已并入本技能：坠落后若生命值低于 {@link #NUKE_HEALTH} 比例，继续播放多段连环核爆，
 * 写法与灾变腾炎「飞跃猛击落地接烈焰轰击」一致。
 */
public class ObliteratorJumpGroundChargeModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public static final int COOLDOWN = 200;
	/** 湮灭浩劫（后半段）解锁的生命阈值（Boss 的 canUltimate 要求三阶段，即 Crackiness.HIGH = 30%）。 */
	public static final float NUKE_HEALTH = LMArmorHelper.PHASE_THREE;
	/** 坠落后再次瞬移砸落的概率。 */
	public static final float SECOND_SMASH_CHANCE = 0.5F;
	/** 第二次砸落的判定延迟（tick）。 */
	private static final int SECOND_SMASH_TICK = 20;
	/** 浩劫窗口长度（tick）。 */
	private static final int NUKE_WINDOW = 110;

	private static final String KEY_CAST_YAW = "ObliteratorRoarCastYaw";
	private static final String KEY_TICKS = "ObliteratorRoarTicks";
	private static final String KEY_STAGE = "ObliteratorRoarStage";
	private static final String KEY_AIRBORNE = "ObliteratorRoarAirborne";
	private static final String KEY_NUKE_X = "ObliteratorNukeX";
	private static final String KEY_NUKE_Z = "ObliteratorNukeZ";

	public ObliteratorJumpGroundChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.addDealtModifier(DamageModifier.multTotal(EarthquakeHelper.LEAP_DAMAGE_MULT, getRegistryName()));
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		// 起手：怒吼 + 推开周围实体 + 记录朝向
		float castYaw = LMProxy.roarSmashLeap(golem);
		golem.getPersistentData().putFloat(KEY_CAST_YAW, castYaw);
		if (golem.getControllingPassenger() instanceof net.minecraft.world.entity.player.Player) {
			// 照末影守卫犬形跳跃：玩家骑乘时直接瞬移到目标上方 / 朝视线前方，而不是普通起跳
			LivingEntity target = golem.getTarget();
			if (target != null && target.isAlive() && golem.predicateTarget(target)) {
				golem.teleportTo(target.getX(), target.getY() + 10, target.getZ());
			} else {
				Vec3 look = golem.getLookAngle();
				Vec3 flat = new Vec3(look.x, 0, look.z);
				if (flat.lengthSqr() < 1e-8) flat = new Vec3(0, 0, 1);
				flat = flat.normalize();
				golem.teleportTo(golem.getX() + flat.x * 8, golem.getY() + 8, golem.getZ() + flat.z * 8);
			}
			golem.setDeltaMovement(0, 0, 0);
		} else {
			EarthquakeHelper.performDefaultJump(golem);
		}
		golem.hasImpulse = true;
	}

	@Override
	public boolean useServerJump() {
		return true;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		float castYaw = golem.getPersistentData().getFloat(KEY_CAST_YAW);
		LMProxy.spawnObliteratorRoarSmashLanding(golem, level, castYaw);
		var data = golem.getPersistentData();
		// 三阶段判定走 LMArmorHelper：穿湮灭胸甲时无视生命值要求，直接进入湮灭浩劫
		if (LMArmorHelper.obliteratorPhaseThree(golem)) {
			// 低血量：砸地即刻接湮灭浩劫（不再等窗口/二次砸落）
			data.putInt(KEY_STAGE, 2);
			data.putInt(KEY_TICKS, 0);
			data.putDouble(KEY_NUKE_X, golem.getX());
			data.putDouble(KEY_NUKE_Z, golem.getZ());
			data.putFloat(KEY_CAST_YAW, golem.getYRot());
			LMProxy.spawnObliteratorUltimateQuake(golem, level);
		} else {
			// 开启落地后的窗口：可能再次起跳砸落 + 到 t45 若掉到阈值下再转浩劫
			data.putInt(KEY_STAGE, 1);
			data.putInt(KEY_TICKS, 0);
			data.putBoolean(KEY_AIRBORNE, false);
		}
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) LMProxy.lmSmashParticles(golem, 0.0f, 0.0f, 0.0f, 7.5f, 4.0f);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				LMArmorHelper.pct(SECOND_SMASH_CHANCE), EarthquakeHelper.leapReduction(),
				LMArmorHelper.pct(NUKE_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

	/**
	 * 落地后的多段窗口：
	 * <ul>
	 *   <li>stage 1（约 0-45 tick）：t20 起有概率再次瞬移到目标上方并补一次砸落；</li>
	 *   <li>到 t45 后若生命低于 {@link #NUKE_HEALTH} 则切到 stage 2 播放湮灭浩劫；否则收尾。</li>
	 *   <li>stage 2：t2-21 吸引 → t48/t80 两次砸地 → t60-98 两段连环核爆 → t105 瞬移背后。</li>
	 * </ul>
	 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		var data = golem.getPersistentData();
		int stage = data.getInt(KEY_STAGE);
		if (stage <= 0) return;
		int ticks = data.getInt(KEY_TICKS) + 1;
		data.putInt(KEY_TICKS, ticks);
		float castYaw = data.getFloat(KEY_CAST_YAW);
		if (stage == 1) {
			// 再次瞬移砸落
			if (ticks == SECOND_SMASH_TICK && LMArmorHelper.obliteratorPhaseTwo(golem)
					&& golem.getRandom().nextFloat() < SECOND_SMASH_CHANCE) {
				LivingEntity target = golem.getTarget();
				if (target != null) {
					// 真实再起跳（朝目标上抛），避免瞬移上天
					golem.setDeltaMovement((target.getX() - golem.getX()) * 0.15, 1.3, (target.getZ() - golem.getZ()) * 0.15);
					golem.hasImpulse = true;
					data.putBoolean(KEY_AIRBORNE, true);
				}
			}
			if (data.getBoolean(KEY_AIRBORNE) && golem.onGround() && ticks > SECOND_SMASH_TICK + 3) {
				data.putBoolean(KEY_AIRBORNE, false);
				LMProxy.spawnObliteratorRoarSmashLanding(golem, level, castYaw);
				data.putInt(KEY_TICKS, 45);
			}
			if (ticks >= 45) {
				if (LMArmorHelper.obliteratorPhaseThree(golem)) {
					data.putInt(KEY_STAGE, 2);
					data.putInt(KEY_TICKS, 0);
					data.putDouble(KEY_NUKE_X, golem.getX());
					data.putDouble(KEY_NUKE_Z, golem.getZ());
					data.putFloat(KEY_CAST_YAW, golem.getYRot());
					LMProxy.spawnObliteratorUltimateQuake(golem, level);
				} else {
					data.putInt(KEY_STAGE, 0);
				}
			}
		} else if (stage == 2) {
			if (ticks >= 2 && ticks <= 21) LMProxy.obliteratorPull(golem);
			if (ticks == 48) LMProxy.obliteratorUltimateHit1(golem, level);
			if (ticks == 80) LMProxy.obliteratorUltimateHit2(golem, level);
			double nx = data.getDouble(KEY_NUKE_X);
			double nz = data.getDouble(KEY_NUKE_Z);
			if (ticks >= 60 && ticks <= 78 && ticks % 2 == 0) {
				LMProxy.obliteratorNukeRing(golem, level, (ticks - 60) / 2 * 2 + 2, true, nx, nz, castYaw);
			}
			if (ticks >= 80 && ticks <= 98 && ticks % 2 == 0) {
				LMProxy.obliteratorNukeRing(golem, level, (ticks - 80) / 2 * 2 + 2, false, nx, nz, castYaw);
			}
			if (ticks == 105) LMProxy.obliteratorTeleportBehind(golem, -4.0);
			if (ticks >= NUKE_WINDOW) data.putInt(KEY_STAGE, 0);
		}
	}
}

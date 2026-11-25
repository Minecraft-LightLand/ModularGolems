package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.FallingSoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulShieldEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.TheObliteratorCloneEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.TheObliteratorCloneWithArmsEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.*;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.ParticleUtils;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorEffects;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LMProxyImpl {

	public static void shake(LivingEntity user, Vec3 pos) {
		CameraShakeEntity.cameraShake(user.level(), pos, 15.0F, 0.3F, 0, 15);
	}

	/** 技能震屏统一入口（骑乘者由客户端按玩家过滤，不影响服务端发包）。 */
	private static void skillShake(LivingEntity golem, Vec3 pos, float scale, float intensity, int dur, int interval) {
		CameraShakeEntity.cameraShake(golem.level(), pos, scale, intensity, dur, interval);
	}

	public static void anchorParticle(ServerLevel level, double x, double y, double z,  float reach) {
		int n = 128;
		for (double i = 0; i < n; ++i) {
			var a = (Math.PI * 2D) / n * i;
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
					x + 0.5F + Math.cos(a) * reach,
					y,
					z + 0.5F + Math.sin(a) * reach,
					0, 0.0F, 0.05, 0.0F, 1);
		}

		level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
	}

	/**
	 * 范围眩晕 + 伤害。**不再自带音效**（旧版写死 IRON_GOLEM_REPAIR，Boss 从不播这个音）——
	 * 需要音效由调用方通过 {@code sound} 传入 Boss 原招的音。
	 */
	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv,
			@Nullable SoundEvent sound, float damage) {
		List<LivingEntity> affected = new ArrayList<>();
		if (sound != null) level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
		Vec3 cen = new Vec3(x, y, z);
		for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, (new AABB(cen, cen)).inflate(reach), e -> e != golem)
				.stream().sorted(Comparator.comparingDouble(ec -> ec.distanceToSqr(cen))).toList()) {
			if (!(golem instanceof AbstractGolemEntity<?, ?> _ag) || !GolemTargetUtil.canTarget(_ag, le)) continue;
			// 对齐 Boss TheObliteratorEntity 的 STUN：getStunDuration()=50，amplifier=1
			le.addEffect(new MobEffectInstance(ModEffects.STUN.get(), OBLITERATOR_STUN_DURATION, OBLITERATOR_STUN_AMP, false, false));
			le.hurt(golem.damageSources().mobAttack(golem), damage);
			affected.add(le);
		}
		return affected;
	}

	/** 兼容旧调用：无音效，伤害沿用旧公式（攻击力 × 0.4 × 等级）。 */
	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv) {
		return stun(level, x, y, z, golem, reach, lv, null, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * lv * 0.4f);
	}

	/** 一根落雷柱：从 maxY 向下找第一个可站立的方块面，在那里生成一个带延迟的 LightningBoltEntity。 */
	private static void spawnSingleBolt(Mob e, double x, double z, double minY, double maxY, float rotation, int delay, float dmg) {
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0.0F;
		do {
			BlockPos low = pos.below();
			BlockState state = e.level().getBlockState(low);
			if (state.isFaceSturdy(e.level(), low, Direction.UP)) {
				if (!e.level().isEmptyBlock(pos)) {
					BlockState topState = e.level().getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(e.level(), pos);
					if (!topShape.isEmpty()) d0 = topShape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			e.level().addFreshEntity(new LightningBoltEntity(e.level(), x, (double) pos.getY() + d0, z, rotation, delay, e, 20, dmg));
		}
	}

	private static Entity createElectricity(LivingEntity entity, Vec3 vec, float angle, float damage) {
		return new ElectricityEntity(entity, vec.x, vec.y, vec.z, entity.level(), damage, angle, 20.0F);
	}

	/** Boss 本体攻击力：TheObliteratorEntity / PossessedPaladinEntity 都是 6.0。 */
	public static final float BOSS_ATTACK = 6.0f;
	/** Boss TheObliteratorEntity#getStunDuration() = 50 tick。 */
	public static final int OBLITERATOR_STUN_DURATION = 50;
	/** Boss 湮灭构造体的 STUN amplifier（原版写死 1）。 */
	public static final int OBLITERATOR_STUN_AMP = 1;

	/**
	 * 把 Boss 的固定伤害按傀儡攻击力等比缩放：傀儡攻击力 = 6.0（与 Boss 同值）时与原招完全一致。
	 * 与原 boss 一致，伤害本身不随技能等级变化（等级效果只体现在冷却减半上，与灾变技能同口径）。
	 * <p>
	 * {@code lv} 参数保留是为了兼容既有调用方，不再参与伤害计算。
	 */
	public static float bossDamage(LivingEntity golem, int lv, float bossValue) {
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		return bossValue * (atk / BOSS_ATTACK);
	}

	/** 护腿「+50% 技能伤害」倍率：穿了指定护腿返回 1.5，否则 1。 */
	private static float legsMult(LivingEntity golem, Object legsItem) {
		return golem instanceof AbstractGolemEntity<?, ?> ag ? LMArmorHelper.legsMult(ag, legsItem) : 1f;
	}

	public static void playAt(LivingEntity golem, SoundEvent sound, float vol, float pitch) {
		golem.level().playSound(null, BlockPos.containing(golem.getX(), golem.getY(), golem.getZ()),
				sound, SoundSource.NEUTRAL, vol, pitch);
	}

	/** Boss 的 Circle 环，服务端广播。shrink=true 用 SHRINK，否则 GROW。 */
	/**
	 * 瞬移特效（Boss 的 teleport() 起点表现）：TELEPORT_EFFECT 粒子（y+3）+ 末影人传送音。
	 * 直接把坐标传进来，瞬移前后各放一次即可。
	 */
	public static void teleportFxAt(Level level, double x, double y, double z) {
		level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 3.0F, 1.0F);
		if (level instanceof ServerLevel sl) {
			// Boss 用 1 颗，这里多撒几颗并向上散开，保证在傀儡体型下也看得见（同一粒子）
			sl.sendParticles(ModParticles.TELEPORT_EFFECT.get(), x, y + 3.0, z, 8, 0.8, 1.2, 0.8, 0.0);
		}
	}

	/** Boss 的 doTeleportEffects：只播末影人传送音（3.0, 1.0），不位移。见 TheObliteratorEntity#doTeleportEffects:5200。 */
	public static void obliteratorTeleportSound(LivingEntity golem) {
		playAt(golem, SoundEvents.ENDERMAN_TELEPORT, 3.0f, 1.0f);
	}

	/**
	 * Boss 的真瞬移（TheObliteratorEntity#teleport:4182）：起点 TELEPORT_EFFECT 粒子（y+3）→ 向下找地面 →
	 * 传送 → CameraShake(10,0.1,5,5) + 传送音(4.0,1.0)。{@code back} 为落点相对目标的沿朝向偏移（Boss 用 -6 / -4）。
	 * 灾变末影守卫傀儡也是真瞬移，这里与 Boss 保持一致。
	 */
	public static void obliteratorTeleportBehind(LivingEntity golem, double back) {
		if (golem.level().isClientSide) return;
		// 只能在落地时瞬移：后撤/瞬移类都不能在空中进行（否则大跳途中被打到会被格挡瞬移走）
		if (!golem.onGround()) return;
		// 停留模式（STAND）不位移：这是「站桩施法」语义，和灾变的位移类技能一样排除 STAND
		if (golem instanceof AbstractGolemEntity<?, ?> ag && ag.getMode() == GolemModes.STAND) return;
		if (!(golem instanceof Mob mob) || mob.getTarget() == null) return;
		LivingEntity target = mob.getTarget();
		// 正后方：沿目标朝向的反方向偏移。MC 的 forward = (-sin yaw, cos yaw)，
		// 所以身后方向 = -forward，用 getForward() 取朝向最稳妥。
		double dist = -back; // back 传负值（如 -6 = 身后 6 格），这里转成正距离
		Vec3 fwd = target.getForward();
		double dx = target.getX() - fwd.x * dist;
		double dz = target.getZ() - fwd.z * dist;
		teleportFxAt(golem.level(), golem.getX(), golem.getY(), golem.getZ());
		Vec3 at = EarthquakeHelper.findFloor(golem.level(), new Vec3(dx, target.getY(), dz), 8);
		double dy = at == null ? target.getY() : at.y;
		if (golem.randomTeleport(dx, dy, dz, true)) {
			mob.getNavigation().stop();
			skillShake(golem, golem.position(), 10.0F, 0.1F, 5, 5);
			teleportFxAt(golem.level(), golem.getX(), golem.getY(), golem.getZ());
		}
	}
	// ===== Obliterator: 新增技能（克隆八向炮 / 四向激光 / 克隆爆发 / 格挡 / 后跳砸地 / 浩劫补全） =====

	/**
	 * 克隆八向炮（Boss AmbushGoal#spawnRingWave）：以目标为中心、半径 8.5 环绕 8 个虚影（两波各 8 点），
	 * 每个虚影朝目标发射一次湮灭炮弹（伤害 16）。二阶段（phase>=2）再补一轮。
	 */
	public static void spawnCloneBarrage(LivingEntity golem, LivingEntity target, int lv, boolean secondWave) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 16.0f);
		double radius = 8.5;
		int totalPoints = 16;
		int wavesTotal = secondWave ? 2 : 2;
		int pointsThisWave = totalPoints / wavesTotal;
		double step = (Math.PI * 2.0) / totalPoints;
		double base = -0.41887902047863906;
		Level level = golem.level();
		int wave = secondWave ? 1 : 0;
		for (int i = 0; i < pointsThisWave; ++i) {
			double angle = base + (double) wave * step + (double) i * (double) wavesTotal * step;
			double spawnX = target.getX() + Math.cos(angle) * radius;
			double spawnZ = target.getZ() + Math.sin(angle) * radius;
			Vec3 at = EarthquakeHelper.findFloor(level, new Vec3(spawnX, target.getY(), spawnZ), 10);
			double spawnY = at == null ? target.getY() : at.y;
			float yawRad = (float) Math.atan2(target.getZ() - spawnZ, target.getX() - spawnX);
			TheObliteratorCloneEntity clone = new TheObliteratorCloneEntity(level, spawnX, spawnY, spawnZ, yawRad, 4,
					golem, damage, (float) target.getX(), (float) target.getY(), (float) target.getZ());
			level.addFreshEntity(clone);
		}
		playAt(golem, SoundEvents.ENDERMAN_TELEPORT, 2.0f, 1.0f);
	}

	/** 克隆八向炮的起手：Boss AmbushGoal 里 tick57 的瞬移 + 传送音。 */
	public static void cloneBarrageStart(LivingEntity golem) {
		obliteratorTeleportBehind(golem, -8.0);
		playAt(golem, SoundEvents.ENDERMAN_TELEPORT, 2.0f, 1.0f);
	}

	/**
	 * 四向湮灭激光（Boss state46）：以自身为中心同时放 4 道旋转光束（偏移 0/90/180/270），
	 * 伤害 7.5、持续 50 tick、旋转速度 2.6+目标速度、方向由 {@code right} 决定。
	 */
	public static void spawnQuadLaser(LivingEntity golem, int lv, boolean right, float castYaw) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 7.5f);
		double targetSpeed = 0.0;
		if (golem instanceof Mob mob && mob.getTarget() != null) {
			targetSpeed = mob.getTarget().getAttributeValue(Attributes.MOVEMENT_SPEED) * 5.0;
		}
		float speed = (float) (2.6 + targetSpeed);
		// 照 Boss state46 的构造参数：yaw/pitch 传入【弧度值】。
		// 注意 calculateEndPos 的 quad 分支会再 Math.toRadians(getYaw())/toRadians(getPitch())，
		// 若这里传【度数】会得到真实俯仰角 —— 傀儡看近处目标低头时激光就朝下射到地面、射不远。
		// Boss 传 (角度*PI/180)，二次转换后 pitch≈0（水平），所以能射很远。这里完全照抄。
		float yRot = (float) ((castYaw + 90.0f) * Math.PI / 180.0);
		float xRot = (float) ((-golem.getXRot()) * Math.PI / 180.0);
		double y = golem.getY() + 2.0;
		@SuppressWarnings("unchecked")
		EntityType<AnnihilationBeamEntity> type = (EntityType<AnnihilationBeamEntity>) (EntityType<?>) ModEntities.ANNIHILATION_BEAM.get();
		for (float offset : new float[]{90.0f, 0.0f, 180.0f, 270.0f}) {
			AnnihilationBeamEntity beam = new AnnihilationBeamEntity(type, golem.level(), golem,
					golem.getX(), y, golem.getZ(), yRot, xRot, 50, damage, 5.0F, 1, true,
					speed, offset, 0.5F, right, 30.0F);
			golem.level().addFreshEntity(beam);
		}
		skillShake(golem, golem.position(), 30.0F, 0.15F, 40, 10);
		playAt(golem, ModSounds.QUAD_ANNIHILATION_LASER_SHOOT.get(), 3.0f, 1.0f);
	}

	/** 四向激光蓄力音（Boss state46 tick9）。 */
	public static void quadLaserCharge(LivingEntity golem) {
		playAt(golem, ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0f, 1.0f);
	}

	/**
	 * 克隆爆发（Boss state24）：3 只持械虚影冲向目标斩击。前两只各砍一次，第三只砍两次；
	 * 傀儡本体在第三只虚影出手后突进并补一记臂刃。
	 */
	public static void spawnCloneBurst(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		// 湮灭护腿：克隆爆发整招伤害 +50%（含召唤虚影的斩击）
		float mult = legsMult(golem, LMCompatRegistry.OBLITERATOR_SHINGUARD.get());
		float damage = bossDamage(golem, lv, 16.0f);
		Level level = golem.level();
		double theta = Math.toRadians(golem.getYRot()) + 1.0;
		double vecX = Math.cos(theta);
		double vecZ = Math.sin(theta);
		Vec3 at = EarthquakeHelper.findFloor(level, new Vec3(golem.getX(), golem.getY(), golem.getZ()), 6);
		double floor = at == null ? golem.getY() : at.y;
		// 三只虚影，动画 1/1/2（第三只带二次攻击）
		int[] anims = {1, 1, 2};
		for (int i = 0; i < 3; i++) {
			double sx = golem.getX() + vecX * 2.0;
			double sz = golem.getZ() + vecZ * 2.0;
			TheObliteratorCloneWithArmsEntity clone = new TheObliteratorCloneWithArmsEntity(level, sx, floor, sz,
					0.0f, 0, golem, 16.0f * mult, (float) target.getX(), (float) target.getY(), (float) target.getZ(),
					anims[i], i == 2 ? 50 : 45);
			level.addFreshEntity(clone);
		}
		playAt(golem, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN, 1.0f, 1.0f);   // boss state24 召唤音
	}

	/** 克隆爆发的收尾：冲锋撞到目标后的一记臂刃范围斩（Boss state24 tick100）。 */
	public static void cloneBurstFinish(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		areaAttack(golem, 5.0f, 215.0f, bossDamage(golem, lv, 20.0f) * legsMult(golem, LMCompatRegistry.OBLITERATOR_SHINGUARD.get()), 1.5f);
		playAt(golem, ModSounds.WEAPON_IMPACT.get(), 1.0f, 0.5f);
	}

	/** 克隆爆发最后一段冲锋：每 tick 撞到范围内敌人的臂刃伤害。 */
	public static boolean cloneBurstChargeHit(LivingEntity golem, int lv, Vec3 dir) {
		if (golem.level().isClientSide) return false;
		if (!(golem instanceof AbstractGolemEntity<?, ?> ag)) return false;
		float damage = bossDamage(golem, lv, 8.0f) * legsMult(golem, LMCompatRegistry.OBLITERATOR_SHINGUARD.get());
		var src = golem.damageSources().mobAttack(golem);
		boolean hit = false;
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(0.6))) {
			if (!GolemTargetUtil.canTarget(ag, e)) continue;
			if (e.hurt(src, damage)) {
				ChargeHelper.pushChargeTarget(ag, e, dir);
				hit = true;
			}
		}
		return hit;
	}

	/**
	 * 湮灭浩劫的吸引段（Boss state53 tick2-21）：把 7 格内的实体持续拉向中心。
	 * 传送门本体吸引 2.5 格、小型传送门吸引 1.5 格（Boss 的 AnnihilationPortalEntity 行为）。
	 */
	public static void obliteratorPull(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		if (!(golem instanceof AbstractGolemEntity<?, ?> ag)) return;
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0), x -> x != golem)) {
			if (!GolemTargetUtil.canTarget(ag, e)) continue;
			Vec3 diff = golem.position().subtract(e.position());
			double len = diff.length();
			if (len < 0.1) continue;
			Vec3 pull = diff.scale(0.015 / len);
			e.setDeltaMovement(e.getDeltaMovement().add(pull));
			e.hurtMarked = true;
		}
	}

	/**
	 * 湮灭浩劫的后半段（Boss state53 tick60-98）：从内到外 10 圈连环核爆，
	 * 波及的实体被抛射到空中（Boss 的 0.35 向上初速）。
	 */
	public static void obliteratorNukeRing(LivingEntity golem, int lv, int distance, boolean warning, double cx, double cz, float castYaw) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 17.0f);
		flameRadagonShockwave(golem, cx, cz, castYaw, 2.0f, distance, 1.0f, 2, 0.0f, 2.0f, damage, warning, 0.15f);
		if (!warning && golem instanceof AbstractGolemEntity<?, ?> ag) {
			for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(25.0), x -> x != golem)) {
				if (!GolemTargetUtil.canTarget(ag, e)) continue;
				if (!e.onGround()) continue;
				e.setDeltaMovement(e.getDeltaMovement().x, 0.35, e.getDeltaMovement().z);
				e.hurtMarked = true;
			}
		}
	}


	/**
	 * 后跳砸地的单圈冲击波（Boss state28/29）：每 2 tick 沿正面外扩一圈，逐圈铺满成完整冲击波。
	 * first=true 时附带镜头抖动、爆炸音、收缩环与 210° 范围攻击（伤害 22）。
	 */
	public static void backstepStompRing(LivingEntity golem, int lv, int distance, boolean first, float castYaw, double cx, double cz) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		float waveDamage = bossDamage(golem, lv, 7.0f);
		// 锁定朝目标的朝向，保证每圈都从傀儡位置正对目标（不歪）
		golem.setYRot(castYaw);
		golem.yBodyRot = castYaw;
		golem.yHeadRot = castYaw;
		if (first) {
			skillShake(golem, golem.position(), 20.0F, 0.25F, 0, 20);
			playAt(golem, ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0f, 1.0f);
			if (level instanceof ServerLevel sl) {
				sl.sendParticles(new Circle.RingData(0.0f, (float) (Math.PI / 2), 20, 0.0f, 1.0f, 0.0f, 1.0f, 100.0f, false, Circle.EnumRingBehavior.GROW),
						golem.getX(), golem.getY() + 0.5, golem.getZ(), 1, 0, 0, 0, 0);
			}
			areaAttack(golem, 4.0f, 210.0f, bossDamage(golem, lv, 22.0f), 1.5f);
		}
		// 以傀儡为原点居中（vec=0, offset=0）—— boss 的 offset/前移是给大体型的，小傀儡会甩到前方；
		// 0.25π 弧、密度按 boss（arcLen=distance*spread）
		flameRadagonShockwave(golem, cx, cz, castYaw, 0.25f, distance, 0.0f, 2, 0.0f, 0.0f, waveDamage, false, 1.0f);
	}
	/** 客户端专用：Boss 的 controlledSmashParticles（只能在客户端画）。 */
	public static void lmSmashParticles(LivingEntity golem, float vec, float offset, float additionalY, float power, float r) {
		if (golem.level().isClientSide) ParticleUtils.controlledSmashParticles(golem, vec, offset, additionalY, power, r);
	}

	public static void sendRing(LivingEntity golem, float scale, int segments, boolean shrink, double yOff) {
		if (golem.level() instanceof ServerLevel sl) {
			ParticleOptions ring = new Circle.RingData(0.0f, (float) (Math.PI / 2), segments, 0.0f, 1.0f, 0.0f, 1.0f,
					scale, false, shrink ? Circle.EnumRingBehavior.SHRINK : Circle.EnumRingBehavior.GROW);
			sl.sendParticles(ring, golem.getX(), golem.getY() + yOff, golem.getZ(), 1, 0, 0, 0, 0);
		}
	}

	public static void sendParticle(LivingEntity golem, ParticleOptions particle, double yOff, int count, double spread) {
		if (golem.level() instanceof ServerLevel sl) {
			sl.sendParticles(particle, golem.getX(), golem.getY() + yOff, golem.getZ(), count, spread, spread, spread, 0);
		}
	}

	/** Boss SideAreaAttack 的简化版：以傀儡为中心的水平扇形内造成伤害，可选击飞。 */
	public static List<LivingEntity> areaAttack(LivingEntity golem, float range, float arcDeg, float damage, float launch) {
		List<LivingEntity> hit = new ArrayList<>();
		if (golem.level().isClientSide) return hit;
		double half = Math.toRadians(Math.min(arcDeg, 360.0f)) / 2;
		double yaw = Math.toRadians(golem.yBodyRot);
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(range), x -> x != golem)) {
			if (!(golem instanceof AbstractGolemEntity<?, ?> _ag) || !GolemTargetUtil.canTarget(_ag, e)) continue;
			double dx = e.getX() - golem.getX();
			double dz = e.getZ() - golem.getZ();
			double d2 = dx * dx + dz * dz;
			if (d2 > range * range) continue;
			if (arcDeg < 359.9f) {
				double ang = Math.atan2(-dx, dz);
				double diff = Math.atan2(Math.sin(ang - yaw), Math.cos(ang - yaw));
				if (Math.abs(diff) > half) continue;
			}
			e.hurt(golem.damageSources().mobAttack(golem), damage);
			if (launch > 0) {
				double d = Math.max(Math.sqrt(d2), 0.001);
				e.push(dx / d * launch, 0.35, dz / d * launch);
			}
			hit.add(e);
		}
		return hit;
	}

	// ===== Obliterator =====

	/** 湮灭巨弹：Boss state6/55 t37 的 shootAnnihilationBomb（伤害 10、16 发子弹）。 */
	public static void spawnObliteratorLargeBomb(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 10.0f);
		@SuppressWarnings("unchecked")
		EntityType<AnnihilationBombEntity> type = (EntityType<AnnihilationBombEntity>) (EntityType<?>) ModEntities.ANNIHILATION_BOMB_ENTITY.get();
		AnnihilationBombEntity bomb = new AnnihilationBombEntity(type, golem.level(), golem, damage, 16, false);
		bomb.moveTo(golem.getX(), golem.getEyeY(), golem.getZ(), golem.getYRot(), golem.getXRot());
		double d0 = target.getX() - bomb.getX();
		double d1 = target.getY() + target.getBbHeight() * 0.5 - bomb.getY();
		double d2 = target.getZ() - bomb.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		bomb.shoot(d0, d1 + d3 * 0.2, d2, 1.0f, (float) (14 - golem.level().getDifficulty().getId() * 4));
		bomb.setOwner(golem);
		golem.level().addFreshEntity(bomb);
	}

	/** Boss state6/7 t17 的 DIMENSIONAL_SHOOT_CHARGE 蓄力音。 */
	public static void obliteratorBombCharge(LivingEntity golem) {
		playAt(golem, ModSounds.DIMENSIONAL_SHOOT_CHARGE.get(), 1.0f, 1.0f);
	}

	public static void obliteratorBombFire(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		sendParticle(golem, ModParticles.ANNIHILATION_EXPLOSION.get(), 3.0, 1, 0.0);
		spawnObliteratorLargeBomb(golem, target, lv);
		playAt(golem, ModSounds.THE_WARPED_ONE_SHOOT.get(), 3.0f, 1.0f);
	}

	/** 发射一枚小型湮灭炸弹，朝给定方向（单位向量）飞出。 */
			/**
	 * 湮灭光束：Boss state21/42/43 —— 蓄力 22 tick 后开火，光束 duration=10、伤害 8.5。
	 * 开火时伴 ANNIHILATION_LASER_SINGLE_SHOOT 与 CameraShake(30, 0.25, 10, 5)。
	 */
	@Nullable
	public static Entity spawnObliteratorLaser(LivingEntity golem, LivingEntity target, int lv, float castYaw) {
		if (golem.level().isClientSide) return null;
		float damage = bossDamage(golem, lv, 8.5f);
		int duration = 10;
		float yRot = (float) ((castYaw + 90.0) * Math.PI / 180.0);
		float xRot = (float) ((-golem.getXRot()) * Math.PI / 180.0);
		double theta = Math.toRadians(castYaw);
		double vecX = Math.cos(theta + Math.PI / 2);
		double vecZ = Math.sin(theta + Math.PI / 2);
		double spawnX = golem.getX() + 2.0 * vecX;
		double spawnZ = golem.getZ() + 2.0 * vecZ;
		double spawnY = golem.getY() + 2.0;
		@SuppressWarnings("unchecked")
		EntityType<AnnihilationBeamEntity> type = (EntityType<AnnihilationBeamEntity>) (EntityType<?>) ModEntities.ANNIHILATION_BEAM.get();
		// 压制光束自带的 spawnExplosions：它的两颗炸弹固定朝【世界东/西】，傀儡朝东西射时会顺着光束飞。
		// 下面自己按「垂直光束、向两侧外飞」来喷，分布/数量仍照 boss（每 4 格一站、amount=len/π、落地面）。
		LivingEntity savedTarget = null;
		Mob mobCaster = golem instanceof Mob m ? m : null;
		if (mobCaster != null) {
			savedTarget = mobCaster.getTarget();
			mobCaster.setTarget(null);
		}
		AnnihilationBeamEntity beam = new AnnihilationBeamEntity(type, golem.level(), golem, spawnX, spawnY, spawnZ,
				yRot, xRot, duration, damage, 5.0f, 1, false, 0.0f, 0.0f, 0.0f, false, 30.0f);
		golem.level().addFreshEntity(beam);
		if (mobCaster != null) mobCaster.setTarget(savedTarget);
		Level level = golem.level();
		Vec3 aim = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ());
		Vec3 dir = aim.subtract(spawnX, spawnY, spawnZ);
		double len = Math.max(dir.length(), 0.001);
		double ux = dir.x / len, uy = dir.y / len, uz = dir.z / len;   // 单位方向（关键：否则 dir*d 会变成距离×距离）
		double hLen = Math.max(Math.sqrt(dir.x * dir.x + dir.z * dir.z), 1.0E-4);
		double hx = dir.x / hLen, hz = dir.z / hLen;
		double perpX = -hz, perpZ = hx;   // 与光束垂直的水平方向（左右）
		@SuppressWarnings("unchecked")
		EntityType<SmallAnnihilationBombEntity> bombType = (EntityType<SmallAnnihilationBombEntity>) (EntityType<?>) ModEntities.SMALL_ANNIHILATION_BOMB_ENTITY.get();
		float bombDamage = bossDamage(golem, lv, 6.5f);
		// 数量按【光束长度】：boss 的 beam 半径 r=30，amount=beamLen/π；沿光束每 4 格一站（boss 的 d2=4*(l+1)）
		double beamLen = 30.0;
		int amount = (int) (beamLen / Math.PI);
		if (amount < 4) amount = 4;
		for (int l = 0; l < amount; ++l) {
			double d = 2.0 + 4.0 * (double) l;   // 从傀儡前方 2 格起、间距 4 格
			if (d > beamLen) break;
			double bx = golem.getX() + ux * d;
			double bz = golem.getZ() + uz * d;
			Vec3 gnd = EarthquakeHelper.findFloor(level, new Vec3(bx, spawnY, bz), 12);
			double gy = (gnd == null ? golem.getY() : gnd.y) + 0.3;
			// 跟随光束高低差（target 高就往上排），但夹到地面以上，避免打低处时远端落到方块里看不见
			double by = Math.max(spawnY + uy * d, gy);
			for (double sgn : new double[]{-1.0, 1.0}) {
				SmallAnnihilationBombEntity bomb = new SmallAnnihilationBombEntity(bombType, level, golem, bombDamage);
				bomb.setPos(bx + perpX * sgn * 1.0, by, bz + perpZ * sgn * 1.0);
				bomb.setTurnRate(0.0F);
				// 照 boss AnnihilationExplosionEntity：水平分量 1.0、竖直 2.0+random*0.3、速度 0.5、误差 1.0
				bomb.shoot(perpX * sgn, 2.0F + golem.getRandom().nextFloat() * 0.3F, perpZ * sgn, 0.5F, 1.0F);
				bomb.setOwner(golem);
				level.addFreshEntity(bomb);
			}
		}
		return beam;
	}

	/** Boss 光束蓄力音（ANNIHILATION_LASER_CHARGE 3.0, 1.5）。 */
	public static void obliteratorLaserCharge(LivingEntity golem) {
		playAt(golem, ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0f, 1.5f);
	}

	/** Boss 光束蓄力中的吸入粒子（SMALL_ANNIHILATION_FLAME）。 */
	public static void obliteratorLaserChargeParticles(LivingEntity golem) {
		sendParticle(golem, ModParticles.SMALL_ANNIHILATION_FLAME.get(), 2.0, 2, 1.2);
	}

	/** Boss 光束开火：镜头抖动 + 光束 + 开火音。 */
	@Nullable
	public static Entity obliteratorLaserFire(LivingEntity golem, LivingEntity target, int lv, float castYaw) {
		if (golem.level().isClientSide) return null;
		skillShake(golem, golem.position(), 30.0f, 0.25f, 10, 5);
		Entity beam = spawnObliteratorLaser(golem, target, lv, castYaw);
		playAt(golem, ModSounds.ANNIHILATION_LASER_SINGLE_SHOOT.get(), 3.0f, 1.0f);
		return beam;
	}

	// Obliterator: jump ground charge quake - replicates state 22 tick 56 proxies + visuals
	/** 180° 以内的范围攻击，伤害 = base + 目标最大生命值 * maxHpPct（Δ怒吼砸击落地用）。按给定 yaw 定向。 */
	public static List<LivingEntity> areaAttackPercent(LivingEntity golem, float range, float arcDeg, float base, float maxHpPct, float launch, float yawDeg) {
		List<LivingEntity> hit = new ArrayList<>();
		if (golem.level().isClientSide) return hit;
		double half = Math.toRadians(Math.min(arcDeg, 360.0f)) / 2;
		double yaw = Math.toRadians(yawDeg);
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(range), x -> x != golem)) {
			if (!(golem instanceof AbstractGolemEntity<?, ?> _ag) || !GolemTargetUtil.canTarget(_ag, e)) continue;
			double dx = e.getX() - golem.getX();
			double dz = e.getZ() - golem.getZ();
			double d2 = dx * dx + dz * dz;
			if (d2 > range * range) continue;
			if (arcDeg < 359.9f) {
				double ang = Math.atan2(-dx, dz);
				double diff = Math.atan2(Math.sin(ang - yaw), Math.cos(ang - yaw));
				if (Math.abs(diff) > half) continue;
			}
			float dmg = base + e.getMaxHealth() * maxHpPct;
			e.hurt(golem.damageSources().mobAttack(golem), dmg);
			if (launch > 0) {
				double d = Math.max(Math.sqrt(d2), 0.001);
				e.push(dx / d * launch, 0.35, dz / d * launch);
			}
			hit.add(e);
		}
		return hit;
	}

	/**
	 * 怒吼砸击起手（Boss state 9 t20-76）：怒吼 + 推开 10 格内实体 + 瞬移到目标上方 +10。
	 * 返回施法时锁定的朝向（度），供落地等离子球 / 砸地扇形使用。
	 */
	public static float roarSmashLeap(LivingEntity golem) {
		Level level = golem.level();
		LivingEntity target = golem instanceof Mob mob ? mob.getTarget() : null;
		float castYaw = golem.yBodyRot;
		if (target != null) {
			double dx = target.getX() - golem.getX();
			double dz = target.getZ() - golem.getZ();
			castYaw = (float) (Mth.atan2(-dx, dz) * (180.0 / Math.PI));
		}
		playAt(golem, ModSounds.THE_WARPED_ONE_ROAR.get(), 2.0f, 1.0f);
		skillShake(golem, golem.position(), 20.0F, 0.15F, 30, 0);
		if (level.isClientSide) return castYaw;
		// （起手不再喷 LaserBullet 簇：之前误用该实体构造器把世界坐标当方向偏移，导致子弹一直往天上飞；boss 的逐 tick 激光弹也非必要）
		// 推开周围实体（Boss t20-30：10x3x10 内的实体被向外推）
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(10.0, 3.0, 10.0), x -> x != golem)) {
			if (!(golem instanceof AbstractGolemEntity<?, ?> _ag) || !GolemTargetUtil.canTarget(_ag, e)) continue;
			double dx = e.getX() - golem.getX();
			double dz = e.getZ() - golem.getZ();
			double len = Math.max(Math.sqrt(dx * dx + dz * dz), 0.1);
			double force = Math.min(1.0 / (len * len), 1.0);
			e.push(dx / len * force, 0.0, dz / len * force);
			e.hurtMarked = true;
		}
		// 起跳位移交给 Modifier.performJump 做真实起跳（不再瞬移到天上，避免「实体飞天 / 落到目标身上才触发」）
		return castYaw;
	}

	/**
	 * 怒吼砸击落地（Boss state 11/12 t3）：砸地 180° / 4 格，伤害 16 + 目标最大生命值 5%；
	 * 同时向锁定朝向的**左右两侧**各喷 6 颗等离子球（等离子球伤害 8，无视护甲）。
	 */
	public static void spawnObliteratorRoarSmashLanding(LivingEntity golem, int lv, float castYaw) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		// 湮灭护腿：怒吼砸击整招伤害 +50%（含砸地/等离子球/360°砸地/环形火焰）
		float mult = legsMult(golem, LMCompatRegistry.OBLITERATOR_SHINGUARD.get());
		float plasmaDamage = bossDamage(golem, lv, 8.0f) * mult;
		// 砸地范围攻击：Boss AreaAttack(4, 3, 180, 16) + 目标最大生命值 5%
		areaAttackPercent(golem, 4.0f, 180.0f, bossDamage(golem, lv, 16.0f) * mult, 0.05f * mult, 0.0f, castYaw);
		playAt(golem, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0f, 1.0f);
		skillShake(golem, golem.position(), 20.0F, 0.15F, 0, 20);
		sendRing(golem, 60.0f, 30, false, 0.4);   // 砸地向外扩散的环形粒子圈
		// 等离子球：Boss state11 —— 6 颗 45° 左旋 + 6 颗 90° 右旋，均向外盘旋（turnStrength 3~6）
		spawnPlasmaSpread(golem, 6, 45.0f, true, 3.0f + golem.getRandom().nextInt(4), plasmaDamage, castYaw);
		spawnPlasmaSpread(golem, 6, 90.0f, false, 3.0f + golem.getRandom().nextInt(4), plasmaDamage, castYaw);
		// 砸地同时触发 state20 效果：360° 范围攻击(伤害12) + 10 道环形火焰(半径8)向外扩散
		spawnObliteratorSlam360(golem, lv, mult);
	}

	/** Boss state20 的砸地效果：360°/4格 范围攻击 + 10 道环形火焰向外扩散（半径8）。 */
	private static void spawnObliteratorSlam360(LivingEntity golem, int lv, float mult) {
		Level level = golem.level();
		areaAttack(golem, 4.0f, 360.0f, bossDamage(golem, lv, 12.0f) * mult, 0.0f);
		playAt(golem, ModSounds.ENERGY_EXPLOSION.get(), 1.0f, 1.0f);
		skillShake(golem, golem.position(), 20.0F, 0.15F, 0, 20);
		sendRing(golem, 80.0f, 40, false, 0.4);   // state20 的 360° 环形粒子圈
		int floorY = Mth.floor(golem.getY());
		int amountOfFlames = 10;
		double multiplier = 8.0;
		float size = 4.0f;
		float flameDamage = bossDamage(golem, lv, 8.0f) * mult;
		for (int k = 0; k < amountOfFlames; ++k) {
			float f3 = (float) k * (float) Math.PI * size / (float) amountOfFlames + (float) Math.PI * size / 10.0f;
			spawnObliteratorFlame(golem, golem.getX() + Mth.cos(f3) * multiplier, golem.getZ() + Mth.sin(f3) * multiplier,
					floorY, golem.getY() + 1.0, f3, 2, flameDamage);
		}
	}
	/** Boss 的 spawnArmedClones：在傀儡侧前方召唤一只持械分身，冲向目标（animation 1/3，寿命 45）。 */
	private static void spawnArmedClone(LivingEntity golem, double x, double z, LivingEntity target, boolean right) {
		float rotation = (float) Math.toRadians(golem.getYRot());
		TheObliteratorCloneWithArmsEntity clone = new TheObliteratorCloneWithArmsEntity(golem.level(), x, golem.getY(), z,
				rotation, 0, golem, 16.0f, (float) target.getX(), (float) target.getY(), (float) target.getZ(),
				right ? 3 : 1, 45);
		golem.level().addFreshEntity(clone);
	}

	// Obliterator: ultimate quake - replicates state 53 proxies + visuals
	/**
	 * 湮灭浩劫的起手段（Boss state53 t2-48）：镜头抖动 + 收缩环 + 中央传送门 + 两圈小传送门 + 六道火焰。
	 * 后续的吸引、连环核爆、瞬移由 {@code ObliteratorJumpGroundChargeModifier#onAiStep} 的窗口按 tick 推进，
	 * 与 Boss 的节奏一致（不再一次性全放）。
	 */
	public static void spawnObliteratorUltimateQuake(LivingEntity golem, int lv) {
		Level level = golem.level();
		float portalDamage = bossDamage(golem, lv, 20.0f);
		float flameDamage = bossDamage(golem, lv, 17.0f);
		skillShake(golem, golem.position(), 20.0F, 0.15F, 20, 6);
		if (level instanceof ServerLevel sl) {
			ParticleOptions ring = new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 150.0f, false, Circle.EnumRingBehavior.SHRINK);
			sl.sendParticles(ring, golem.getX(), golem.getY(), golem.getZ(), 3, 0, 0, 0, 0);
			for (int i = 0; i < 20; ++i) {
				double sx = golem.getX() + (golem.getRandom().nextFloat() - 0.5) * 4.0;
				double sy = golem.getY() + golem.getRandom().nextFloat() * 2.0;
				double sz = golem.getZ() + (golem.getRandom().nextFloat() - 0.5) * 4.0;
				sl.sendParticles(ModParticles.SMALL_ANNIHILATION_FLAME.get(), sx, sy, sz, 1, 0, 0.05, 0, 0);
			}
			ParticleOptions ring2 = new Circle.RingData(0.0f, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK);
			sl.sendParticles(ring2, golem.getX(), golem.getY(), golem.getZ(), 1, 0, 0, 0, 0);
		}
		playAt(golem, ModSounds.OBLITERATOR_ARM_SHOOT.get(), 3.0f, 1.0f);   // 浩劫起手手臂音（boss t30）
		playAt(golem, ModSounds.FLAME_BURST.get(), 3.0f, 1.0f);
		if (level.isClientSide) return;
		spawnObliteratorPortal(golem, golem.getX(), golem.getZ(), golem.getY() - 5.0, golem.getY() + 5.0, 50, 12, 6.0f, portalDamage);
		doPortalEffect(golem, 15.0, 2.0f, 5, 7, 3.5f, 50, 15.0f);
		doPortalEffect(golem, 8.0, 2.0f, 5, 7, 3.5f, 50, 15.0f);
		int floorY = Mth.floor(golem.getY());
		for (int k = 0; k < 6; ++k) {
			float f3 = (float) k * (float) Math.PI * 2.0f / 6.0f + (float) Math.PI * 2.0f / 10.0f;
			spawnObliteratorFlame(golem, golem.getX() + Mth.cos(f3) * 5.0, golem.getZ() + Mth.sin(f3) * 5.0,
					floorY, golem.getY() + 1.0, f3, 2, flameDamage);
		}
	}

	/** 浩劫 t48 的第一次砸地：OMINOUS + 6 格 380° 范围攻击（伤害 12）。 */
	public static void obliteratorUltimateHit1(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		playAt(golem, ModSounds.ULTIMATE_FLAME_IMPACT.get(), 3.0f, 1.0f);
		skillShake(golem, golem.position(), 30.0F, 0.2F, 10, 5);
		areaAttack(golem, 6.0f, 380.0f, bossDamage(golem, lv, 12.0f), 0.7f);
	}

	/** 浩劫 t80 的第二次砸地：OMINOUS_EXPLOSION + 6 格 380° 范围攻击（伤害 12，与 t48 一致）。 */
	public static void obliteratorUltimateHit2(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		playAt(golem, ModSounds.OMINOUS_EXPLOSION.get(), 3.0f, 1.0f);
		skillShake(golem, golem.position(), 30.0F, 0.15F, 20, 5);
		areaAttack(golem, 6.0f, 380.0f, bossDamage(golem, lv, 12.0f), 0.7f);
	}
	// ===== Possessed Paladin =====

	/**
	 * 生成单把幻影匕首，朝目标方向偏 {@code yawOffset} 度。供 {@code BurstRangedGoal} 的"一发一把"使用：
	 * 数量与节奏由 Goal 统一控制，这里只负责一把的生成，不再自己乘 3。
	 */
	public static void spawnSinglePhantomDagger(LivingEntity golem, LivingEntity target, int lv, float yawOffset) {
		spawnSinglePhantomDagger(golem, target, lv, yawOffset, null);
	}

	/**
	 * 生成单把幻影匕首，并把它锁定的追踪目标交给调用方决定。
	 * <p>
	 * {@code homingTarget} 为 {@code null} 时退回旧行为（自己查一次、取列表首个目标）。
	 * <b>多把齐射必须逐把传入不同目标</b>，否则每把都只拿到「首个目标」，
	 * 表现就是所有匕首全追踪同一个主目标，而不是各自锁定。
	 */
	public static void spawnSinglePhantomDagger(LivingEntity golem, LivingEntity target, int lv, float yawOffset,
			@Nullable LivingEntity homingTarget) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 6.0f);
		boolean red = paladinPhaseTwo(golem);
		LivingEntity lock = homingTarget != null ? homingTarget : firstHomingTarget(golem);
		int distance = (int) golem.distanceTo(target);
		double spawnX = golem.getX();
		double spawnY = golem.getY() + golem.getBbHeight() / 2.0;
		double spawnZ = golem.getZ();
		float baseYaw = (float) Math.toDegrees(Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()));
		float yaw = baseYaw + yawOffset;
		@SuppressWarnings("unchecked")
		EntityType<ThrownPhantomDaggerEntity> type = (EntityType<ThrownPhantomDaggerEntity>) (EntityType<?>) ModEntities.THROWN_PHANTOM_DAGGER.get();
		ThrownPhantomDaggerEntity dagger = new ThrownPhantomDaggerEntity(type, golem.level());
		dagger.setOwner(golem);
		// 兜底回旋到傀儡本体（未穿头盔 / 附近无可用目标时）
		dagger.setReturnEntity(lock != null ? lock : golem);
		dagger.setReturnTick(10 + distance);
		dagger.setYRot(yaw);
		dagger.setPos(spawnX, spawnY, spawnZ);
		dagger.shootFromRotation(golem, 0.0f, yaw, 0.0f, 1.0f, 0.0f);
		dagger.setDamage(damage);
		dagger.setRed(red);
		golem.level().addFreshEntity(dagger);
	}

	/** 取该傀儡的第一个可用追踪目标（未穿堕魂头盔或无目标时返回 null）。 */
	@Nullable
	private static LivingEntity firstHomingTarget(LivingEntity golem) {
		List<LivingEntity> list = daggerHomingTargets(golem, 1);
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	/** 幻影飞刀：Boss state15/28 的 throwDaggers —— 3 把、间隔 30°、伤害 6，飞回施法者（回旋镖），returnTick = 10 + 距离。 */
	public static void spawnPhantomDaggers(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		float damage = bossDamage(golem, lv, 6.0f);
		int count = 3;
		float speed = 1.0f;
		boolean red = paladinPhaseTwo(golem);
		// 堕魂头盔：每把匕首单独锁定一个目标，改用「飞回施法者」同一套追踪逻辑去追踪该目标
		List<LivingEntity> homingTargets = daggerHomingTargets(golem, count);
		int distance = (int) golem.distanceTo(target);
		double spawnX = golem.getX();
		double spawnY = golem.getY() + golem.getBbHeight() / 2.0;
		double spawnZ = golem.getZ();
		float baseYaw = (float) Math.toDegrees(Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()));
		for (int i = 0; i < count; i++) {
			float yaw = baseYaw + (i - 1) * 30.0f;
			@SuppressWarnings("unchecked")
			EntityType<ThrownPhantomDaggerEntity> type = (EntityType<ThrownPhantomDaggerEntity>) (EntityType<?>) ModEntities.THROWN_PHANTOM_DAGGER.get();
			ThrownPhantomDaggerEntity dagger = new ThrownPhantomDaggerEntity(type, golem.level());
			dagger.setOwner(golem);
			dagger.setReturnEntity(homingTargets != null && !homingTargets.isEmpty()
					? homingTargets.get(i % homingTargets.size()) : golem);
			dagger.setReturnTick(10 + distance);
			dagger.setYRot(yaw);
			dagger.setPos(spawnX, spawnY, spawnZ);
			dagger.shootFromRotation(golem, 0.0f, yaw, 0.0f, speed, 0.0f);
			dagger.setDamage(damage);
			dagger.setRed(red);
			golem.level().addFreshEntity(dagger);
		}
	}

	/** Boss 的 DAGGER_THROW 音效。 */
	public static void paladinDaggerSound(LivingEntity golem) {
		playAt(golem, ModSounds.DAGGER_THROW.get(), 1.0f, 0.75f);
	}

	/**
	 * 堕落魂刃阵：Boss strikeZigzagXBlades(12, 1.0, falling, dmg, 4, 1, 2.0, warning)。
	 * 角度 = i * PI / (12/4) → 实际只有 3 个方向，每个方向重复 4 次；每方向 8 把。
	 * 第一遍只铺地面预警（GROUNDSOUL + SHRINK 环），第二遍才真正生成魂刃（伤害 12）。
	 */
	public static void spawnPaladinLeapBlades(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		boolean red = paladinPhaseTwo(golem);
		Level level = golem.level();
		skillShake(golem, golem.position(), 20.0F, 0.25F, 0, 20);
		playAt(golem, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.75f);
		// 范围攻击（Boss SideAreaAttack(3,3,180,24)）
		areaAttack(golem, 3.0f, 180.0f, bossDamage(golem, lv, 24.0f), 1.5f);
		if (level.isClientSide) return;
		int rune = 12;
		float divider = 4.0f;
		int dirs = (int) (rune / divider);
		double amplitude = 2.0;
		for (int dir = 0; dir < dirs; ++dir) {
			float throwAngle = (float) dir * (float) Math.PI / divider;
			float perpX = -Mth.sin(throwAngle);
			float perpZ = Mth.cos(throwAngle);
			for (int rep = 0; rep < 4; ++rep) {
				for (int k = 0; k < 8; ++k) {
					double forward = 1.25 * (k + 1);
					double lateral = amplitude * ((k % 2 == 0) ? 1 : -1);
					double xOff = Mth.cos(throwAngle) * forward + perpX * lateral;
					double zOff = Mth.sin(throwAngle) * forward + perpZ * lateral;
					Vec3 at = EarthquakeHelper.findFloor(level, new Vec3(golem.getX() + xOff, golem.getY(), golem.getZ() + zOff), 6);
					double y = at == null ? golem.getY() : at.y;
					// 预警：GROUNDSOUL + SHRINK 环
					if (level instanceof ServerLevel sl) {
						sl.sendParticles(red ? ModParticles.GROUNDSOUL_RED.get() : ModParticles.GROUNDSOUL.get(), golem.getX() + xOff, y + 2.0, golem.getZ() + zOff, 1, 0, 0, 0, 0);
						sl.sendParticles(new Circle.RingData(0.0f, 1.5707964f, 35, 0.0f, 0.9f, 0.8f, 0.8f, 15.0f, false, Circle.EnumRingBehavior.SHRINK),
								golem.getX() + xOff, y + 0.25, golem.getZ() + zOff, 1, 0, 0, 0, 0);
					}
					FallingSoulBladeEntity blade = new FallingSoulBladeEntity(level, golem.getX() + xOff, y, golem.getZ() + zOff,
							throwAngle, k + 1, golem, bossDamage(golem, lv, 12.0f), red);
					blade.setActivate(true);
					level.addFreshEntity(blade);
				}
			}
		}
	}

	/**
	 * 堕落魂盾阵：Boss state25 两波（35 / 59 tick），每波 8 面（16 点 / 2 波），
	 * 第一波半径 12、第二波半径 2；GENERIC_ARM_SWING → SOUL_SHIELD_SMASH / STAB_HIT，并各伴一次范围攻击。
	 */
	/**
	 * 烈波：Boss state19 tick38 的扇形电波 —— 5 道 ElectricityEntity，间隔 26°，伤害 6、寿命 35，
	 * 生成在y 取整的脚下、身体前侧 1 格。伤害走 INDIRECT_MAGIC，天然无视护甲与盾牌。
	 */
	public static void spawnCloudGolemWaves(LivingEntity golem, int count, float angleStep, float damage, float life, double yOffsetRatio, double cx, double cz, float yawDeg) {
		if (golem.level().isClientSide) return;
		// 傀儡从身体正中释放：Boss 那个 1 格侧移是给大体型单脚踩地用的，傀儡不需要
		// Y 必须锚到地面：傀儡跳起/站高处时 getY() 会让烈波在天上炸开（同扫戟扇形阵/后跳踏地）
		double baseY = golem.getY();
		var ground = EarthquakeHelper.findFloor(golem.level(), new Vec3(cx, golem.getY(), cz), 8);
		if (ground != null) baseY = ground.y;
		double y = yOffsetRatio < 0 ? baseY : baseY + golem.getBbHeight() * yOffsetRatio;
		for (int i = 0; i < count; i++) {
			float angle = yawDeg + (float) (i - count / 2) * angleStep;
			float rad = (float) Math.toRadians(angle);
			double dx = -Math.sin(rad);
			double dz = Math.cos(rad);
			ElectricityEntity wave = new ElectricityEntity(golem, dx, 0.0, dz, golem.level(), damage, angle, life);
			wave.setPos(cx, y, cz);
			golem.level().addFreshEntity(wave);
		}
	}

	// 烈波重踏起手：Boss state18 tick19 的镜头抖动（服务端）
	/** 浮云格挡的格挡音效/特效：护盾格挡音 + 轻微镜头抖动（护盾风格，非圣骑/湮灭配色）。 */
	public static void cloudBlockFx(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		playAt(golem, SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);
		skillShake(golem, golem.position(), 8.0F, 0.1F, 0, 6);
	}

	public static void cloudStompShake(LivingEntity golem) {
		skillShake(golem, golem.position(), 20.0F, 0.1F, 0, 20);
	}

	// Cloud Golem: skyfall smash landing effects. Boss state 15/16: camera shake plus a dust ring
	// thrown up from the landing spot, both anchored on the ground.
	public static void cloudSmashGroundFx(LivingEntity golem, double x, double y, double z) {
		// Boss state16 只做镜头抖动，不额外铺环状粒子，这里保持一致
		skillShake(golem, new Vec3(x, y, z), 20.0F, 0.1F, 0, 20);
	}

	/**
	 * 飞天重砸的落地表现（客户端），照 Boss 两段原码拼起来：
	 * <ul>
	 *   <li>state15 落地：{@code controlledSmashParticles(0, 0, 0, 7.5, 3)}</li>
	 *   <li>state16 attackParticle()（PTICKS == 2）：两圈各 360 颗方块碎屑（速度 0.9 / 1.3）
	 *       加一圈水平 GROW 环（scale 85）</li>
	 * </ul>
	 */
	public static void cloudSmashParticles(LivingEntity golem) {
		// 落地特效必须锚到地面：客户端位置是插值的，落点瞬间可能还在半空，
		// 直接用 getY() 会把碎屑/环画在「撞到目标的高度」，blockPosition().below() 此时还是空气（粒子直接看不见）。
		Vec3 at = EarthquakeHelper.findFloor(golem.level(), golem.position(), 8);
		if (at == null) at = golem.position();
		double x = at.x;
		double y = at.y;
		double z = at.z;
		ParticleUtils.controlledSmashParticles(golem, 0.0F, 0.0F, 0.0F, 7.5F, 3.0F);
		BlockState state = golem.level().getBlockState(BlockPos.containing(x, y - 0.5, z));
		BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
		for (int i = 0; i < 360; i++) {
			golem.level().addParticle(option, x + Math.cos(i), y, z + Math.sin(i),
					Math.cos(i) * 0.9, 0.0, Math.sin(i) * 0.9);
		}
		for (int i = 0; i < 360; i++) {
			golem.level().addParticle(option, x + Math.cos(i), y, z + Math.sin(i),
					Math.cos(i) * 1.3, 0.0, Math.sin(i) * 1.3);
		}
		golem.level().addParticle(new Circle.RingData(0.0F, (float) (Math.PI / 2), 30,
				1.0F, 1.0F, 1.0F, 1.0F, 85.0F, false, Circle.EnumRingBehavior.GROW),
				x, y + 0.1, z, 0.0, 0.0, 0.0);
	}

	// Cloud Golem: tornado (state 23/21) - Tornado, one per direction, aimed along the golem's facing.
	public static void spawnCloudGolemTornado(LivingEntity golem, float yaw, float damage, int life) {
		if (golem.level().isClientSide) return;
		float rad = (float) Math.toRadians(yaw);
		double dx = -Math.sin(rad);
		double dz = Math.cos(rad);
		double theta = Math.toRadians(golem.yBodyRot) + Math.PI / 2;
		double spawnX = golem.getX() + Math.cos(theta) * 1;
		double spawnZ = golem.getZ() + Math.sin(theta) * 1;
		Tornado tornado = new Tornado(golem, dx, 0, dz, golem.level(), damage, yaw, life);
		tornado.setPos(spawnX, golem.getY(0.15D), spawnZ);
		golem.level().addFreshEntity(tornado);
	}

	/**
	 * Cloud Golem 的雷击列（state31）：沿「傀儡 → 目标」方向铺 16 根雷柱，间距 1.25 格、逐根延迟落地。
	 * <p>
	 * {@code angleOffset} 是**角度（弧度）**，加到「傀儡 → 目标」的方位角上——照 Boss t37 的
	 * {@code {f - 0.3, f, f + 0.3}} 三列，以及灾变 {@code guardianStompFangsRow(angleOffset)}。
	 * 注意它不是横向平移：改它只改变张开角，雷柱起点仍在傀儡身上。
	 */
	public static void spawnCloudGolemThunderRow(LivingEntity golem, LivingEntity target, float angleOffset, float dmg) {
		if (golem.level().isClientSide || !(golem instanceof Mob mob)) return;
		double minY = Math.min(target.getY(), golem.getY());
		double maxY = Math.max(target.getY(), golem.getY()) + 2.0;
		float dir = (float) Mth.atan2(target.getZ() - golem.getZ(), target.getX() - golem.getX()) + angleOffset;
		for (int l = 0; l < 16; l++) {
			double len = 1.25 * (l + 1);
			int delay = (int) (1.25F * l);
			spawnSingleBolt(mob, golem.getX() + Mth.cos(dir) * len, golem.getZ() + Mth.sin(dir) * len, minY, maxY, dir, delay, dmg);
		}
	}

	// Cloud Golem: electric burst ring around the golem. This used to be the thunderstorm's on-hit
	// retaliation; it now belongs to the skyfall smash landing, where the boss also discharges.
	public static void spawnCloudGolemBurst(LivingEntity golem, double x, double y, double z, float damage, int n) {
		if (golem.level().isClientSide) return;
		for (int i = 0; i < n; i++) {
			double rad = Math.PI * 2 / n * i;
			double dx = Math.cos(rad);
			double dz = Math.sin(rad);
			float angle = (float) (rad * Mth.RAD_TO_DEG);
			ElectricityEntity e = new ElectricityEntity(golem, dx, 0, dz, golem.level(), damage, angle, 20.0F);
			e.setPos(x + dx, y, z + dz);
			golem.level().addFreshEntity(e);
		}
	}

	// Cloud Golem: falling cloud (state 20 / 9). The cloud itself is purely visual here: CloudEntity
	// hurts every living entity in its blast and only spares the caster, so handing it a golem owner
	// would splash allied golems and the owner's pets. Left ownerless it deals no damage at all and
	// the skill settles the blast itself through GolemTargetUtil.
	/**
	 * 带主人的云（照 Boss state9）：云自己落地按碰撞箱 inflate(attackRange) 判定、按距离衰减结算伤害，
	 * 并跳过主人本体与其驯服宠物。比无主云 + 自写 settle 更贴近 Boss（避免落点/范围对不上）。
	 */
	public static void spawnCloudGolemCloud(LivingEntity golem, double x, double y, double z, int damage, float attackRange) {
		if (golem.level().isClientSide) return;
		CloudEntity cloud = ModEntities.C.get().create(golem.level());
		if (cloud == null) return;
		cloud.setPos(x, y, z);
		cloud.setOwner(golem);
		cloud.setParticleOptimalization(true);
		cloud.setDamage(damage);
		cloud.setAttackRange(attackRange);
		golem.level().addFreshEntity(cloud);
	}

	public static void spawnCloudGolemCloud(double x, double y, double z, Level level) {
		if (level.isClientSide) return;
		CloudEntity cloud = ModEntities.C.get().create(level);
		if (cloud == null) return;
		cloud.setPos(x, y, z);
		cloud.setOwner(null);
		cloud.setParticleOptimalization(true);
		cloud.setDamage(0);
		level.addFreshEntity(cloud);
	}

	private static void spawnObliteratorFlame(LivingEntity golem, double x, double z, double minY, double maxY, float rotation, int delay, float damage) {
		Level level = golem.level();
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0;
		do {
			BlockPos low = pos.below();
			BlockState state = level.getBlockState(low);
			if (state.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState topState = level.getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(level, pos);
					if (!topShape.isEmpty()) d0 = topShape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			level.addFreshEntity(new AnnihilationFlameStrike(level, x, pos.getY() + d0, z, rotation, delay, golem, 20, damage + 2.0f));
		}
	}

	private static void spawnObliteratorPortal(LivingEntity golem, double x, double z, double minY, double maxY, int life, int warmup, float scale, float damage) {
		Level level = golem.level();
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0;
		do {
			BlockPos low = pos.below();
			BlockState state = level.getBlockState(low);
			if (state.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState topState = level.getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(level, pos);
					if (!topShape.isEmpty()) d0 = topShape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			level.addFreshEntity(new AnnihilationPortalEntity(level, x, pos.getY() + d0, z, 0.0f, warmup, golem, life, damage, true, scale));
		}
	}

	private static void doPortalEffect(LivingEntity golem, double multiplier, float size, int amount, int warmup, float scale, int life, float damage) {
		for (int k = 0; k < amount; ++k) {
			float f3 = (float) k * (float) Math.PI * size / (float) amount + (float) Math.PI * size / 10.0f;
			spawnObliteratorPortal(golem, golem.getX() + Mth.cos(f3) * multiplier, golem.getZ() + Mth.sin(f3) * multiplier, golem.getY() - 5.0, golem.getY() + 5.0, life, warmup, scale, damage);
		}
	}

		/**
	 * Boss 的 shootPlasmaBall：沿施法朝向的**左右两侧**各生成一组等离子球，向正前方射出。
	 * <p>
	 * Boss 原码把出生点放在 {@code yawRad + 1 弧度}（约 57°）的侧向，两组分别落在身体左右；
	 * 弹道方向则是 {@code yaw + (i - n/2) * step} 的窄扇形，所以看上去是「光束两旁飞出的球」。
	 * 这里同样用 {@code castYaw}（起手锁定的朝向），傀儡中途转身不会把球带到别的方向。
	 */
	/** 怒吼砸击落地后向**四周**扩散的大量等离子球（Boss state 11/12 的 shootPlasmaBall）：从傀儡中心环形散开。 */
	private static void spawnPlasmaSpread(LivingEntity golem, int count, float angleStep, boolean turnLeft, float turnStrength, float damage, float castYaw) {
		Level level = golem.level();
		double cx = golem.getX();
		double cy = golem.getEyeY() - 0.2;
		double cz = golem.getZ();
		// 出生点略微前移（Boss shootPlasmaBall：golem + 朝向*1.0）
		double thetaF = Math.toRadians(castYaw) + 1.0;
		double fwdX = Math.cos(thetaF);
		double fwdZ = Math.sin(thetaF);
		for (int i = 0; i < count; ++i) {
			float angle = castYaw + (i - count / 2.0f) * angleStep;   // 以朝向为中心的对称扇形
			float rad = (float) Math.toRadians(angle);
			double dx = -Mth.sin(rad);
			double dz = Mth.cos(rad);
			PlasmaOrbEntity orb = new PlasmaOrbEntity(golem, dx, 0.0, dz, level, 6.0F, angle, 20.0F);
			orb.setTurnLeft(turnLeft);
			orb.setTurnStrength(turnStrength);                 // 盘旋强度（Boss 用 3~6）
			orb.setPos(cx + fwdX * 1.0, cy, cz + fwdZ * 1.0);
			level.addFreshEntity(orb);
		}
	}
	/** 兼容旧调用：以傀儡当前位置/朝向为圆心（用于单发、或自身即锚点的技能）。 */
	private static void flameRadagonShockwave(LivingEntity golem, float spreadarc, int distance, float vec, int delay, float pos, float offset, float damage, boolean warningParticle, float densityK) {
		flameRadagonShockwave(golem, golem.getX(), golem.getZ(), golem.getYRot(), spreadarc, distance, vec, delay, pos, offset, damage, warningParticle, densityK);
	}

	/** 锁定圆心(cx,cz)与朝向(yawDeg)：多段技能的后续波都用起手锁定的值，不再跟随傀儡。 */
	private static void flameRadagonShockwave(LivingEntity golem, double cx, double cz, float yawDeg, float spreadarc, int distance, float vec, int delay, float pos, float offset, float damage, boolean warningParticle, float densityK) {
		Level level = golem.level();
		float f = Mth.cos(yawDeg * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(yawDeg * Mth.DEG_TO_RAD);
		double theta1 = Math.toRadians(yawDeg);
		double vecX = Math.cos(theta1 + Math.PI / 2);
		double vecZ = Math.sin(theta1 + Math.PI / 2);
		double x = cx + pos * vecX + f * offset;
		double z = cz + pos * vecZ + f1 * offset;
		double perpFacing = Math.toRadians(yawDeg) + Math.PI / 2;
		double spread = Math.PI * spreadarc;
		int arcLen = Math.max(2, Mth.ceil(distance * spread * densityK));
		for (int i = 0; i < arcLen; ++i) {
			double theta = ((double) i / (arcLen - 1.0) - 0.5) * spread + perpFacing;
			double vx = Math.cos(theta);
			double vz = Math.sin(theta);
			double px = x + vx * distance + vec * Math.cos(Math.toRadians(yawDeg + 90.0f));
			double pz = z + vz * distance + vec * Math.sin(Math.toRadians(yawDeg + 90.0f));
			int hitX = Mth.floor(px);
			int hitZ = Mth.floor(pz);
			spawnObliteratorFlame(golem, hitX + 0.5, hitZ + 0.5, golem.getY() - 5.0, golem.getY() + 3.0, (float) theta, delay, damage);
		}
	}


	/** 堕落圣骑格挡音效/特效：BLOCK 音 1.0 + CameraShake(10,0.15,5,5)（照 PossessedPaladinEntity 格挡）。 */
	public static void paladinBlockFx(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		playAt(golem, ModSounds.BLOCK.get(), 1.0f, 1.0f);
		skillShake(golem, golem.position(), 10.0F, 0.15F, 5, 5);
	}

	// ===== 堕落圣骑：扑跃砸地 / 格挡反击 / 翻滚斩 / 堕魂掷戟 / 灵魂震波 =====

	/**
	 * 圣骑灵魂环（Boss spawnCircleParticle）。配色照 Boss：一阶段 (0, 0.9, 0.8) 青绿，二阶段用红色。
	 * 不要用 sendRing —— 那是湮灭的纯绿 (0,1,0) 配色。
	 */
	private static void paladinRing(LivingEntity golem, boolean shrink, float scale) {
		if (!(golem.level() instanceof ServerLevel sl)) return;
		float r, g, b;
		if (paladinPhaseTwo(golem)) { r = 1.0f; g = 0.0f; b = 0.0f; }
		else { r = 0.0f; g = 0.9f; b = 0.8f; }
		ParticleOptions ring = new Circle.RingData(0.0f, (float) (Math.PI / 2), 100, r, g, b, 0.8f, scale,
				false, shrink ? Circle.EnumRingBehavior.SHRINK : Circle.EnumRingBehavior.GROW);
		sl.sendParticles(ring, golem.getX(), golem.getY(), golem.getZ(), 1, 0, 0, 0, 0);
	}

	/**
	 * 灵魂横扫粒子（Boss createSweep）。
	 * <p>
	 * 调用方都在服务端（技能主体），所以这里必须用 {@code ServerLevel.sendParticles} 广播，
	 * 不能用客户端专用的 {@code addParticle}——后者在服务端是空实现，特效会完全看不到。
	 */
	private static void paladinSweepFx(LivingEntity golem, boolean reverse, float scale) {
		if (!(golem.level() instanceof ServerLevel sl)) return;
		// 与 Boss createSweep 完全一致的朝向/俯仰计算
		float yaw = (float) Math.toRadians(-golem.yBodyRot + (reverse ? 20.0f : 180.0f));
		double d1 = golem.getY() + (double) (golem.getBbHeight() / 2.0F) + 1.0;
		double lookX = -Math.cos(yaw);
		double lookZ = -Math.sin(yaw);
		float pitch = (float) (reverse ? -1 : 1) * (float) Math.atan2(3.0, Math.sqrt(lookX * lookX + lookZ * lookZ));
		// 本体是 getScale() * scale；getScale() 是 vanilla Entity#getScale()，恒为 1.0。
		// 之前用 getBbWidth() 会让金属傀儡的刀光宽度翻倍，看着比 boss 大一圈。
		float size = golem.getScale() * scale;
		ParticleOptions data = paladinPhaseTwo(golem)
				? new net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle.SweepData(size, yaw, pitch)
				: new net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle.SweepData(size, yaw, pitch);
		sl.sendParticles(data, golem.getX(), d1, golem.getZ(), 1, 0, 0, 0, 0);
	}
	/** 锯齿剑刃（Boss strikeZigzagXBlades）：12 条放射 × 每条 8 格，锯齿左右偏 2 格。红色版随阶段。 */
	private static void paladinZigzagBlades(LivingEntity golem, int lv, boolean falling) {
		Level level = golem.level();
		float damage = bossDamage(golem, lv, 10.0f) * paladinLegsMult(golem);
		boolean red = paladinPhaseTwo(golem);
		int rune = 12;
		double divider = 4.0;
		double amplitude = 2.0;
		for (int i = 0; i < rune; ++i) {
			float throwAngle = (float) i * (float) Math.PI / ((float) rune / (float) divider);
			float perpX = -Mth.sin(throwAngle);
			float perpZ = Mth.cos(throwAngle);
			for (int k = 0; k < 8; ++k) {
				double forward = 1.25 * (double) (k + 1);
				int group = k % 2;
				double lateral = amplitude * (double) (group == 0 ? 1 : -1);
				double xOff = Mth.cos(throwAngle) * forward + (double) perpX * lateral;
				double zOff = Mth.sin(throwAngle) * forward + (double) perpZ * lateral;
				int delay = k + 1;
				double sx = golem.getX() + xOff;
				double sz = golem.getZ() + zOff;
				Vec3 at = EarthquakeHelper.findFloor(level, new Vec3(sx, golem.getY() + 1.0, sz), 6);
				double sy = at == null ? golem.getY() : at.y;
				if (falling) {
					level.addFreshEntity(new FallingSoulBladeEntity(level, sx, sy, sz, throwAngle, delay, golem, damage, red));
				} else {
					level.addFreshEntity(new SoulBladeEntity(level, sx, sy, sz, throwAngle, delay, golem, damage, red));
				}
			}
		}
	}

	/** 扑跃砸地落地（Boss state24）：180°/3格 24伤 + 锯齿剑刃 + 圣骑砸地特效；并附带灵魂盾。 */
	public static void paladinSlamLanding(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		float mult = paladinLegsMult(golem);
		for (LivingEntity hit : areaAttack(golem, 3.0f, 180.0f, bossDamage(golem, lv, 24.0f) * mult, 1.0f)) {
			paladinBreakArmor(hit, 1);
		}
		playAt(golem, SoundEvents.TOTEM_USE, 1.0f, 0.75f);   // Boss state24/32 的 f_12513_
		skillShake(golem, golem.position(), 20.0F, 0.25F, 0, 20);
		paladinZigzagBlades(golem, lv, false);
		paladinSmashDust(golem, 1.0f, 0.0f, 0.0f, 7.5f, 3.0f);
		// 每次砸地都附带灵魂盾（Boss state25）
		paladinSoulShieldWaves(golem, lv);
	}

	/** 扑跃砸地 phase2 的追加段（Boss state32）：从上方插下的剑刃 + 再斩。 */
	public static void paladinSlamSecond(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		for (LivingEntity hit : areaAttack(golem, 3.0f, 180.0f, bossDamage(golem, lv, 20.0f) * paladinLegsMult(golem), 1.0f)) {
			paladinBreakArmor(hit, 1);
		}
		playAt(golem, SoundEvents.TOTEM_USE, 1.0f, 0.75f);   // Boss state24/32 的 f_12513_
		skillShake(golem, golem.position(), 20.0F, 0.25F, 0, 20);
		// Boss state32：t67 先从地面冒出剑刃，t70 再从上方插下
		paladinZigzagBlades(golem, lv, false);
		paladinZigzagBlades(golem, lv, true);
		paladinSmashDust(golem, 1.0f, 0.0f, 0.0f, 7.5f, 3.0f);
	}

	/** 灵魂盾两波（Boss state25 spawnSoulShieldRing）：外圈半径 12、内圈半径 2。 */
	public static void paladinSoulShieldWaves(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		float damage = bossDamage(golem, lv, 10.0f) * paladinLegsMult(golem);
		boolean red = paladinPhaseTwo(golem);
		skillShake(golem, golem.position(), 20.0F, 0.15F, 5, 5);
		double totalRadius = 12.0;
		int totalPoints = 16;
		int wavesTotal = 2;
		int pointsThisWave = totalPoints / wavesTotal;
		double step = (Math.PI * 2.0) / totalPoints;
		double base = -0.41887902047863906;
		// Boss state25：smashTick1(t35) 先放 wave0（外圈 12 格，isOuter=false → 向中心收），
		// smashTick2(t59) 再放 wave1（内圈 2 格，isOuter=true → 向外飞出）。
		for (int wave = 0; wave < 2; ++wave) {
			double radius = wave == 0 ? totalRadius : 2.0;
			boolean isOuter = wave != 0;
			int delay = wave == 0 ? 4 : 28;
			// Boss：起手音 GENERIC_ARM_SWING 在 t28/t32/t52/t56，命中音 t35=SOUL_SHIELD_SMASH / t59=STAB_HIT
			if (wave == 0) playAt(golem, ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
			for (int i = 0; i < pointsThisWave; ++i) {
				double angle = base + (double) wave * step + (double) i * (double) wavesTotal * step;
				double spawnX = golem.getX() + Math.cos(angle) * radius;
				double spawnZ = golem.getZ() + Math.sin(angle) * radius;
				// 与 boss spawnSoulShields 一致：从 spawnY 向下扫到 minY-1 找地面
				double spawnY = paladinFloorY(level, spawnX, golem.getY() + 1.0, spawnZ, Mth.floor(golem.getY()) - 1);
				float yawRad = (float) Math.atan2(golem.getZ() - spawnZ, golem.getX() - spawnX);
				float rot = isOuter ? (float) i * ((float) Math.PI * 2F) / (float) pointsThisWave + (((float) Math.PI * 2F) / (float) pointsThisWave - 1.0F) : yawRad;
				level.addFreshEntity(new SoulShieldEntity(level, spawnX, spawnY, spawnZ, rot, delay, golem, damage,
						(float) golem.getX(), (float) golem.getY(), (float) golem.getZ(), isOuter, red));
			}
			areaAttack(golem, 3.25f, 180.0f, bossDamage(golem, lv, 20.0f) * paladinLegsMult(golem), 1.0f);
			playAt(golem, wave == 0 ? ModSounds.SOUL_SHIELD_SMASH.get() : ModSounds.STAB_HIT.get(), 1.0f, 1.0f);
			paladinRing(golem, wave == 0, 2.0f);
		}
	}
	/** 格挡反击（Boss state5）：横扫正面 180°/3.5格 20伤 + 投掷 6 把幻影匕首。 */
	/**
	 * 堕魂之刃：命中时给目标叠碎魂（SOUL_FRACTURE）+ 碎甲（BROKEN_ARMOR）。
	 * 生命值低于 65%（二阶段）时额外附带流血。
	 * <p>
	 * 碎魂照 Boss 的 {@code EntityUtil.applyStackingEffect(e, SOUL_FRACTURE, 1, 4, 200)}：每次叠 1 级、上限 4 级、10 秒。
	 * 碎甲照 {@code NewPossessedPaladin#BreakArmor}：首次 1 级、之后每次 +1 级、持续 5 秒（100 tick）。
	 */
	/** 碎魂上限：Boss 的 {@code applyStackingEffect(..., 1, 4, 200)} 最高 4 级。 */
	public static final int SOUL_FRACTURE_MAX = 4;

	/** 碎魂：照 Boss {@code EntityUtil.applyStackingEffect(e, SOUL_FRACTURE, 1, max, 200)}（+1 级、10 秒）。 */
	public static void paladinSoulFracture(LivingEntity target, int maxLevel) {
		if (target == null) return;
		EntityUtil.applyStackingEffect(target, ModEffects.SOUL_FRACTURE.get(), 1, maxLevel, 200);
	}

	/** 碎甲：照 Boss {@code NewPossessedPaladin#BreakArmor}（无则 amp、有则 +amp、100 tick）。 */
	public static void paladinBreakArmor(LivingEntity target, int amp) {
		if (target == null || amp <= 0) return;
		MobEffectInstance cur = target.getEffect(ModEffects.BROKEN_ARMOR.get());
		int lvl = cur == null ? amp : cur.getAmplifier() + amp;
		target.addEffect(new MobEffectInstance(ModEffects.BROKEN_ARMOR.get(), 100, lvl));
	}

	/** 眩晕：Boss 的 STUN（时长与等级由调用方按招式给）。 */
	public static void paladinStun(LivingEntity target, int duration, int amp) {
		if (target == null) return;
		target.addEffect(new MobEffectInstance(ModEffects.STUN.get(), duration, amp));
	}

	/**
	 * 堕魂之刃：命中叠碎魂（碎魂是攻击就有的通用效果），二阶段额外流血。
	 * 碎甲（BROKEN_ARMOR）在 boss 那边只有特定招式才施加，这里不挂。
	 */
	public static void paladinSoulBladeHit(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide || target == null) return;
		// maxLevel 是 amplifier 上限（显示等级 = amp+1）。
		// 1 级 → amp 上限 1（显示 1~2 级 buff）；2 级 → Boss 上限 amp 4（显示 1~5 级 buff）
		int max = lv >= 2 ? SOUL_FRACTURE_MAX : 1;
		paladinSoulFracture(target, max);
		if (paladinPhaseTwo(golem)) {
			// 照 Boss（ThrownHalbert）：固定 amp 0、80 tick。流血本身没有等级效果（伤害固定 1/2/3 随机），
			// Boss 也从不叠层，所以这里不跟碎魂一起叠。
			target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 80, 0));
		}
	}


	public static void paladinCounterAttack(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		// 先转向目标，横扫才会朝着目标正面（否则可能朝脚下/背后）
		if (target != null) {
			float yaw = (float) (Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()) * Mth.RAD_TO_DEG);
			golem.setYRot(yaw);
			golem.yBodyRot = yaw;
			golem.yHeadRot = yaw;
		}
		playAt(golem, ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0f, 0.75f);
		paladinSweepFx(golem, false, 2.0f);
		// Boss state5 的 SideAreaAttack：命中施加碎魂 + 碎甲 + STUN(getStunDuration()=55)
		for (LivingEntity hit : areaAttack(golem, 3.5f, 180.0f, bossDamage(golem, lv, 20.0f), 2.0f)) {
			paladinSoulFracture(hit, SOUL_FRACTURE_MAX);
			paladinBreakArmor(hit, 1);
			paladinStun(hit, 55, 0);
		}
	}

	/**
	 * 砸地尘土（Boss ParticleUtils.controlledSmashParticles）。
	 * <p>
	 * 原实现内部用 {@code level.addParticle}，在服务端是空实现 → 砸地看不到碎屑。
	 * 这里改用 {@code sendParticles} 广播方块碎屑，服务端调用即可被附近玩家看到。
	 */
	private static void paladinSmashDust(LivingEntity golem, float vec, float offset, float additionalY, float power, float r) {
		if (!(golem.level() instanceof ServerLevel sl)) return;
		float yawDeg = golem.yBodyRot;
		float f = Mth.cos(yawDeg * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(yawDeg * Mth.DEG_TO_RAD);
		double theta = yawDeg * Mth.DEG_TO_RAD + 1.0;
		double vecX = Math.cos(theta);
		double vecZ = Math.sin(theta);
		double cx = golem.getX() + (double) vec * vecX + (double) f * offset;
		double cy = golem.getY();
		double cz = golem.getZ() + (double) vec * vecZ + (double) f1 * offset;
		BlockPos pos = BlockPos.containing(cx, cy - 0.5, cz);
		BlockState state = sl.getBlockState(pos);
		if (state.isAir()) return;
		BlockParticleOption dust = new BlockParticleOption(ParticleTypes.FALLING_DUST, state);
		sl.sendParticles(dust, cx, cy + 1.0 + additionalY, cz, (int) (power / 0.03), 0.5, 0.2, 0.5, 0.2);
	}

	/** Boss state5 t30：投出幻影匕首，并补一次正面范围判定（与斩击分离）。 */
	public static void paladinDaggerVolley(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		// Boss t30 的 SideAreaAttack 音效是 f_271165_(EMPTY)，即无命中音；只有 DAGGER_THROW
		for (LivingEntity hit : areaAttack(golem, 3.5f, 180.0f, bossDamage(golem, lv, 20.0f), 2.0f)) {
			paladinSoulFracture(hit, SOUL_FRACTURE_MAX);
			paladinBreakArmor(hit, 1);
			paladinStun(hit, 55, 0);
		}
		if (target != null && target.isAlive()) paladinThrowDaggers(golem, target, lv, 6);
	}

	/** 幻影匕首扇形投掷（Boss throwDaggers）：6 把、每把间隔 30°，朝目标扇形散开。 */
	private static void paladinThrowDaggers(LivingEntity golem, LivingEntity target, int lv, int count) {
		Level level = golem.level();
		float damage = bossDamage(golem, lv, 6.0f);
		// 二阶段（含穿胸甲的「无视生命值要求」）用红色匕首；之前漏了 setRed，导致格挡反击一直丢蓝匕首
		boolean red = paladinPhaseTwo(golem);
		List<LivingEntity> homingTargets = daggerHomingTargets(golem, count);
		double dx = target.getX() - golem.getX();
		double dz = target.getZ() - golem.getZ();
		double dy = target.getY() - golem.getY();
		Vec3 flatDir = new Vec3(dx, dy, dz).normalize();
		double elevRad = Math.toRadians(dy);
		float angleBetween = 30.0f;
		double totalSpread = Math.toRadians(angleBetween * (count - 1));
		double startYaw = -totalSpread * 0.5;
		@SuppressWarnings("unchecked")
		EntityType<ThrownPhantomDaggerEntity> type = (EntityType<ThrownPhantomDaggerEntity>) (EntityType<?>) ModEntities.THROWN_PHANTOM_DAGGER.get();
		for (int i = 0; i < count; ++i) {
			double yawOff = startYaw + Math.toRadians((float) i * angleBetween);
			// 绕 Y 轴旋转平面方向
			double cosY = Math.cos(yawOff), sinY = Math.sin(yawOff);
			double rx = flatDir.x * cosY + flatDir.z * sinY;
			double rz = -flatDir.x * sinY + flatDir.z * cosY;
			double cosP = Math.cos(elevRad);
			double sinP = Math.sin(elevRad);
			double vx = rx * cosP, vy = sinP, vz = rz * cosP;
			ThrownPhantomDaggerEntity dagger = new ThrownPhantomDaggerEntity(type, level);
			dagger.setPos(golem.getX(), golem.getY() + golem.getBbHeight() / 2.0, golem.getZ());
			// 兜底回旋到傀儡本体：未穿堕魂头盔时 daggerHomingTargets 返回 null，
			// 少了这个兜底 rEntity 一直是 null，匕首的折返条件 `returnEntity() != null` 不成立，会一直往前飞不回来。
			dagger.setReturnEntity(homingTargets != null && !homingTargets.isEmpty()
					? homingTargets.get(i % homingTargets.size()) : golem);
			dagger.setReturnTick(10);
			dagger.setDamage(damage);
			dagger.setRed(red);
			dagger.shoot(vx, vy, vz, 1.0f, 0.0f);
			level.addFreshEntity(dagger);
		}
		playAt(golem, ModSounds.DAGGER_THROW.get(), 1.0f, 0.75f);
	}
	/** 翻滚斩起手（Boss state29 t4）：翻滚音 + 位移。 */
	public static void paladinRollStart(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		playAt(golem, ModSounds.POSSESSED_PALADIN_ROLL.get(), 1.0f, 1.0f);
	}

	/** 与 boss spawnSoulPillars 一致的地面向下扫描：从 y 到 lowestY 找可站立面，返回柱底 Y。 */
	/** 二阶段判定：统一走 {@link LMArmorHelper}，穿灵魂骑士胸甲时恒为二阶段。 */
	private static boolean paladinPhaseTwo(LivingEntity golem) {
		return golem instanceof AbstractGolemEntity<?, ?> ag
				&& GolemFormAppearance.visualFinal(ag, LMArmorHelper.paladinFinalForm(ag), LMArmorHelper.PHASE_TWO);
	}

	/** 堕魂护腿：扑跃砸地 / 翻滚斩整招伤害 +50%。 */
	private static float paladinLegsMult(LivingEntity golem) {
		return legsMult(golem, LMCompatRegistry.PALADIN_SHINGUARD.get());
	}

	/** 堕魂头盔：为每把幻影匕首挑一个（尽量不重复的）目标；未穿头盔返回 null。 */
	@Nullable
	private static List<LivingEntity> daggerHomingTargets(LivingEntity golem, int count) {
		if (!(golem instanceof AbstractGolemEntity<?, ?> ag)) return null;
		if (!LMArmorHelper.paladinHelmet(ag)) return null;
		LivingEntity main = golem instanceof Mob mob ? mob.getTarget() : null;
		var list = golem.level().getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(32),
				e -> e != golem && GolemTargetUtil.canTarget(ag, e));
		list.sort(Comparator.comparing((LivingEntity e) -> e == main ? 0 : 1));
		List<LivingEntity> ans = new ArrayList<>();
		for (LivingEntity e : list) {
			if (ans.size() >= count) break;
			ans.add(e);
		}
		return ans;
	}

	private static double paladinFloorY(Level level, double x, double y, double z, int lowestY) {
		BlockPos pos = BlockPos.containing(x, y, z);
		while (pos.getY() >= lowestY) {
			BlockPos below = pos.below();
			if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
				double d0 = 0.0;
				BlockState st = level.getBlockState(pos);
				if (!st.isAir()) {
					VoxelShape shape = st.getCollisionShape(level, pos);
					if (!shape.isEmpty()) d0 = shape.max(Direction.Axis.Y);
				}
				return pos.getY() + d0;
			}
			pos = below;
		}
		return y;
	}

	/** 单根灵魂柱，坐标照 boss spawnSoulPillar：vec 沿朝向、offset 沿侧向、d2 再侧偏 1.25。 */
	/**
	 * 单根灵魂柱，坐标照 boss spawnSoulPillar。
	 * <p>
	 * <b>不能用 golem.yBodyRot</b>：翻滚斩是旋转冲锋，yBodyRot 每 tick 都在转，
	 * 拿它算柱子方向会让柱子绕着傀儡打转、排不成网格。这里改由调用方传入
	 * <b>锁定的冲锋朝向</b>（castYaw），与位移方向一致，柱子才会沿直线整齐铺开。
	 */
	private static void paladinSpawnPillar(LivingEntity golem, int lv, float castYaw, float vec, float offset) {
		Level level = golem.level();
		float f = Mth.cos(castYaw * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(castYaw * Mth.DEG_TO_RAD);
		double theta = castYaw * Mth.DEG_TO_RAD + 1.0;
		double vecX = Math.cos(theta);
		double vecZ = Math.sin(theta);
		int standingOnY = Mth.floor(golem.getY()) - 1;
		double headY = golem.getY() + 1.0;
		float yawRadians = (float) Math.toRadians(90.0 + castYaw);
		double d2 = 1.25;
		double sx = golem.getX() + (double) vec * vecX + (double) f * offset + (double) Mth.cos(yawRadians) * d2;
		double sz = golem.getZ() + (double) vec * vecZ + (double) f1 * offset + (double) Mth.sin(yawRadians) * d2;
		double sy = paladinFloorY(level, sx, headY, sz, standingOnY);
		level.addFreshEntity(new SoulPillarEntity(level, sx, sy, sz, yawRadians, 0, golem, 20, bossDamage(golem, lv, 8.0f) * paladinLegsMult(golem), paladinPhaseTwo(golem)));
	}

	/**
	 * 翻滚斩沿途的灵魂柱（Boss state29）：每 3 tick 调 spawnSoulPillar(-1, 0/2/-2, 1)，
	 * 即「三根一组、侧向间隔 2 格」。朝向用锁定的冲锋方向 castYaw，不跟随旋转。
	 */
	public static void paladinRollPillar(LivingEntity golem, int lv, float castYaw) {
		if (golem.level().isClientSide) return;
		paladinSpawnPillar(golem, lv, castYaw, -1.0f, 0.0f);
		paladinSpawnPillar(golem, lv, castYaw, -1.0f, 2.0f);
		paladinSpawnPillar(golem, lv, castYaw, -1.0f, -2.0f);
	}
	/** 翻滚斩的 360° 斩（Boss t41 / t54）：全体范围 + 横扫粒子。 */
	public static void paladinRollSlash(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		paladinSweepFx(golem, true, 2.0f);
		for (LivingEntity hit : areaAttack(golem, 5.0f, 360.0f, bossDamage(golem, lv, 17.0f) * paladinLegsMult(golem), 1.0f)) {
			paladinSoulFracture(hit, SOUL_FRACTURE_MAX);
			paladinBreakArmor(hit, 1);
		}
		playAt(golem, ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0f, 0.75f);
		skillShake(golem, golem.position(), 20.0F, 0.05F, 0, 10);
	}

	/** 堕魂掷戟（Boss state38 t28）：投出灵魂三叉戟。 */
	public static void paladinThrowTrident(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		SoulTridentEntity trident = new SoulTridentEntity(golem.level(), golem, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TRIDENT));
		double dx = target.getX() - golem.getX();
		// 照 Boss throwSoulTrident：瞄准身体 1/3 高度（不是眼睛），散布随难度递减
		double dy = target.getY(0.3333333333333333D) - trident.getY();
		double dz = target.getZ() - golem.getZ();
		double dh = Math.sqrt(dx * dx + dz * dz);
		trident.shoot(dx, dy + dh * 0.2, dz, 1.0f, (float) (14 - golem.level().getDifficulty().getId() * 4));
		playAt(golem, SoundEvents.DROWNED_SHOOT, 1.0f, 0.75f);   // Boss throwSoulTrident 的 f_11821
		golem.level().addFreshEntity(trident);
	}

	/** 堕魂掷戟的扇形冲击波（Boss state38 t69-83 的 flameRadagonShockwave）：向外逐圈扩散。 */
	public static void paladinTridentWave(LivingEntity golem, int lv, int distance, double cx, double cz, float yawDeg) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		float spreadarc = 0.2f;
		// Boss spawnSoulPillars 内部写死 8.0F（flameRadagonShockwave 传的 damage 参数并未被使用）
		float damage = bossDamage(golem, lv, 8.0f);
		float f = Mth.cos(yawDeg * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(yawDeg * Mth.DEG_TO_RAD);
		double theta1 = yawDeg * Mth.DEG_TO_RAD + 1.0;
		double vecX = Math.cos(theta1);
		double vecZ = Math.sin(theta1);
		double x = cx;
		double z = cz;
		double facingAngle = yawDeg * Mth.DEG_TO_RAD + Math.PI / 2.0;
		double spread = Math.PI * (double) spreadarc;
		int arcLen = Mth.ceil((double) distance * spread);
		int standingOnY = Mth.floor(golem.getY()) - 1;
		for (int i = 0; i < arcLen; ++i) {
			double theta = ((double) i / ((double) arcLen - 1.0) - 0.5) * spread + facingAngle;
			double px = x + Math.cos(theta) * (double) distance;
			double pz = z + Math.sin(theta) * (double) distance;
			double sy = paladinFloorY(level, px, golem.getY(), pz, standingOnY);
			// Boss flameRadagonShockwave 用的是灵魂柱（不是剑刃）
			// 照 Boss：落点对齐到方块中心（floor+0.5），warmupDelayTicks = 5
			int hitX = Mth.floor(px);
			int hitZ = Mth.floor(pz);
			level.addFreshEntity(new SoulPillarEntity(level, hitX + 0.5, sy, hitZ + 0.5, (float) theta, 5, golem, 20, damage, paladinPhaseTwo(golem)));
		}
	}

	/** 灵魂震波（Boss state31 的 SoulStrike）：15 道放射灵魂冲击，一次铺满一圈。 */
	public static void paladinSoulShockwave(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		Level level = golem.level();
		float damage = bossDamage(golem, lv, 12.0f);
		boolean red = paladinPhaseTwo(golem);
		int quake = 15;
		float angle = 360.0f / (float) quake;
		for (int i = 0; i < quake; ++i) {
			SoulStrike strike = new SoulStrike(level, golem, red);
			strike.setDamage(damage);
			strike.shootFromRotation(golem, 0.0f, angle * (float) i, 0.0f, 0.45f, 0.0f);
			strike.setPos(golem.getX(), golem.getY() + 0.3, golem.getZ());
			level.addFreshEntity(strike);
		}
		playAt(golem, SoundEvents.WITHER_SHOOT, 1.0f, 1.0f);   // Boss state31 原音效（f_12558）
		skillShake(golem, golem.position(), 20.0F, 0.1F, 5, 5);
	}
}

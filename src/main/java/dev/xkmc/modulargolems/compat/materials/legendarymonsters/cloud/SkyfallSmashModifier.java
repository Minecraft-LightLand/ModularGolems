package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemUtils;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;
import java.util.List;

/**
 * 飞天重砸（对应 Cloud_Golem state 17 起跳 → 15 落地 → 16 砸地）：
 * 落地时以**地面落点**为中心 360° 冲击波，伤害 = 攻击力 + 目标最大生命 8%，破盾，
 * 并在落点释放一圈电爆（原雷暴技能的受击反击移到这里）。
 * <p>
 * 冲击波中心必须投影到地面：傀儡跳向目标后停下来时，脚下可能正踩着目标，
 * 直接拿 {@code golem.position()} 当中心会让特效和伤害区域整体偏高、偏离落点
 * （同一处理见灾变的 {@code ScyllaAnchorSlamModifier}）。
 */
public class SkyfallSmashModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public static final int COOLDOWN = 200;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 20;

	/** 落地冲击波半径（格），对应 Boss state16 的 AreaAttack(6.5F, ...)。 */
	public static final double SMASH_RADIUS = 6.5;
	/** 冲击波伤害里目标最大生命的占比，对应 Boss 的 0.08。 */
	public static final float HEALTH_RATIO = 0.08F;
	/** 电爆环里每根电弧的伤害系数（乘傀儡攻击力）。 */
	public static final float BURST_DAMAGE = 0.6F;

	public SkyfallSmashModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
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
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		Vec3 at = EarthquakeHelper.findFloor(golem.level(), golem.position(), 16);
		if (at == null) at = golem.position();
		SkillSound.playQuakeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
		LMProxy.cloudSmashGroundFx(golem, at.x, at.y, at.z);

		var src = golem.damageSources().mobAttack(golem);
		float atk = GolemCombatHelper.getAttackDamage(golem);
		// 浮云护腿：飞天重砸伤害 +50%
		float mult = LMArmorHelper.legsMult(golem, LMCompatRegistry.CLOUD_SHINGUARD.get());
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, smashArea(at))) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (horizontalDistSqr(e, at) > SMASH_RADIUS * SMASH_RADIUS) continue;
			float dmg = GolemUtils.adjustedDamage(atk, e.getMaxHealth() * HEALTH_RATIO) * mult;
			boolean hit = GolemCombatHelper.meleeHit(golem, e, src, dmg);
			if (hit) {
				SkillSound.playChargeHit(golem);
				// 复刻 IAnimatedMonster#launch(entity, true)：直接 push，无视击退抗性
				double dx = e.getX() - golem.getX();
				double dz = e.getZ() - golem.getZ();
				double d2 = Math.max(dx * dx + dz * dz, 0.0001);
				e.push(dx / d2 * 2.0, 0.5, dz / d2 * 2.0);
			}
		}
		// 落点电爆暂停启用：等后续加上专属装备再投放。恢复时把下面这行注释放开即可
		// （LMProxy.spawnCloudGolemBurst 与 BURST_DAMAGE 常量都保留着）。
		// LMProxy.spawnCloudGolemBurst(golem, at.x, at.y, at.z, atk * BURST_DAMAGE, 6);
	}

	/** 冲击波判定区域：以地面落点为中心，竖直方向留出目标身高的余量。 */
	private static AABB smashArea(Vec3 at) {
		return new AABB(
				at.x - SMASH_RADIUS, at.y - 2, at.z - SMASH_RADIUS,
				at.x + SMASH_RADIUS, at.y + 4, at.z + SMASH_RADIUS);
	}

	private static double horizontalDistSqr(LivingEntity e, Vec3 at) {
		double dx = e.getX() - at.x;
		double dz = e.getZ() - at.z;
		return dx * dx + dz * dz;
	}

	/**
	 * 下落阶段：Boss state15 在没落地时每 tick 调 {@code Particle(30, x, y, z)}，即在自身位置撒一圈
	 * 云粒子。这一步只在客户端画，所以走 onClientTick。
	 */
	@Override
	public void onClientTick(AbstractGolemEntity<?, ?> golem, int level) {
		if (!EarthquakeHelper.isLeaping(golem)) return;
		for (int i = 0; i < 360; i += 30) {
			golem.level().addParticle(ParticleTypes.CLOUD, golem.getX(), golem.getY(), golem.getZ(),
					Math.cos(i) * 0.25, 0.0, Math.sin(i) * 0.25);
		}
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		// 落地粒子只在客户端画（Level#addParticle 在服务端是空实现）
		if (!golem.level().isClientSide) return;
		if (event == EarthquakeHelper.FLAG) LMProxy.cloudSmashParticles(golem);
	}

	/**
	 * 把落点投影到脚下的实体方块顶面。整段下探都找不到实心面时返回 null，由调用方退回傀儡自身位置。
	 */

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return (double) MAX_RANGE * MAX_RANGE;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}

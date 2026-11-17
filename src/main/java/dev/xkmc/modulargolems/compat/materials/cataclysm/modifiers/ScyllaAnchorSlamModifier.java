package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class ScyllaAnchorSlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {
	public static final int COOLDOWN = 160;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;
	/** 坠雷击各段伤害（直伤 + 35 根雷柱 + 闪电重砸）自落地起延续约 25 tick，这里留 15 tick 余量。 */
	public static final int BYPASS_WINDOW = 40;
	private static final String KEY_BYPASS_UNTIL = "ScyllaSlamBypassUntil";

	public ScyllaAnchorSlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/** 坠雷击穿透窗口的截止 gameTime；没开窗时为 0。 */
	public static long slamBypassUntil(AbstractGolemEntity<?, ?> golem) {
		return golem.getPersistentData().getLong(KEY_BYPASS_UNTIL);
	}

	/**
	 * 风暴护腿：坠雷击的直伤（本体直接结算的那一下）无视目标无敌帧。
	 * 雷柱 / 落雷与电火花落地生成的静电场都不放行。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		ScyllaInvulnerabilityBypass.onAnchorSlamAttack(entity, event);
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

	/**
	 * 风暴护腿：所有技能留下的电火花（cataclysm:spark 及其落地后的闪电区域）伤害提高。
	 */
	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		if (event == null) return;
		if (!CataArmorHelper.stormLegs(entity)) return;
		if (!ScyllaInvulnerabilityBypass.isSpark(event.getSource())) return;
		cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.sparkArmorBonus.get().floatValue()));
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var ans = EarthquakeHelper.findFloor(golem.level(), golem.position(), 16);
		if (ans == null) return;
		// 开窗：后续各段伤害都在这段时间内结算，onAttackTarget 靠它判断是否属于坠雷击
		golem.getPersistentData().putLong(KEY_BYPASS_UNTIL, golem.level().getGameTime() + BYPASS_WINDOW);
		GolemCataProxy.scyllaAnchorSlamFX(golem);
		// 本体 AnchorThrow（attackState 13）按阶段给火花数量：满血 2 道、phase1（≤2/3）4 道、phase2（≤1/3）6 道。
		// 风暴胸甲把傀儡钉在最终形态，直接按 6 道算。
		int bolts;
		if (CataArmorHelper.stormChest(golem) || CataArmorHelper.isBelow(golem, CataArmorHelper.ONE_THIRD)) {
			bolts = 6;
		} else if (CataArmorHelper.isBelow(golem, CataArmorHelper.TWO_THIRDS)) {
			bolts = 4;
		} else {
			bolts = 2;
		}
		// 风暴护腿：坠雷击的攻击范围 +N%（雷柱阵列的排布间距不变，只放大每根雷柱的判定）
		float range = anchorSlamRangeScale(golem);
		GolemCataProxy.scyllaFallingBurstAt(golem, ans.x, ans.y, ans.z, bolts, range);
		// 风暴护腿：完整形态（半血以下，或被风暴胸甲钉住）时额外触发一次闪电重砸
		if (CataArmorHelper.stormLegs(golem) && CataArmorHelper.stormFinalForm(golem)) {
			lightningSmash(golem, ans, range);
		}
	}

	/**
	 * 风暴护腿：坠雷击的攻击范围倍率。{@code +100%} 即 ×2，同时作用于坠雷击的雷柱与闪电重砸的落雷。
	 */
	public static float anchorSlamRangeScale(AbstractGolemEntity<?, ?> golem) {
		if (!CataArmorHelper.stormLegs(golem)) return 1.0F;
		return 1.0F + MGConfig.COMMON.anchorSlamRangeBonus.get().floatValue();
	}

	/**
	 * 闪电重砸：复刻本体 state 20（锚暴摔）的落点效果——一次落雷结算，外加一圈电火花飞溅出去。
	 * <p>
	 * 本体没有"独立铺开静电场"的代码：静电场全部来自 {@code Spark_Entity} 落地后自己生成的
	 * {@code Lightning_Area_Effect_Entity}。所以这里也只抛火花，不额外排环。
	 */
	private static void lightningSmash(AbstractGolemEntity<?, ?> golem, Vec3 at, float range) {
		// 落雷 + 范围伤害（无敌帧由护腿特性的 onAttackTarget 统一处理）
		GolemCataProxy.scyllaLightningExplosion(golem, at.x, at.y, at.z, range);
		// 电火花四散落地：必须从落点抛（本体是原地砸地，火花从脚下飞溅；傀儡是跳过去的，
		// 用 golem 自身坐标会让火花全冒在傀儡脚下，看着像"没飞出去"）
		for (int i = 0; i < 8; i++) {
			GolemCataProxy.shootSparkAt(golem, at.x, at.y, at.z, 2.0F * range);
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}



	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				EarthquakeHelper.leapReduction(),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS)).withStyle(ChatFormatting.GREEN));
	}

}

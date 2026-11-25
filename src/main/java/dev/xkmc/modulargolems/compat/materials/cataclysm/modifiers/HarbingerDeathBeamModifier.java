package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HarbingerDeathBeamModifier extends GolemModifier {

	public static final int COOLDOWN = 400;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 35;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 释放复仇导弹 / 榴弹炮 / 冲锋后，缩短「死亡光束」剩余冷却的 tick 数（4 秒）。 */
	public static final int BEAM_REDUCTION_TICKS = 80;
	private static final String KEY_REDUCTION = "HarbingerBeamReduction";

	/**
	 * 由其它技能调用：累积一次冷却缩减，由光束 Goal 的 {@code tick()} 取走并直接扣到
	 * {@code BaseRangedAttackGoal.attackTime} 上。与暴怒冲锋的
	 * {@code addChargeReduction} / {@code takeChargeReduction} 是同一套写法。
	 */
	public static void addBeamReduction(AbstractGolemEntity<?, ?> golem) {
		var data = golem.getPersistentData();
		data.putInt(KEY_REDUCTION, data.getInt(KEY_REDUCTION) + BEAM_REDUCTION_TICKS);
	}

	/** 取出并清零已累积的缩减（tick）。 */
	public static int takeBeamReduction(AbstractGolemEntity<?, ?> golem) {
		var data = golem.getPersistentData();
		int val = data.getInt(KEY_REDUCTION);
		if (val != 0) data.putInt(KEY_REDUCTION, 0);
		return val;
	}

	public HarbingerDeathBeamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerDeathBeamAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (!GolemCataProxy.isLaser(event.getSource())) return;
		if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.HARBINGER_HELMET.get())) return;
		event.getTarget().invulnerableTime = 0;
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		var source = cache.getSource();
		if (GolemCataProxy.isLaser(source) && entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.HARBINGER_HELMET.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.laserArmorBonus.get().floatValue(), ModularGolems.loc("harbinger_armor")));
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc",
						CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_CD_REDUCTION.get(
						CataArmorHelper.skillList(
								CataCompatRegistry.HARBINGER_MISSILE.get(),
								CataCompatRegistry.HARBINGER_HOWITZER.get(),
								CataCompatRegistry.HARBINGER_CHARGE.get()),
						CataArmorHelper.number(BEAM_REDUCTION_TICKS / 20))
						.withStyle(ChatFormatting.GREEN));
	}

}

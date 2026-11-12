package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class AncientRemnantChargeModifier extends GolemModifier implements ChargeHelper.Modifier {
	public static final int COOLDOWN = 400;

	public static final int MAX_TICKS = 60;
	public static final double MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 释放尾骨震击 / 遗魂沙暴 / 裂地重踏后，缩短「暴怒冲锋」剩余冷却的 tick 数（4 秒）。 */
	public static final int CHARGE_REDUCTION_TICKS = 80;
	private static final String KEY_REDUCTION = "AncientChargeReduction";

	/**
	 * 由其它技能调用：累积一次冷却缩减，等 {@link #onAiStep} 结算。
	 * 与死亡光束的 {@code addBeamReduction} / {@code takeBeamReduction} 是同一套写法。
	 */
	public static void addChargeReduction(AbstractGolemEntity<?, ?> golem) {
		var data = golem.getPersistentData();
		data.putInt(KEY_REDUCTION, data.getInt(KEY_REDUCTION) + CHARGE_REDUCTION_TICKS);
	}

	/** 取出并清零已累积的缩减（tick）。 */
	public static int takeChargeReduction(AbstractGolemEntity<?, ?> golem) {
		var data = golem.getPersistentData();
		int val = data.getInt(KEY_REDUCTION);
		if (val != 0) data.putInt(KEY_REDUCTION, 0);
		return val;
	}

	/**
	 * 结算累积的缩减：冲锋冷却以 {@code <modifierId>:timestamp} 存储、就绪时刻 = 时间戳 + 冷却，
	 * 所以要把时间戳<b>往前推</b>（往后再推反而会延长），并最低收敛到「现在」，不会减成负数。
	 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		int reduction = takeChargeReduction(golem);
		if (reduction <= 0) return;
		var data = golem.getPersistentData();
		String key = getID() + ":timestamp";
		if (!data.contains(key)) return;
		long stamp = data.getLong(key);
		long now = golem.level().getGameTime();
		int cd = getCooldown(golem, level);
		long readyAt = stamp + cd;
		if (stamp > now || readyAt <= now) return;
		data.putLong(key, Math.max(now, readyAt - reduction) - cd);
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		// 远古金属胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半
		return CataArmorHelper.ancientFastSkill(golem) ? COOLDOWN / 2 : COOLDOWN;
	}


	public AncientRemnantChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new AncientRemnantChargeAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (AncientRemnantChargeAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(AncientRemnantChargeAttackGoal.KEY_ACTIVE, on);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.remnantRoarParticle(golem, 1.0F, 0.0F, (float) golem.getBbHeight() * 0.5F);
		GolemCataProxy.remnantChargeSound(golem);
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		if (golem.tickCount % 3 == 0) {
			GolemCataProxy.remnantRoarParticle(golem, 1.0F, 0.0F, (float) golem.getBbHeight() * 0.5F);
			GolemCataProxy.remnantChargeStep(golem);
		}
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		return atk * 2 + Math.min(atk * 2, target.getMaxHealth() * 0.1f);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc",
						ChargeHelper.chargeReduction(),
						CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_CD_REDUCTION.get(
						CataArmorHelper.skillList(
								CataCompatRegistry.ANCIENT_RENANT_LEAP.get(),
								CataCompatRegistry.SANDSTORM.get(),
								CataCompatRegistry.ANCIENT_RENANT_STOMP.get()),
						CataArmorHelper.number(CHARGE_REDUCTION_TICKS / 20))
						.withStyle(ChatFormatting.GREEN));
	}

}

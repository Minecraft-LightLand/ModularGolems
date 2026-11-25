package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;


import java.util.function.BiConsumer;

public class HarbingerChargeModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 200;
	public static final int MAX_TICKS = 60;
	public static final double MAX_RANGE = 16;
	public static final int MIN_RANGE = 0;
	public static final double CLIMB_BOOST = 2.5;

	/** 最大连续冲锋段数（本体三连冲）。 */
	public static final int MAX_DASH = 3;

	/** 落地等待上限（tick）。 */
	public static final int LANDING_TIMEOUT = 60;

	public HarbingerChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new HarbingerChargeAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (HarbingerChargeAttackGoal.isCharging(entity)) {
			event.addDealtModifier(DamageModifier.multTotal(ChargeHelper.CHARGE_DAMAGE_MULT, getRegistryName()));
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(HarbingerChargeAttackGoal.KEY_ACTIVE, on);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.harbingerChargePrepare(golem);
		GolemCataProxy.harbingerChargeSound(golem);
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		if (golem.tickCount % 4 == 0) {
			ChargeHelper.spawnChargeParticles(golem, golem.getLookAngle(), ParticleTypes.SMOKE);
		}
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		return atk * 1.1F + Math.min(atk * 1.1F, target.getMaxHealth() * 0.05F);
	}

	/**
	 * 爬升型冲锋，目标可以远高于自身。
	 */
	@Override
	public double getMaxTargetHeight(AbstractGolemEntity<?, ?> golem, int lv) {
		return 4.0;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var dashes = Component.literal("" + MAX_DASH).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction(), dashes, CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}

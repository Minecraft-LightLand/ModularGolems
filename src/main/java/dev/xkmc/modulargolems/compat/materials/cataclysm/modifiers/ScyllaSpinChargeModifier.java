package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
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

public class ScyllaSpinChargeModifier extends GolemModifier implements ChargeHelper.Modifier {
	public static final int COOLDOWN = 200;

	public static final int MAX_TICKS = 60;
	public static final float SPIN_SPEED = 15.0F;
	public static final double MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}


	public ScyllaSpinChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new ScyllaSpinChargeAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (ScyllaSpinChargeAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(ScyllaSpinChargeAttackGoal.KEY_ACTIVE, on);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.scyllaStormParticles(golem);
		GolemCataProxy.scyllaSpinSound(golem);
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		if (golem.tickCount % 2 == 0) {
			GolemCataProxy.scyllaStormParticles(golem);
		}
		if (golem.tickCount % 10 == 0) {
			GolemCataProxy.scyllaSpin(golem);
			GolemCataProxy.scyllaSpinSound(golem);
			GolemCataProxy.screenShake(golem, 20.0F);
		}
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		return atk + Math.min(atk, target.getMaxHealth() * 0.05F);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}

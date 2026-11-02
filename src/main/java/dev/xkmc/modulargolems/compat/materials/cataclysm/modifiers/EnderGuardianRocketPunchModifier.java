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

public class EnderGuardianRocketPunchModifier extends GolemModifier implements ChargeHelper.Modifier {
	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 160;
	}


	public EnderGuardianRocketPunchModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new EnderGuardianRocketPunchGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (EnderGuardianRocketPunchGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(EnderGuardianRocketPunchGoal.KEY_ACTIVE, on);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		GolemCataProxy.guardianExplosionSound(ins.owner());
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.gravityPull(golem);
		if (golem.tickCount % 20 == 0) {
			EnderGuardianRocketPunchGoal.fireBulletFan(golem);
		}
	}

	@Override
	public void endCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.rocketPunch(golem, golem.getTarget());
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		return atk * 1.1F + Math.min(atk * 1.1F, target.getMaxHealth() * 0.05F);
	}

	@Override
	public void onChargeTouch(ChargeHelper.Instance ins, LivingEntity target) {
		GolemCataProxy.inflictStun(ins.owner(), target, EnderGuardianRocketPunchGoal.STUN_TICKS);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}

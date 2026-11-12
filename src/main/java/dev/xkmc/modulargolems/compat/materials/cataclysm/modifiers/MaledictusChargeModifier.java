package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.init.GolemParticles;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class MaledictusChargeModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 200;

	public static final int MAX_TICKS = 60;
	public static final double MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public MaledictusChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new MaledictusChargeAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (MaledictusChargeAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(MaledictusChargeAttackGoal.KEY_ACTIVE, on);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.summonSoulRing(golem);
		GolemCataProxy.maledictusShortRoar(golem);
		GolemCataProxy.maledictusChargeSound(golem);
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		GolemCataProxy.summonSoulRing(golem);
		GolemParticles.spawnAfterimage(golem);
	}

	@Override
	public ParticleOptions getChargeParticle() {
		return ParticleTypes.SOUL;
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		return atk * 1.1F + Math.min(atk * 1.1F, target.getMaxHealth() * 0.05F);
	}

	public static int cooldown(AbstractGolemEntity<?, ?> golem) {
		if (CataArmorHelper.maledictusLegs(golem))
			return COOLDOWN / 2;
		return COOLDOWN;
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return cooldown(golem);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var dashes = Component.literal("" + MaledictusChargeAttackGoal.MAX_DASH).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction(), dashes).withStyle(ChatFormatting.GREEN));
	}

}

package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class PoseidonRiptideModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 100;

	public PoseidonRiptideModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new PoseidonRiptideChargeGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (PoseidonRiptideChargeGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(PoseidonRiptideChargeGoal.KEY_ACTIVE, on);
	}

	@Override
	public boolean canCharge(AbstractGolemEntity<?, ?> golem, int lv) {
		return golem.isInWaterRainOrBubble();
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public boolean hasChargeParticle() {
		return false;
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		golem.playSound(SoundEvents.TRIDENT_RIPTIDE_1, 1.0F, 1.0F);
		golem.setRiptideSpin(true);
		golem.fallDistance = 0;
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		ins.owner().fallDistance = 0;
	}

	@Override
	public void endCharge(ChargeHelper.Instance ins) {
		ins.owner().setRiptideSpin(false);
	}

	@Override
	public double getChargeDistance(AbstractGolemEntity<?, ?> golem, int lv) {
		return 10;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}

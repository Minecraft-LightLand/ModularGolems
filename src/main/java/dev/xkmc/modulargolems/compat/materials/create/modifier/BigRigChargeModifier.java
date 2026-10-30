package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.modulargolems.util.GolemCombatHelper;
import com.simibubi.create.AllSoundEvents;
import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.SkillSound;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class BigRigChargeModifier extends GolemModifier implements ChargeHelper.Modifier {

	public BigRigChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new BigRigChargeGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (BigRigChargeGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(BigRigChargeGoal.KEY_ACTIVE, on);
	}

	@Override
	public ParticleOptions getChargeParticle() {
		return ParticleTypes.CLOUD;
	}

	@Override
	public boolean hasChargeParticle() {
		return true;
	}

	@Override
	public boolean hasChargeSmoke() {
		return true;
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		SkillSound.playCharge(golem, SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F);
		SkillSound.playCharge(golem, AllSoundEvents.WHISTLE_TRAIN.getMainEvent(), 1.0F, 1.0F);
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		float size = (float) ins.owner().getAttributeValue(GolemTypes.GOLEM_SIZE.get());
		float speed = (float) ins.owner().getAttributeValue(Attributes.MOVEMENT_SPEED);
		if (ins.owner().getControllingPassenger() instanceof AbstractGolemEntity<?, ?> other) {
			size += other.getAttributeValue(GolemTypes.GOLEM_SIZE.get());
		} else if (ins.owner().getControllingPassenger() instanceof Player) {
			size += 2.5F;
		} else if (ins.owner().getVehicle() instanceof AbstractGolemEntity<?, ?> v) {
			size += v.getAttributeValue(GolemTypes.GOLEM_SIZE.get());
			speed = (float) v.getAttributeValue(Attributes.MOVEMENT_SPEED);
		}
		return atk * size * speed;
	}

	@Override
	public int getCooldown(ChargeHelper.Instance ins) {
		return calcCooldown(ins.owner());
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return calcCooldown(golem);
	}

	private int calcCooldown(AbstractGolemEntity<?, ?> golem) {
		if (golem.hasEffect(CreateCompatRegistry.EFF_MOBILE.get()) || golem.hasEffect(CreateCompatRegistry.EFF_FORCE.get())) {
			return BigRigChargeGoal.COOLDOWN / 2;
		}
		return BigRigChargeGoal.COOLDOWN;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}

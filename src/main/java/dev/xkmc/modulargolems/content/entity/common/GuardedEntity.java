package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@SerialClass
public class GuardedEntity extends AbstractGolem {

	private long antiHealDisableTimestamp;

	protected GuardedEntity(EntityType<? extends AbstractGolem> type, Level level) {
		super(type, level);
	}

	protected boolean isProtected() {
		return isInvulnerable();
	}

	protected boolean isEffectImmune() {
		return false;
	}


	protected void postHurt(DamageSource source) {
	}

	public boolean isInvulnerableToExtra(DamageSource damage) {
		return isInvulnerable() && !damage.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
	}

	@Override
	protected final void actuallyHurt(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) return;
		if (isInvulnerableToExtra(source)) return;
		amount = ForgeHooks.onLivingHurt(this, source, amount);
		if (amount <= 0) return;
		amount = getDamageAfterArmorAbsorb(source, amount);
		amount = getDamageAfterMagicAbsorb(source, amount);
		amount = ForgeHooks.onLivingDamage(this, source, amount);
		hurtFinal(source, amount);
	}

	protected final void hurtFinal(DamageSource source, float amount) {
		if (!Float.isFinite(amount)) return;
		float abs = getAbsorptionAmount();
		if (!Float.isFinite(abs)) {
			setAbsorptionAmount(0);
			return;
		}
		float actual = Math.max(amount - Math.max(0, abs), 0);
		float absorb = amount - actual;
		setAbsorptionAmount(Math.max(0, getAbsorptionAmount() - absorb));
		if (absorb > 0.0F && absorb < 3.4028235E37F) {
			Entity entity = source.getEntity();
			if (entity instanceof ServerPlayer serverplayer) {
				serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(absorb * 10.0F));
			}
		}
		if (actual != 0.0F) {
			getCombatTracker().recordDamage(source, actual);
			hurtFinalImpl(source, actual);
			setAbsorptionAmount(Math.max(0, getAbsorptionAmount() - actual));
			gameEvent(GameEvent.ENTITY_DAMAGE);
		}
	}

	protected final void hurtFinalImpl(DamageSource source, float amount) {
		if (isInvulnerableToExtra(source)) return;
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			antiHealDisableTimestamp = level().getGameTime() + 100;
			if (isInvulnerable())
				amount = Math.max(amount, Math.max(1, getMaxHealth() * 0.01f));
		}
		setHealth(getHealth() - amount);
		postHurt(source);
	}

	public final void validateData() {
		if (getHealth() > 0) {
			if (deathTime > 0) deathTime = 0;
			if (dead) dead = false;
		}
	}

	@Override
	public final void setHealth(float amount) {
		if (!Float.isFinite(amount)) return;
		if (level().isClientSide()) {
			super.setHealth(amount);
		}
		float health = getHealth();
		if (tickCount > 5 && amount <= health) return;
		setHealth(amount);
	}

	public final void heal(float original) {
		var heal = ForgeEventFactory.onLivingHeal(this, original);
		if (isEffectImmune() && level().getGameTime() > antiHealDisableTimestamp) {
			heal = Math.max(original, heal);
			if (heal <= 0) return;
		}
		float f = getHealth();
		if (f > 0) {
			setHealth(f + heal);
		}
	}

	@Override
	public void die(DamageSource source) {
		if (getHealth() > 0) return;
		super.die(source);
	}

	@MustBeInvokedByOverriders
	@Override
	public void tick() {
		double maxSpeed = 1.5;
		if (isProtected() && getDeltaMovement().length() > maxSpeed) {
			setDeltaMovement(getDeltaMovement().normalize().scale(maxSpeed));
		}
		validateData();
		super.tick();
	}

	public void onRemove(RemovalReason reason) {
	}

}

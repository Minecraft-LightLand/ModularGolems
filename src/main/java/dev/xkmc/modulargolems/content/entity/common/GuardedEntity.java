package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
		super.setHealth(getHealth() - amount);
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
		super.setHealth(amount);
	}

	public void heal(float original) {
		var heal = ForgeEventFactory.onLivingHeal(this, original);
		if (isEffectImmune() && level().getGameTime() > antiHealDisableTimestamp) {
			heal = Math.max(original, heal);
			if (heal <= 0) return;
		}
		float f = getHealth();
		float m = getMaxHealth();
		heal = Math.max(m - f, heal);
		if (f > 0 && heal > 0) {
			onHeal(heal);
			setHealth(f + heal);
		}
	}

	public void onHeal(float heal) {
	}

	@Override
	public void die(DamageSource source) {
		if (getHealth() > 0) return;
		if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, source)) return;
		if (specialDeath(source)) return;
		if (isRemoved() || dead) return;
		Entity entity = source.getEntity();
		LivingEntity livingentity = this.getKillCredit();
		if (this.deathScore >= 0 && livingentity != null) {
			livingentity.awardKillScore(this, this.deathScore, source);
		}
		if (this.isSleeping()) {
			this.stopSleeping();
		}

		this.dead = true;
		this.getCombatTracker().recheckStatus();
		Level level = this.level();
		if (level instanceof ServerLevel) {
			ServerLevel serverlevel = (ServerLevel) level;
			if (entity == null || entity.killedEntity(serverlevel, this)) {
				this.gameEvent(GameEvent.ENTITY_DIE);
				this.dropAllDeathLoot(source);
				this.createWitherRose(livingentity);
			}

			this.level().broadcastEntityEvent(this, (byte) 3);
		}
		this.setPose(Pose.DYING);
	}

	public boolean specialDeath(DamageSource source) {
		return false;
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

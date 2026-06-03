package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

@SerialClass
public abstract class GuardedEntity extends AbstractGolem {

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
	protected void actuallyHurt(ServerLevel level, DamageSource source, float amount) {
		if (isInvulnerableTo(level, source)) return;
		damageContainers.peek().setReduction(DamageContainer.Reduction.ARMOR, damageContainers.peek().getNewDamage() - getDamageAfterArmorAbsorb(source, damageContainers.peek().getNewDamage()));
		getDamageAfterMagicAbsorb(source, damageContainers.peek().getNewDamage());
		float damage = CommonHooks.onLivingDamagePre(this, damageContainers.peek());
		damageContainers.peek().setReduction(DamageContainer.Reduction.ABSORPTION, Math.min(getAbsorptionAmount(), damage));
		float absorbed = Math.min(damage, damageContainers.peek().getReduction(DamageContainer.Reduction.ABSORPTION));
		setAbsorptionAmount(Math.max(0, getAbsorptionAmount() - absorbed));
		float f1 = damageContainers.peek().getNewDamage();
		float f = absorbed;
		if (f > 0.0F && f < 3.4028235E37F && source.getEntity() instanceof ServerPlayer serverplayer) {
			serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
		}
		if (f1 != 0.0F) {
			hurtFinal(source, f1);
		}
		CommonHooks.onLivingDamagePost(this, damageContainers.peek());
	}

	protected void hurtFinal(DamageSource source, float amount) {
		if (isInvulnerableToExtra(source)) return;
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			antiHealDisableTimestamp = level().getGameTime() + 100;
			if (isInvulnerable())
				amount = Math.max(amount, Math.max(1, getMaxHealth() * 0.01f));
		}
		getCombatTracker().recordDamage(source, amount);
		setGuardedDataImpl(getGuardedDataImpl() - amount, false, false);
		gameEvent(GameEvent.ENTITY_DAMAGE);
		onDamageTaken(damageContainers.peek());
		postHurt(source);
	}

	public final void validateData() {
		if (getGuardedDataImpl() > 0) {
			if (deathTime > 0) deathTime = 0;
			if (dead) dead = false;
		}
	}

	@Override
	public final void setHealth(float amount) {
		if (!Float.isFinite(amount)) return;
		if (level().isClientSide()) {
			setGuardedDataImpl(amount, false, false);
		}
		float health = getGuardedDataImpl();
		if (tickCount > 5 && amount <= health) return;
		setGuardedDataImpl(amount, amount > health, false);
	}

	public void heal(float original) {
		if (level().isClientSide()) return;
		var heal = EventHooks.onLivingHeal(this, original);
		if (isEffectImmune() && level().getGameTime() > antiHealDisableTimestamp) {
			heal = Math.max(original, heal);
			if (heal <= 0) return;
		}
		float f = getGuardedDataImpl();
		float m = getMaxHealth();
		heal = Math.min(m - f, heal);
		if (f > 0 && heal > 0) {
			onHeal(heal);
			setGuardedDataImpl(f + heal, true, false);
		}
	}

	public void onHeal(float heal) {
	}

	@Override
	public void die(DamageSource source) {
		if (getGuardedDataImpl() > 0) return;
		if (CommonHooks.onLivingDeath(this, source)) return;
		if (specialDeath(source)) return;
		if (isRemoved() || dead) return;
		Entity entity = source.getEntity();
		LivingEntity livingentity = this.getKillCredit();
		if (livingentity != null) {
			livingentity.awardKillScore(this, source);
		}
		if (this.isSleeping()) {
			this.stopSleeping();
		}
		this.dead = true;
		this.getCombatTracker().recheckStatus();
		Level level = this.level();
		if (level instanceof ServerLevel serverlevel) {
			if (entity == null || entity.killedEntity(serverlevel, this, source)) {
				this.gameEvent(GameEvent.ENTITY_DIE);
				this.dropAllDeathLoot(serverlevel, source);
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
		if (guardedData != null)
			guardedData = guardedData.update(this);
		if (tickCount % 20 == 13 && isAddedToLevel() && !level().isClientSide()) {
			validateGuardedData();
			GuardedDataToClient.send(this);
		}
	}



	@Override
	public void kill(ServerLevel sl) {
		if (dynamicReductionRate() > 0 && !level().isClientSide()) {
			guardedData = new GuardedData(0, 0);
			super.setHealth(0);
			die(damageSources().genericKill());
			GuardedDataToClient.send(this);
			return;
		}
		super.kill(sl);
	}

	public void onRemove(RemovalReason reason) {
	}

	@Override
	protected void dropAllDeathLoot(ServerLevel p_348524_, DamageSource p_21192_) {
		if (getGuardedDataImpl() > 0) return;
		super.dropAllDeathLoot(p_348524_, p_21192_);
	}

	@Override
	public void setPose(Pose pose) {
		if (getGuardedDataImpl() > 0 && pose == Pose.DYING)
			return;
		super.setPose(pose);
	}

	@Override
	public void handleEntityEvent(byte event) {
		if (event == EntityEvent.DEATH && getGuardedDataImpl() > 0)
			return;
		super.handleEntityEvent(event);
	}

	@Override
	protected void tickDeath() {
		if (getGuardedDataImpl() > 0) return;
		super.tickDeath();
	}

	@Nullable
	@SerialField
	private GuardedData guardedData;
	private boolean loopingSetHealth = false;

	public void setGuardedDataImpl(float amount) {
		setGuardedDataImpl(amount, true, false);
	}

	public void setGuardedDataImpl(float amount, boolean force, boolean repair) {
		boolean update = guardedData == null || amount != guardedData.amount();
		if (guardedData == null) guardedData = GuardedData.start(this, amount);
		else guardedData = guardedData.set(this, amount, force, repair);
		if (!loopingSetHealth) {
			loopingSetHealth = true;
			super.setHealth(amount);
			loopingSetHealth = false;
			if (update && isAddedToLevel() && !level().isClientSide())
				GuardedDataToClient.send(this);
		}
	}

	public void applyData(GuardedData data) {
		guardedData = data;
		loopingSetHealth = true;
		super.setHealth(data.amount());
		loopingSetHealth = false;
	}

	public float getGuardedDataImpl() {
		if (guardedData != null)
			return Math.max(super.getHealth(), guardedData.amount());
		if (!level().isClientSide())
			validateGuardedData();
		return super.getHealth();
	}

	@Override
	public float getHealth() {
		return getGuardedDataImpl();
	}

	public float getDynamicBaseline() {
		return dynamicReductionRate() == 0 || guardedData == null ? 0 : guardedData.baseline();
	}

	@Override
	protected boolean isImmobile() {
		return getGuardedDataImpl() <= 0;
	}

	public void validateGuardedData() {
		if (loopingSetHealth) return;
		if (guardedData == null) {
			guardedData = GuardedData.start(this, super.getHealth());
		} else {
			if (super.getHealth() < guardedData.amount()) {
				loopingSetHealth = true;
				super.setHealth(guardedData.amount());
				loopingSetHealth = false;
			} else guardedData = guardedData.set(this, super.getHealth(), true, false);
		}
	}
	protected float dynamicReductionRate() {
		return 0;
	}

	protected float dynamicReductionCap() {
		return 0.2f;
	}

	public record GuardedData(float amount, float baseline) {

		public static GuardedData start(GuardedEntity e, float amount) {
			var rate = e.dynamicReductionRate();
			float base = rate == 0 ? 0 : amount - e.getMaxHealth() * e.dynamicReductionCap();
			return new GuardedData(amount, base);
		}

		public GuardedData set(GuardedEntity e, float amount, boolean force, boolean boostBase) {
			var rate = e.dynamicReductionRate();
			float ans = rate > 0 && !force ? Math.max(amount, baseline) : amount;
			float base = rate > 0 && boostBase && ans > amount() ?
					Math.max(baseline + ans - amount(), ans - e.getMaxHealth() * e.dynamicReductionCap()) :
					Math.min(baseline, ans);
			return new GuardedData(ans, base);
		}

		public GuardedData update(GuardedEntity e) {
			var rate = e.dynamicReductionRate();
			if (rate == 0) return this;
			var max = e.getMaxHealth();
			var allowed = max * e.dynamicReductionCap();
			var minBase = Math.max(0, amount - allowed);
			if (baseline <= minBase) {
				if (e.getTarget() != null)
					return this;
				var maxBase = Math.min(minBase, baseline + max / 1200f);
				return new GuardedData(amount, maxBase);
			}
			return new GuardedData(amount, Math.max(minBase, baseline - allowed / rate));
		}
	}

	public record GuardedDataToClient(int id, GuardedData data) implements SerialPacketBase<GuardedDataToClient> {

		public static void send(GuardedEntity e) {
			if (e.guardedData == null) return;
			ModularGolems.HANDLER.toTrackingPlayers(new GuardedDataToClient(e.getId(), e.guardedData), e);
		}

		@Override
		public void handle(Player player) {
			GuardedDataHandler.handle(this);
		}
	}

	public static class GuardedDataHandler {

		public static void handle(GuardedDataToClient packet) {
			var level = Minecraft.getInstance().level;
			if (level == null) return;
			var e = level.getEntity(packet.id);
			if (!(e instanceof GuardedEntity g)) return;
			g.applyData(packet.data);
		}

	}

}

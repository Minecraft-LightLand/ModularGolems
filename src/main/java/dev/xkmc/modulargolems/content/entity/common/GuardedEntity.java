package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkEvent;
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
		setGuardedDataImpl(getGuardedDataImpl() - amount, false, false);
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
		var heal = ForgeEventFactory.onLivingHeal(this, original);
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
		if (guardedData != null)
			guardedData = guardedData.update(this);
		if (tickCount % 20 == 13 && isAddedToWorld() && !level().isClientSide()) {
			validateGuardedData();
			GuardedDataToClient.send(this);
		}
	}

	public void onRemove(RemovalReason reason) {
	}

	@Override
	protected void dropAllDeathLoot(DamageSource source) {
		if (getGuardedDataImpl() > 0) return;
		super.dropAllDeathLoot(source);
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

	@SerialClass.SerialField
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
			if (update && isAddedToWorld() && !level().isClientSide())
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

	public record GuardedData(float amount, float baseline) {

		public static GuardedData start(GuardedEntity e, float amount) {
			var rate = e.dynamicReductionRate();
			float base = rate == 0 ? 0 : amount - e.getMaxHealth() * 0.2f;
			return new GuardedData(amount, base);
		}

		public GuardedData set(GuardedEntity e, float amount, boolean force, boolean boostBase) {
			var rate = e.dynamicReductionRate();
			float ans = rate > 0 && !force ? Math.max(amount, baseline) : amount;
			float base = rate > 0 && boostBase && ans > amount() ?
					Math.max(baseline + ans - amount(), ans - e.getMaxHealth() * 0.2f) :
					Math.min(baseline, ans);
			return new GuardedData(ans, base);
		}

		public GuardedData update(GuardedEntity e) {
			if (e.level().isClientSide()) return this;
			var rate = e.dynamicReductionRate();
			if (rate == 0) return this;
			var max = e.getMaxHealth();
			var allowed = max * 0.2f;
			var minBase = Math.max(0, amount - allowed);
			if (baseline <= minBase) {
				if (e.getTarget() != null)
					return this;
				var maxBase = Math.min(minBase, baseline + allowed / 100f);
				return new GuardedData(amount, maxBase);
			}
			return new GuardedData(amount, Math.max(minBase, baseline - allowed / rate));
		}
	}

	@SerialClass
	public static class GuardedDataToClient extends SerialPacketBase {

		public static void send(GuardedEntity e) {
			var ans = new GuardedDataToClient();
			ans.id = e.getId();
			ans.data = e.guardedData;
			ModularGolems.HANDLER.toTrackingPlayers(ans, e);
		}

		@SerialClass.SerialField
		public int id;
		@SerialClass.SerialField
		public GuardedData data;

		public GuardedDataToClient() {

		}

		@Override
		public void handle(NetworkEvent.Context context) {
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

package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.content.upgrades.UpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

@SerialClass
public class AbstractGolemEntity<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolem
		implements IEntityAdditionalSpawnData, NeutralMob, OwnableEntity {

	protected AbstractGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
	}

	// ------ materials

	@SerialClass.SerialField(toClient = true)
	private ArrayList<GolemMaterial> materials = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	private ArrayList<Item> upgrades = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	@Nullable
	private UUID owner;
	@SerialClass.SerialField(toClient = true)
	private HashMap<GolemModifier, Integer> modifiers = new HashMap<>();

	public void onCreate(ArrayList<GolemMaterial> materials, ArrayList<UpgradeItem> upgrades, @Nullable UUID owner) {
		this.materials = materials;
		this.upgrades = Wrappers.cast(upgrades);
		this.owner = owner;
		this.modifiers = GolemMaterial.collectModifiers(materials, upgrades);
		GolemMaterial.addAttributes(materials, getThis());
		this.setHealth(this.getMaxHealth());
	}

	public EntityType<T> getType() {
		return Wrappers.cast(super.getType());
	}

	public ArrayList<GolemMaterial> getMaterials() {
		return materials;
	}

	public ArrayList<Item> getUpgrades() {
		return upgrades;
	}

	public HashMap<GolemModifier, Integer> getModifiers() {
		return modifiers;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getMainHandItem().isEmpty()) {
			if (!level.isClientSide()) {
				player.setItemSlot(EquipmentSlot.MAINHAND, GolemHolder.setEntity(getThis()));
				level.broadcastEntityEvent(this, EntityEvent.POOF);
				this.discard();
			}
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damage) {
		super.actuallyHurt(source, damage);
		if (getHealth() <= 0) {
			//TODO add modifier check
			spawnAtLocation(GolemHolder.setEntity(getThis()));
			level.broadcastEntityEvent(this, EntityEvent.POOF);
			this.discard();
		}
	}

	@Nullable
	public UUID getOwnerUUID() {
		return owner;
	}

	@Nullable
	public Player getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level.getPlayerByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	// ------ addition golem behavior

	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("auto-serial", Objects.requireNonNull(TagCodec.toTag(new CompoundTag(), this)));
	}

	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("auto-serial")) {
			Wrappers.run(() -> {
				TagCodec.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, (f) -> true);
			});
		}
	}

	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void writeSpawnData(FriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	public void readSpawnData(FriendlyByteBuf data) {
		PacketCodec.from(data, Wrappers.cast(this.getClass()), getThis());
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

	// ------ common golem behavior

	public boolean canAttackType(EntityType<?> target) {
		if (target == EntityType.PLAYER) {
			return false;
		}
		return target != EntityType.CREEPER && super.canAttackType(target);
	}

	protected float getAttackDamage() {
		return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}

	@Override
	public void aiStep() {
		this.updateSwingTime();
		super.aiStep();
		double heal = this.getAttributeValue(GolemTypeRegistry.GOLEM_REGEN.get());
		if (heal > 0 && this.tickCount % 20 == 0) {
			this.heal((float) heal);
		}
		if (!this.level.isClientSide) {
			this.updatePersistentAnger((ServerLevel) this.level, true);
		}
	}

	protected int decreaseAirSupply(int air) {
		return air;
	}

	// ------ persistent anger

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private int remainingPersistentAngerTime;
	@Nullable
	private UUID persistentAngerTarget;

	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	public void setRemainingPersistentAngerTime(int time) {
		this.remainingPersistentAngerTime = time;
	}

	public int getRemainingPersistentAngerTime() {
		return this.remainingPersistentAngerTime;
	}

	public void setPersistentAngerTarget(@Nullable UUID target) {
		this.persistentAngerTarget = target;
	}

	@Nullable
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	// ------ tamable

	public Team getTeam() {
		LivingEntity owner = this.getOwner();
		if (owner != null) {
			return owner.getTeam();
		}
		return super.getTeam();
	}

	public boolean isAlliedTo(Entity other) {
		LivingEntity owner = this.getOwner();
		if (other == owner) {
			return true;
		}
		if (owner != null) {
			return owner.isAlliedTo(other);
		}
		return super.isAlliedTo(other);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
		}
		return super.doHurtTarget(target);
	}

	protected void registerTargetGoals() {
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::predicatePriorityTarget));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (e) -> e instanceof Enemy && !(e instanceof Creeper)));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	private boolean predicatePriorityTarget(LivingEntity e) {
		if (e instanceof Mob mob) {
			for (var target : List.of(
					Optional.ofNullable(mob.getLastHurtMob()),
					Optional.ofNullable(mob.getTarget()),
					Optional.ofNullable(mob.getLastHurtByMob())
			)) {
				if (target.isPresent()) {
					Player owner = getOwner();
					if (target.get() == owner) return true;
					if (target.get().isAlliedTo(this)) return true;
				}
			}
		}
		return false;
	}

}



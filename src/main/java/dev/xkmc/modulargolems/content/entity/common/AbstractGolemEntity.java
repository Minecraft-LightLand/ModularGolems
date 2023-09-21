package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.util.annotation.ServerOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.goals.*;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.sync.SyncedData;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.item.wand.WandItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

@SerialClass
public class AbstractGolemEntity<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolem
		implements IEntityAdditionalSpawnData, NeutralMob, OwnableEntity, PowerableMob {

	private static final SyncedData GOLEM_DATA = new SyncedData(AbstractGolemEntity.class);

	protected AbstractGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
		this.waterNavigation = new AmphibiousPathNavigation(this, level);
		this.groundNavigation = new GroundPathNavigation(this, level);
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
	@SerialClass.SerialField(toClient = true)
	private final HashSet<GolemFlags> golemFlags = new HashSet<>();

	protected final PathNavigation waterNavigation;
	protected final GroundPathNavigation groundNavigation;

	public void onCreate(ArrayList<GolemMaterial> materials, ArrayList<UpgradeItem> upgrades, @Nullable UUID owner) {
		updateAttributes(materials, upgrades, owner);
		this.setHealth(this.getMaxHealth());
	}

	public void updateAttributes(ArrayList<GolemMaterial> materials, ArrayList<UpgradeItem> upgrades, @Nullable UUID owner) {
		this.materials = materials;
		this.upgrades = Wrappers.cast(upgrades);
		this.owner = owner;
		this.modifiers = GolemMaterial.collectModifiers(materials, upgrades);
		this.golemFlags.clear();
		this.setMaxUpStep(1);
		if (!level().isClientSide()) {
			getModifiers().forEach((m, i) -> m.onRegisterFlag(golemFlags::add));
		}
		if (canSwim()) {
			this.moveControl = new GolemSwimMoveControl(this);
			this.navigation = waterNavigation;
			this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
			this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
		}
		if (!level().isClientSide()) {
			getModifiers().forEach((m, i) -> m.onRegisterGoals(this, i, this.goalSelector::addGoal));
		}
		GolemMaterial.addAttributes(materials, upgrades, getThis());
		refreshDimensions();
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

	public boolean hasFlag(GolemFlags flag) {
		return golemFlags.contains(flag);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		this.unRide();
		if (player.getItemInHand(hand).getItem() instanceof WandItem) return InteractionResult.PASS;
		if (!MGConfig.COMMON.barehandRetrieve.get() || !this.isAlliedTo(player)) return InteractionResult.FAIL;
		if (player.getMainHandItem().isEmpty()) {
			if (!level().isClientSide()) {
				player.setItemSlot(EquipmentSlot.MAINHAND, toItem());
			}
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}

	@ServerOnly
	public ItemStack toItem() {
		var ans = GolemHolder.setEntity(getThis());
		level().broadcastEntityEvent(this, EntityEvent.POOF);
		this.discard();
		return ans;
	}

	@Override
	public boolean fireImmune() {
		return hasFlag(GolemFlags.FIRE_IMMUNE);
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damage) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) damage *= 1000;
		super.actuallyHurt(source, damage);
		if (getHealth() <= 0 && hasFlag(GolemFlags.RECYCLE)) {
			Player player = getOwner();
			ItemStack stack = GolemHolder.setEntity(getThis());
			if (player != null) {
				player.getInventory().placeItemBackInInventory(stack);
			} else {
				spawnAtLocation(stack);
			}
			level().broadcastEntityEvent(this, EntityEvent.POOF);
			this.discard();
		}
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int i, boolean b) {
		Map<Item, Integer> drop = new HashMap<>();
		for (GolemMaterial mat : getMaterials()) {
			Item item = GolemMaterialConfig.get().ingredients.get(mat.id()).getItems()[0].getItem();
			drop.compute(item, (e, old) -> (old == null ? 0 : old) + 1);
		}
		drop.forEach((k, v) -> spawnAtLocation(new ItemStack(k, v)));
	}

	public float getScale() {
		if (materials == null || materials.isEmpty() || getTags().contains("ClientOnly")) {
			return 1;
		}
		return (float) (getAttributeValue(GolemTypes.GOLEM_SIZE.get()) / DefaultAttributes.getSupplier(getType()).getValue(GolemTypes.GOLEM_SIZE.get()));
	}

	// ------ swim

	public boolean canSwim() {
		return hasFlag(GolemFlags.SWIM);
	}

	public void travel(Vec3 pTravelVector) {
		if (!getMode().isMovable()) {
			pTravelVector = Vec3.ZERO;
		}
		if (this.isEffectiveAi() && this.isInWater() && canSwim()) {
			this.moveRelative(0.02F, pTravelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
		} else {
			super.travel(pTravelVector);
		}
	}

	public void updateSwimming() {
		if (!this.level().isClientSide) {
			this.setSwimming(this.isEffectiveAi() && this.isInWater() && this.canSwim());
		}

	}

	@Override
	public boolean isPushable() {
		return getMode().isMovable();
	}

	public boolean isPushedByFluid() {
		return !this.isSwimming() && getMode().isMovable();
	}

	// ------ ownable entity

	@Nullable
	public UUID getOwnerUUID() {
		return owner;
	}

	@Nullable
	public Player getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	// ------ addition golem behavior

	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.addPersistentAngerSaveData(tag);
		tag.put("auto-serial", Objects.requireNonNull(TagCodec.toTag(new CompoundTag(), this)));
		GOLEM_DATA.write(tag, entityData);
	}

	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.readPersistentAngerSaveData(this.level(), tag);
		if (tag.contains("auto-serial")) {
			Wrappers.run(() -> {
				TagCodec.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, (f) -> true);
			});
		}
		updateAttributes(materials, Wrappers.cast(getUpgrades()), owner);
		GOLEM_DATA.read(tag, entityData);

	}

	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void writeSpawnData(FriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	public void readSpawnData(FriendlyByteBuf data) {
		PacketCodec.from(data, Wrappers.cast(this.getClass()), getThis());
		updateAttributes(materials, Wrappers.cast(upgrades), owner);
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

	// ------ common golem behavior

	@Override
	public boolean hasLineOfSight(Entity target) {
		if (target.level() == this.level() && hasFlag(GolemFlags.SEE_THROUGH)) {
			Vec3 self = new Vec3(this.getX(), this.getEyeY(), this.getZ());
			Vec3 tarp = new Vec3(target.getX(), target.getEyeY(), target.getZ());
			double dist = tarp.distanceTo(self);
			if (dist <= 128.0D) {
				if (target.level().canSeeSky(target.blockPosition()))
					return true;
				if (dist < 5)
					return true;
				if (self.y() < tarp.y())
					return true;
			}
		}
		return super.hasLineOfSight(target);
	}

	@Override
	public boolean canFreeze() {
		return !hasFlag(GolemFlags.FREEZE_IMMUNE);
	}

	@Override
	public boolean canBeSeenAsEnemy() {
		return hasFlag(GolemFlags.PASSIVE) || super.canBeSeenAsEnemy();
	}

	@Override
	public void setTarget(@Nullable LivingEntity pTarget) {
		if (pTarget != null && !canAttack(pTarget)) {
			return;
		}
		super.setTarget(pTarget);
		if (pTarget instanceof Mob mob) {
			if (mob.getTarget() == null && mob.canAttack(this)) {
				mob.setTarget(this);
			}
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onSetTarget(this, mob, entry.getValue());
			}

		}
	}

	@Override
	public boolean canAttackType(EntityType<?> pType) {
		if (hasFlag(GolemFlags.PASSIVE)) {
			return false;
		}
		return !pType.is(MGTagGen.GOLEM_FRIENDLY);
	}

	@Override
	public boolean canAttack(LivingEntity pTarget) {
		return !this.isAlliedTo(pTarget) && canAttackType(pTarget.getType()) && super.canAttack(pTarget);
	}

	protected float getAttackDamage() {
		float ans = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (var entry : getModifiers().entrySet()) {
			ans = entry.getKey().modifyDamage(ans, this, entry.getValue());
		}
		return ans;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onClientTick(this, entry.getValue());
			}
		}
	}

	@Override
	public void aiStep() {
		this.updateSwingTime();
		super.aiStep();
		double heal = this.getAttributeValue(GolemTypes.GOLEM_REGEN.get());
		if (heal > 0 && this.tickCount % 20 == 0) {
			for (var entry : getModifiers().entrySet()) {
				heal = entry.getKey().onHealTick(heal, this, entry.getValue());
			}
			if (heal > 0) {
				this.heal((float) heal);
			}
		}
		if (!this.level().isClientSide) {
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onAiStep(this, entry.getValue());
			}
			this.updatePersistentAnger((ServerLevel) this.level(), true);
		}
	}

	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity target) {
		Player player = getOwner();
		if (player != null) GolemTriggers.KILL.trigger((ServerPlayer) player, target);
		return super.killedEntity(level, target);
	}

	// mode

	private static final EntityDataAccessor<Integer> DATA_MODE = GOLEM_DATA.define(SyncedData.INT, 0, "follow_mode");
	private static final EntityDataAccessor<BlockPos> GUARD_POS = GOLEM_DATA.define(SyncedData.BLOCK_POS, BlockPos.ZERO, "guard_pos");

	public GolemMode getMode() {
		return GolemModes.get(this.entityData.get(DATA_MODE));
	}

	public BlockPos getGuardPos() {
		return this.entityData.get(GUARD_POS);
	}

	public void setMode(int mode, BlockPos pos) {
		this.entityData.set(DATA_MODE, mode);
		this.entityData.set(GUARD_POS, pos);
	}

	@Override
	public boolean canChangeDimensions() {
		return getMode().canChangeDimensions() && super.canChangeDimensions();
	}

	private static final EntityDataAccessor<Optional<UUID>> CONFIG_ID = GOLEM_DATA.define(SyncedData.UUID, Optional.empty(), "config_owner");
	private static final EntityDataAccessor<Integer> CONFIG_COLOR = GOLEM_DATA.define(SyncedData.INT, 0, "config_color");

	@Nullable
	public GolemConfigEntry getConfigEntry(@Nullable Component dummy) {
		UUID configOwner = entityData.get(CONFIG_ID).orElse(null);
		int configColor = entityData.get(CONFIG_COLOR);
		if (configColor < 0 || configOwner == null) return null;
		var storage = GolemConfigStorage.get(level());
		if (dummy == null) {
			return storage.getStorage(configOwner, configColor);
		} else {
			return storage.getOrCreateStorage(configOwner, configColor, dummy);
		}
	}

	public void setConfigCard(UUID owner, int color) {
		entityData.set(CONFIG_ID, Optional.of(owner));
		entityData.set(CONFIG_COLOR, color);
	}

	// ------ persistent anger

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = GOLEM_DATA.define(SyncedData.INT, 0, null);

	@Nullable
	private UUID persistentAngerTarget;

	protected void defineSynchedData() {
		super.defineSynchedData();
		GOLEM_DATA.register(this.entityData);
	}

	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	public int getRemainingPersistentAngerTime() {
		return this.entityData.get(DATA_REMAINING_ANGER_TIME);
	}

	public void setRemainingPersistentAngerTime(int pTime) {
		this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
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
		if (other instanceof Player && getOwnerUUID() == null) {
			return true;
		}
		if (MGConfig.COMMON.ownerPickupOnly.get()) {
			return false;
		}
		if (owner != null) {
			return owner.isAlliedTo(other) || other.isAlliedTo(owner);
		}
		return super.isAlliedTo(other);
	}

	protected void doPush(Entity entity) {
		if (entity instanceof Enemy && !(entity instanceof Creeper)) {
			this.setTarget((LivingEntity) entity);
		}
		super.doPush(entity);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
		}
		return super.doHurtTarget(target);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new GolemFloatGoal(this));
		this.goalSelector.addGoal(1, new TeleportToOwnerGoal(this));
		this.goalSelector.addGoal(3, new FollowOwnerGoal(this));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new GolemRandomStrollGoal(this));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::predicatePriorityTarget));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::predicateSecondaryTarget));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	protected boolean predicatePriorityTarget(LivingEntity e) {
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

	protected boolean predicateSecondaryTarget(LivingEntity e) {
		return e instanceof Enemy && !(e instanceof Creeper);
	}

	public boolean isInSittingPose() {
		return false;
	}

	public Vec3 getTargetPos() {
		if (getMode().hasPos()) {
			BlockPos pos = getGuardPos();
			return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		}
		LivingEntity owner = getOwner();
		if (owner == null) return getPosition(1);
		return owner.getPosition(1);
	}

	@Override
	public boolean isPowered() {
		return true;
	}

	@Override
	public boolean isInvulnerable() {
		return hasFlag(GolemFlags.IMMUNITY);
	}

	@Override
	public void die(DamageSource source) {
		ModularGolems.LOGGER.info("Golem {} died, message: '{}'", this, source.getLocalizedDeathMessage(this).getString());
		Player owner = getOwner();
		if (owner != null && !level().isClientSide) {
			owner.sendSystemMessage(source.getLocalizedDeathMessage(this));
		}
		super.die(source);
	}

	public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity target) {
		return GolemMeleeGoal.calculateDistSqr(this, target);
	}

	public void checkRide(LivingEntity target) {
	}

}



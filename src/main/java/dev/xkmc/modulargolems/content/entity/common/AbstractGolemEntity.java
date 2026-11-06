package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.util.annotation.ServerOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.goals.*;
import dev.xkmc.modulargolems.content.entity.impl.GolemImpls;
import dev.xkmc.modulargolems.content.entity.impl.GolemInvManager;
import dev.xkmc.modulargolems.content.entity.impl.TransientStateContainer;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.targeting.Golem3DTargetGoal;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.equipments.IGolemModifierItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.EsterEggUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

@SerialClass
public class AbstractGolemEntity<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends GuardedEntity
		implements IEntityAdditionalSpawnData, GolemImpls {

	private static <T> EntityDataAccessor<T> defineId(EntityDataSerializer<T> ser) {
		return SynchedEntityData.defineId(AbstractGolemEntity.class, ser);
	}

	private static final SyncedData GOLEM_DATA = new SyncedData(AbstractGolemEntity::defineId);
	private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = GOLEM_DATA.define(SyncedData.UUID, Optional.empty(), null);
	public static final EntityDataAccessor<Integer> DATA_MODE = GOLEM_DATA.define(SyncedData.INT, 0, "follow_mode");
	public static final EntityDataAccessor<BlockPos> GUARD_POS = GOLEM_DATA.define(SyncedData.BLOCK_POS, BlockPos.ZERO, "guard_pos");
	public static final EntityDataAccessor<Optional<UUID>> CONFIG_ID = GOLEM_DATA.define(SyncedData.UUID, Optional.empty(), "config_owner");
	public static final EntityDataAccessor<Integer> CONFIG_COLOR = GOLEM_DATA.define(SyncedData.INT, 0, "config_color");
	public static final EntityDataAccessor<Integer> PATROL_STAGE = GOLEM_DATA.define(SyncedData.INT, 0, "patrol_stage");
	public static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = GOLEM_DATA.define(SyncedData.INT, 0, null);
	public static final EntityDataAccessor<Boolean> IS_IN_RANGE_ATTACK = GOLEM_DATA.define(SyncedData.BOOL, false, null);
	public static final EntityDataAccessor<Boolean> BREATH_ATTACK = GOLEM_DATA.define(SyncedData.BOOL, false, null);
	public static final EntityDataAccessor<Float> DASH_YAW = GOLEM_DATA.define(SyncedData.FLOAT, Float.NaN, null);
	public static final EntityDataAccessor<ItemStack> ARROW_SLOT = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.ITEM_STACK);
	public static final EntityDataAccessor<ItemStack> ARROW_SLOT_2 = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	protected MoveControl waterMoveControl;

	protected AbstractGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
		registerBannerSlot(() -> bannerSlot, e -> bannerSlot = e);
		waterMoveControl = new GolemSwimMoveControl(this);
		this.waterNavigation = new AmphibiousPathNavigation(this, level);
		this.groundNavigation = new FastGroundPathNavigation(this, level);
		navigation = groundNavigation;
	}

	// ------ materials

	@SerialClass.SerialField(toClient = true)
	private ArrayList<GolemMaterial> materials = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	private ArrayList<Item> upgrades = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	@Nullable
	private UUID owner, leader;
	@SerialClass.SerialField(toClient = true)
	private HashMap<GolemModifier, Integer> modifiers = new LinkedHashMap<>();
	@SerialClass.SerialField(toClient = true)
	private final HashSet<GolemFlags> golemFlags = new HashSet<>();
	@SerialClass.SerialField
	private Vec3 recordedPosition = Vec3.ZERO;
	@SerialClass.SerialField
	private BlockPos recordedGuardPos = BlockPos.ZERO;
	@SerialClass.SerialField(toClient = true)
	private ItemStack bannerSlot = ItemStack.EMPTY,
			arrowSlot = ItemStack.EMPTY,
			arrowSlot2 = ItemStack.EMPTY;

	protected final PathNavigation waterNavigation;
	protected final GroundPathNavigation groundNavigation;
	public final Set<MobEffect> effectImmunity = new HashSet<>();
	private final List<Goal> modifierGoals = new ArrayList<>();
	private Golem3DTargetGoal targeter;
	public LivingEntity forcedTarget;
	public GolemMeleeGoal meleeGoal;
	public final GolemInvManager invManager = new GolemInvManager(this);
	public final TransientStateContainer transientManager = new TransientStateContainer();

	// marks opened inventory
	public int inventoryTick = 0;
	public int specialAttackCoolDown = 0;
	private double lastSize = 0;
	private boolean sizeDirty = false;

	@Override
	public SynchedEntityData entityData() {
		return entityData;
	}

	@SerialClass.OnInject
	public void cleanInvalidEntries() {
		GolemType<?, ?> type = GolemType.getGolemType(getType());
		if (materials != null) {
			IGolemPart<?>[] parts = type == null ? null : type.values();
			for (int i = 0; i < materials.size(); i++) {
				GolemMaterial mat = materials.get(i);
				if (mat != null && mat.part() instanceof GolemPart<?, ?>) continue;
				if (parts == null || i >= parts.length) {
					materials.remove(i--);
					continue;
				}
				materials.set(i, parts[i].toItem().parseMaterial(type.defaultMaterial()));
			}
		}
		if (upgrades != null) upgrades.removeIf(Objects::isNull);
		if (modifiers != null) modifiers.keySet().removeIf(Objects::isNull);
	}

	public void onCreate(ArrayList<GolemMaterial> materials, ArrayList<IUpgradeItem> upgrades, @Nullable UUID owner) {
		updateAttributes(materials, upgrades, owner);
		if (!level().isClientSide()) {
			applyData(GuardedData.start(this, getMaxHealth()));
		}
	}

	public void updateAttributes(ArrayList<GolemMaterial> materials, ArrayList<IUpgradeItem> upgrades, @Nullable UUID owner) {
		this.materials = materials;
		this.upgrades = Wrappers.cast(upgrades);
		setOwnerUUID(owner);
		this.modifiers = GolemMaterial.collectModifiers(materials, upgrades);
		this.golemFlags.clear();
		this.setMaxUpStep(1);
		getModifiers().forEach((m, i) -> m.onRegisterFlag(golemFlags::add));
		if (canSwim()) {
			this.moveControl = waterMoveControl;
			this.navigation = waterNavigation;
			this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
			this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
		}
		if (!level().isClientSide()) {
			modifierGoals.forEach(goalSelector::removeGoal);
			modifierGoals.clear();
			getModifiers().forEach((m, i) ->
					m.onRegisterGoals(this, i, (priority, goal) -> {
						modifierGoals.add(goal);
						goalSelector.addGoal(priority, goal);
					}));
		}
		var sup = DefaultAttributes.getSupplier(getType());
		for (var e : ForgeRegistries.ATTRIBUTES) {
			var ins = getAttribute(e);
			if (ins == null | !sup.hasAttribute(e)) continue;
			ins.setBaseValue(sup.getBaseValue(e));
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

	public HashMap<GolemModifier, Integer> getModifiersExtended() {
		HashMap<GolemModifier, Integer> ans = new HashMap<>(modifiers);
		for (var e : EquipmentSlot.values()) {
			ItemStack stack = getItemBySlot(e);
			if (stack.getItem() instanceof IGolemModifierItem item) {
				for (var ins : item.getModifier(stack, this)) {
					ans.compute(ins.mod(), (k, v) -> (v == null ? 0 : v) + ins.level());
				}
			}
		}
		return ans;
	}

	public boolean hasFlag(GolemFlags flag) {
		if (golemFlags == null) return false;
		return golemFlags.contains(flag);
	}

	@Override
	protected final InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getItemInHand(hand).is(MGTagGen.GOLEM_INTERACT)) return InteractionResult.PASS;
		for (var ent : modifiers.entrySet()) {
			var result = ent.getKey().interact(player, this, hand, ent.getValue());
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return mobInteractImpl(player, hand);
	}

	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		if (!MGConfig.COMMON.barehandRetrieve.get() || !this.canModify(player)) return InteractionResult.FAIL;
		if (player.getMainHandItem().isEmpty()) {
			if (!level().isClientSide()) {
				this.unRide();

				player.setItemSlot(EquipmentSlot.MAINHAND, toItem(player));
				setRetrivedTo(GolemTracker.RetrieveTarget.INVENTORY);
			}
			return InteractionResult.SUCCESS;
		} else {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof IGolemEquipmentItem item) {
				if (item.isFor(getType()) && getItemBySlot(item.getSlot()).isEmpty()) {
					if (!level().isClientSide()) {
						setItemSlot(item.getSlot(), stack.split(1));
					}
					return InteractionResult.CONSUME;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@ServerOnly
	public ItemStack toItem(LivingEntity player) {
		recordedPosition = position();
		recordedGuardPos = getGuardPos();
		leader = null;
		untrack(player == getOwner() ? GolemTracker.Status.RETRIEVED : GolemTracker.Status.OTHER_RETRIEVED, player);
		unRide();
		var ans = GolemHolder.setEntity(getThis());
		level().broadcastEntityEvent(this, EntityEvent.POOF);
		this.discard();
		return ans;
	}

	/**
	 * Registers arrow slots as extra slots. Dog, metal and humanoid golems have two. Golems that
	 * never call this expose empty wrappers instead.
	 */
	protected final void registerArrowSlot(boolean two) {
		invManager.registerArrowSlot(() -> arrowSlot, e -> arrowSlot = e);
		if (two) {
			invManager.registerArrowSlot2(() -> arrowSlot2, e -> arrowSlot2 = e);
		}
	}

	public ItemStack getProjectile(ItemStack pShootable) {
		return invManager.getProjectile(pShootable);
	}

	@Override
	public boolean fireImmune() {
		return hasFlag(GolemFlags.FIRE_IMMUNE);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (level().isClientSide()) return false;
		if (source.getEntity() == this) return false;
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && source.getEntity() == null && getY() < level().getMinBuildHeight() - 64) {
			markDead();
			return true;
		}
		return super.hurt(source, amount);
	}

	protected void postHurt(DamageSource source) {
		if (getGuardedDataImpl() <= 0 && hasFlag(GolemFlags.RECYCLE)) {
			markDead();
		}
		if (source.getEntity() instanceof LivingEntity le) {
			setTargetOnHurt(le);
		}
	}

	public void setTargetOnHurt(LivingEntity le) {
		if (isAlive() && predicateTarget(le)) {
			if (isWithinMeleeAttackRange(le)) {
				targeter.findTarget();
				setTarget(targeter.getTarget());
			}
		}
		if (getControllingPassenger() instanceof AbstractGolemEntity<?, ?> golem) {
			golem.setTargetOnHurt(le);
		}
		meleeGoal.clearDelay();
	}

	public double getMeleeAttackRangeSqr(LivingEntity e) {
		double val = getAttributeValue(ForgeMod.ENTITY_REACH.get());
		return val * val + e.getBbWidth();
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int i, boolean b) {
		dropGolemLoot(source);
		super.dropCustomDeathLoot(source, i, b);
	}


	public void markSizeDirty() {
		sizeDirty = true;
	}

	private double getScaleImpl() {
		int reforge = getReforgeBase();
		double rate = Math.pow(1d * (reforge - getReforgeCount()) / reforge, 1d / 3);
		return getAttributeValue(GolemTypes.GOLEM_SIZE.get()) * rate;
	}

	public void checkSize() {
		if (!sizeDirty && tickCount > 5 && tickCount % 10 != 0) return;
		sizeDirty = false;
		double cur = getScaleImpl();
		if (lastSize != cur) {
			refreshDimensions();
		}
	}

	@Override
	public void refreshDimensions() {
		lastSize = getScaleImpl();
		super.refreshDimensions();
	}

	@Override
	public float maxUpStep() {
		return super.maxUpStep() * getScale();
	}

	public float getScale() {
		if (materials == null || materials.isEmpty() || level().isClientSide() && !isAddedToWorld() || getTags().contains("ClientOnly")) {
			return 1;
		}
		var def = DefaultAttributes.getSupplier(getType()).getValue(GolemTypes.GOLEM_SIZE.get());
		return (float) (getScaleImpl() / def);
	}

	public void calculateEntityAnimation(boolean hasY) {
		float f = (float) Mth.length(this.getX() - this.xo, hasY ? this.getY() - this.yo : 0.0D, this.getZ() - this.zo);
		this.updateWalkAnimation(f / getScale());
	}

	public boolean canSwim() {
		return hasFlag(GolemFlags.SWIM);
	}

	public boolean isMovable() {
		return getMode().isMovable() && !isInSittingPose();
	}

	public void travel(Vec3 pTravelVector) {
		if (!isMovable()) {
			pTravelVector = Vec3.ZERO;
		}
		if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.isInWater() && canSwim()) {
			this.moveRelative(0.08F, pTravelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (this.isControlledByLocalInstance()) {
				super.travel(pTravelVector);
			}
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
		return isMovable();
	}

	@Deprecated
	public boolean isPushedByFluid() {
		return !this.isSwimming() && isMovable();
	}

	public void setOwnerUUID(@Nullable UUID id) {
		owner = id;
		entityData.set(OWNER_ID, Optional.ofNullable(owner));
	}

	@Nullable
	public final UUID getOwnerUUID() {
		if (level().isClientSide()) {
			return entityData.get(OWNER_ID).orElse(null);
		}
		return owner;
	}

	@Nullable
	public UUID getLeaderUUID() {
		return leader;
	}

	public void setLeader(LivingEntity le) {
		leader = le.getUUID();
	}


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
		buffer.writeInt(getReforgeCount());
	}

	public void readSpawnData(FriendlyByteBuf data) {
		PacketCodec.from(data, Wrappers.cast(this.getClass()), getThis());
		updateAttributes(materials, Wrappers.cast(upgrades), owner);
		int reforge = data.readInt();
		if (reforge > 0)
			updateReforge(reforge);
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

	public ItemStack getAsStack() {
		return GolemHolder.setEntity(getThis());
	}

	// ------ common golem behavior

	@Override
	public boolean hasLineOfSight(Entity target) {
		if (target.level() == this.level() && hasFlag(GolemFlags.SEE_THROUGH) && validSeeThroughTarget(target))
			return true;
		return super.hasLineOfSight(target);
	}

	@Override
	public boolean canFreeze() {
		return !hasFlag(GolemFlags.FREEZE_IMMUNE);
	}

	@Override
	public boolean canBeSeenAsEnemy() {
		return !hasFlag(GolemFlags.PASSIVE) && super.canBeSeenAsEnemy();
	}

	protected boolean setTargetRaw(@Nullable LivingEntity target) {
		if (target != null && !canAttack(target)) {
			return false;
		}
		super.setTarget(target);
		return true;
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		var prev = getTarget();
		if (!setTargetRaw(target)) return;
		if (getVehicle() instanceof AbstractGolemEntity<?, ?> veh) {
			veh.setTargetRaw(target);
		}
		if (getControllingPassenger() != null) return;
		postSetTarget(prev, target);
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return !hasFlag(GolemFlags.PASSIVE);
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return canAttackImpl(target, super.canAttack(target));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.inventoryTick > 0) {
			this.inventoryTick--;
		}
		if (this.specialAttackCoolDown > 0) {
			this.specialAttackCoolDown--;
		}
		checkSize();
		// Some extra slots change while in use (e.g. ammo), but their serialized fields only reach
		// the client once at spawn. Push synced slots back every tick so previews stay current.
		tickExtraSlots();
		if (this.level().isClientSide) {
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onClientTick(this, entry.getValue());
			}
		}
		tickInventory();
		tickTracker();
	}

	@Override
	public void aiStep() {
		this.updateSwingTime();
		super.aiStep();
		if (!this.level().isClientSide && getGuardedDataImpl() > 0) {
			if (this.tickCount % 20 == 0) {
				double heal = this.getAttributeValue(GolemTypes.GOLEM_REGEN.get());
				for (var entry : getModifiersExtended().entrySet()) {
					heal = entry.getKey().onHealTick(heal, this, entry.getValue());
				}
				if (heal > 0) {
					this.heal((float) heal);
				}
				if (hasFlag(GolemFlags.REFORGE)) {
					checkReforge();
				}
			}
			for (var entry : getModifiersExtended().entrySet()) {
				entry.getKey().onAiStep(this, entry.getValue());
			}
			tickTarget();
			tickTransient();
		}
	}

	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity target) {
		Player player = getOwner();
		if (player != null) GolemTriggers.KILL.trigger((ServerPlayer) player, target);
		if (target == getTarget()) {
			targeter.findTarget();
			setTarget(targeter.getTarget());
		}
		return super.killedEntity(level, target);
	}

	@Override
	public void handleEntityEvent(byte event) {
		for (var e : modifiers.entrySet()) {
			e.getKey().handleEvent(this, e.getValue(), event);
		}
		super.handleEntityEvent(event);
	}

	// mode

	public boolean initMode(@Nullable Player player) {
		var config = getConfigEntry(null);
		int mode = config == null ? 0 : config.defaultMode;
		boolean far = config != null && config.summonToPosition && mode != 0 && recordedPosition.lengthSqr() > 0;
		BlockPos guard = far && !recordedGuardPos.equals(BlockPos.ZERO) ? recordedGuardPos : blockPosition();
		Vec3 pos = far ? recordedPosition : position();
		boolean succeed = level().isLoaded(BlockPos.containing(pos)) &&
				pos.distanceTo(position()) < MGConfig.COMMON.summonDistance.get();
		if (!succeed) {
			if (player instanceof ServerPlayer sp) {
				sp.sendSystemMessage(MGLangData.SUMMON_FAILED.get(getDisplayName()));
			}
			return false;
		} else {
			if (far && player instanceof ServerPlayer sp) {
				sp.sendSystemMessage(MGLangData.SUMMON_FAR.get(getDisplayName(), (int) pos.x(), (int) pos.y(), (int) pos.z()));
			}
		}
		setMode(mode, mode == 0 ? BlockPos.ZERO : guard);
		moveTo(pos);
		setTarget(null);
		setPersistentAngerTarget(null);
		return true;
	}

	@Override
	public boolean canChangeDimensions() {
		return getMode().canChangeDimensions() && super.canChangeDimensions();
	}


	// ------ persistent anger

	protected void defineSynchedData() {
		super.defineSynchedData();
		GOLEM_DATA.register(this.entityData);
		entityData.define(ARROW_SLOT, ItemStack.EMPTY);
		entityData.define(ARROW_SLOT_2, ItemStack.EMPTY);
	}

	// ------ tamable

	@Nullable
	public Team getTeam() {
		LivingEntity owner = this.getOwner();
		if (owner != null) {
			return owner.getTeam();
		}
		return super.getTeam();
	}

	public boolean isAlliedTo(Entity other) {
		return isAlliedToImpl(other, super.isAlliedTo(other));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
		}
		return super.doHurtTarget(target);
	}

	public int aiHurtTarget(Entity target) {
		boolean ans = doHurtTarget(target);
		return ans ? -1 : 0;
	}

	protected void registerGoals() {
		meleeGoal = new GolemMeleeGoal(this);
		this.goalSelector.addGoal(0, new GolemFloatGoal(this));
		this.goalSelector.addGoal(1, new TeleportToOwnerGoal(this));
		this.goalSelector.addGoal(4, new FollowOwnerGoal(this));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new GolemRandomStrollGoal(this));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, targeter = new Golem3DTargetGoal(this, 5));
		this.targetSelector.addGoal(6, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	/**
	 * Where a shoulder mounted weapon should aim at, as an offset from this entity. Golem types
	 * tracking their own aim point override this, otherwise it falls back to the current target.
	 */
	public Vec3 getTargetAimPos() {
		LivingEntity target = getTarget();
		if (target == null) return Vec3.ZERO;
		return target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(position());
	}

	/**
	 * Tick count of the shoulder weapon deploy animation, or a negative value when it is not
	 * deploying. Golem types with a shoulder weapon drive this from their own anim state.
	 */
	public int getShoulderAnimStarting() {
		return -1;
	}

	/**
	 * Tick count of the shoulder weapon retract animation, or a negative value when it is not
	 * retracting.
	 */
	public int getShoulderAnimEnding() {
		return -1;
	}

	/**
	 * Aim pose for a shoulder cannon using the given base pose. Golem types that mount the weapon
	 * differently override this with their own pose.
	 */
	public CannonPoseUtil getCannonPose(CannonPoseUtil base) {
		return base;
	}

	@Override
	public boolean isInvulnerable() {
		return hasFlag(GolemFlags.IMMUNITY);
	}

	public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity target) {
		return GolemMeleeGoal.calculateDistSqr(this, target);
	}

	public void resetTarget(@Nullable LivingEntity le) {
		for (var e : targetSelector.getAvailableGoals()) {
			if (e.getGoal() instanceof TargetGoal t) {
				t.stop();
			}
		}
		if (le != null) {
			setLastHurtByMob(le);
			forcedTarget = le;
		}
	}

	public ItemWrapper getWrapperOfHand(EquipmentSlot slot) {
		return ItemWrapper.simple(() -> this.getItemBySlot(slot), e -> super.setItemSlot(slot, e));
	}

	@Nullable
	@Override
	public Entity changeDimension(ServerLevel level, ITeleporter teleporter) {
		if (!MGConfig.COMMON.allowDimensionChange.get()) {
			return null;
		}
		return super.changeDimension(level, teleporter);
	}

	public boolean isInRangedMode() {
		return getMode() == GolemModes.STAND || entityData().get(AbstractGolemEntity.IS_IN_RANGE_ATTACK) || hasRangeAttack();
	}

	public void setInRangeAttack(boolean flag) {
		entityData().set(AbstractGolemEntity.IS_IN_RANGE_ATTACK, flag);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance ins) {
		if (effectImmunity.contains(ins.getEffect()))
			return false;
		for (var ent : modifiers.entrySet()) {
			if (ent.getKey().isImmuneTo(this, ins, ent.getValue())) {
				return false;
			}
		}
		return super.canBeAffected(ins);
	}

	@Override
	protected boolean isEffectImmune() {
		return hasFlag(GolemFlags.EFFECT_IMMUNE);
	}

	@Override
	public void makeStuckInBlock(BlockState state, Vec3 vec) {
		if (hasFlag(GolemFlags.FREE_MOVE)) return;
		super.makeStuckInBlock(state, vec);
	}

	@Override
	public void onRemove(RemovalReason reason) {
		if (reason.shouldDestroy())
			untrackRemoved(null);
	}

	@Override
	public boolean specialDeath(DamageSource source) {
		if (untrackRemoved(source)) {
			playDeathSound();
			dead = true;
			discard();
			return true;
		}
		return false;
	}

	@Override
	protected void tickDeath() {
		if (getGuardedDataImpl() > 0) return;
		if (untrackRemoved(null)) {
			discard();
		}
		super.tickDeath();
	}

	public void speedUpUseItem(int tick) {
		if (useItemRemaining > 0) {
			useItemRemaining -= tick;
		}
	}

	@Override
	public void setPosRaw(double x, double y, double z) {
		trackPos(x, y, z);
		super.setPosRaw(x, y, z);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> new CombinedInvWrapper(aggregateInventories().toArray(new IItemHandlerModifiable[0]))).cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void onHeal(float heal) {
		for (var entry : getModifiers().entrySet()) {
			entry.getKey().onHealPost(heal, this, entry.getValue());
		}
	}

	@Override
	public void heal(float heal) {
		for (var entry : getModifiers().entrySet()) {
			heal = entry.getKey().onHealPre(heal, this, entry.getValue());
		}
		super.heal(heal);
	}

	@Override
	public void lookAt(Entity e, float x, float y) {
		if (!getMode().isMovable())
			getLookControl().setLookAt(e, x, y);
		else super.lookAt(e, x, y);
	}

	public void saveToItem(CompoundTag tag) {
		super.save(tag);
		tag.remove("AngryAt");
	}

	@Override
	protected float getSoundVolume() {
		return super.getSoundVolume() * (isHostile() ?
				MGConfig.COMMON.hostileGolemSoundVolumeFactor.get().floatValue() :
				MGConfig.COMMON.golemSoundVolumeFactor.get().floatValue()
		);
	}

	@Override
	protected float dynamicReductionRate() {
		if (!hasFlag(GolemFlags.DYNAMIC_REDUCTION)) return 0;
		return (float) getAttributeValue(GolemTypes.DYNAMIC_REDUCTION.get()) * 20;
	}

	/** 进入/退出原版三叉戟激流旋身姿态（SPIN_ATTACK），供海神激流冲锋使用。 */
	public void setRiptideSpin(boolean spinning) {
		this.setLivingEntityFlag(LIVING_ENTITY_FLAG_SPIN_ATTACK, spinning);
	}

	@Override
	protected void hurtArmor(DamageSource source, float damage) {
		hurtArmorImpl(source, damage);
	}

	@Override
	public void playDeathSound() {
		EsterEggUtil.playDeathSound(this);
	}

}

package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2core.base.entity.SyncedData;
import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.capability.PathConfig;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.goals.*;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.targeting.Golem3DTargetGoal;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.equipments.CustomDropGolemWeapon;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.equipments.TickEquipmentItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.wand.GolemTransportHandler;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.events.event.GolemToOwnerEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.EntityArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;

import javax.annotation.Nullable;
import java.util.*;

@SerialClass
public class AbstractGolemEntity<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolem
		implements IEntityWithComplexSpawn, NeutralMob, OwnableEntity, PowerableMob {

	private static <T> EntityDataAccessor<T> defineId(EntityDataSerializer<T> ser) {
		return SynchedEntityData.defineId(AbstractGolemEntity.class, ser);
	}

	private static final SyncedData GOLEM_DATA = new SyncedData(AbstractGolemEntity::defineId);
	private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = GOLEM_DATA.define(SyncedData.UUID, Optional.empty(), null);

	protected AbstractGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
		this.waterNavigation = new AmphibiousPathNavigation(this, level);
		this.groundNavigation = new GroundPathNavigation(this, level);
	}

	// ------ materials

	@SerialField
	private ArrayList<GolemMaterial> materials = new ArrayList<>();
	@SerialField
	private GolemUpgrade upgrades = new GolemUpgrade(0, new ArrayList<>());
	@SerialField
	@Nullable
	private UUID owner, leader;
	@SerialField
	private HashMap<GolemModifier, Integer> modifiers = new LinkedHashMap<>();
	@SerialField
	private final HashSet<GolemFlags> golemFlags = new HashSet<>();
	@SerialField(toClient = false)
	private Vec3 recordedPosition = Vec3.ZERO;
	@SerialField(toClient = false)
	private BlockPos recordedGuardPos = BlockPos.ZERO;

	// marks opened inventory
	public int inventoryTick = 0;
	public int specialAttackCoolDown = 0;

	protected final PathNavigation waterNavigation;
	protected final GroundPathNavigation groundNavigation;

	public final Set<MobEffect> effectImmunity = new HashSet<>();

	private Golem3DTargetGoal targeter;
	public LivingEntity forcedTarget;

	public void onCreate(ArrayList<GolemMaterial> materials, GolemUpgrade upgrades, @Nullable UUID owner) {
		updateAttributes(materials, upgrades, owner);
		this.setHealth(this.getMaxHealth());
	}

	public void updateAttributes(ArrayList<GolemMaterial> materials, GolemUpgrade upgrades, @Nullable UUID owner) {
		this.materials = materials;
		this.upgrades = upgrades;
		setOwnerUUID(owner);
		this.modifiers = GolemMaterial.collectModifiers(materials, upgrades);
		this.golemFlags.clear();
		getModifiers().forEach((m, i) -> m.onRegisterFlag(golemFlags::add));
		if (canSwim()) {
			this.moveControl = new GolemSwimMoveControl(this);
			this.navigation = waterNavigation;
			this.setPathfindingMalus(PathType.WATER, 0.0F);
			this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
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

	public GolemUpgrade getUpgrades() {
		return upgrades;
	}

	public HashMap<GolemModifier, Integer> getModifiers() {
		return modifiers;
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
			if (stack.getItem() instanceof GolemEquipmentItem item) {
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

	@Nullable
	public GolemTracker getTracker() {
		var id = getOwnerUUID();
		if (id == null || id.equals(Util.NIL_UUID) || isHostile()) return null;
		if (getOwner() instanceof FakePlayer) return null;
		return GolemConfigStorage.get(level()).getTracker(id);
	}

	public void setRetrivedTo(GolemTracker.RetrieveTarget target) {
		var tracker = getTracker();
		if (tracker == null) return;
		var data = tracker.data.get(getUUID());
		if (data == null) return;
		data.target = target;
	}

	public void untrack(GolemTracker.Status type, @Nullable Entity cause) {
		var tracker = getTracker();
		if (tracker != null) tracker.untrack(this, type, cause);
	}

	@ServerOnly
	public ItemStack toItem(LivingEntity player) {
		recordedPosition = position();
		recordedGuardPos = getGuardPos();
		leader = null;
		untrack(player == getOwner() ? GolemTracker.Status.RETRIEVED : GolemTracker.Status.OTHER_RETRIEVED, player);
		var ans = GolemHolder.setEntity(getThis());
		level().broadcastEntityEvent(this, EntityEvent.POOF);
		this.discard();
		return ans;
	}

	public ItemStack asItemForDisplay() {
		return GolemHolder.setEntity(getThis());
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
			unRide();
			untrack(GolemTracker.Status.DEATH_RECYCLE, source.getEntity());
			returnToInventory();
			level().broadcastEntityEvent(this, EntityEvent.POOF);
			this.discard();
		}
		if (isAlive() && source.getEntity() instanceof LivingEntity le && predicateTarget(le)) {
			if (isWithinMeleeAttackRange(le)) {
				targeter.findTarget();
				setTarget(targeter.getTarget());
			}
		}
	}

	@Override
	protected AABB getAttackBoundingBox() {
		var r = getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
		return getBoundingBox().inflate(r);
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean player) {
		boolean skip = false;
		if (source.getDirectEntity() instanceof MetalGolemEntity golem &&
				golem.getMainHandItem().getItem() instanceof CustomDropGolemWeapon item) {
			skip = item.dropCustomDeathLoot(this, golem, golem.getMainHandItem(), source);
		}
		if (!skip) {
			Map<Item, Integer> drop = new HashMap<>();
			for (GolemMaterial mat : getMaterials()) {
				Item item = GolemMaterialConfig.get().getCraftIngredient(mat.id()).getItems()[0].getItem();
				drop.compute(item, (e, old) -> (old == null ? 0 : old) + 1);
			}
			drop.forEach((k, v) -> spawnAtLocation(new ItemStack(k, v)));
		}
		if (!isHostile()) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				dropSlot(slot, true);
			}
		}
		super.dropCustomDeathLoot(level, source, player);
	}

	protected void dropSlot(EquipmentSlot slot, boolean isDeath) {
		ItemStack itemstack = this.getItemBySlot(slot);
		if (itemstack.isEmpty()) return;
		var bind = EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE;
		var vanish = EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP;
		if (!isDeath && EnchantmentHelper.has(itemstack, bind)) return;
		if (isDeath && EnchantmentHelper.has(itemstack, vanish)) return;
		this.spawnAtLocation(itemstack);
		this.setItemSlot(slot, ItemStack.EMPTY);
	}

	private double lastSize = 0;
	private boolean sizeDirty = false;

	private double getScaleImpl() {
		int reforge = getReforgeBase();
		double rate = Math.pow(1d * (reforge - getReforgeCount()) / reforge, 1d / 3);
		return getAttributeValue(GolemTypes.GOLEM_SIZE) * rate;
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
		if (materials == null || materials.isEmpty() || level().isClientSide() && !isAddedToLevel() || getTags().contains("ClientOnly")) {
			return 1;
		}
		var def = DefaultAttributes.getSupplier(getType()).getValue(GolemTypes.GOLEM_SIZE);
		return (float) (getScaleImpl() / def);
	}

	public void calculateEntityAnimation(boolean hasY) {
		float f = (float) Mth.length(this.getX() - this.xo, hasY ? this.getY() - this.yo : 0.0D, this.getZ() - this.zo);
		this.updateWalkAnimation(f / getScale());
	}

	// ------ swim

	public boolean canSwim() {
		return hasFlag(GolemFlags.SWIM);
	}

	public void travel(Vec3 pTravelVector) {
		if (!getMode().isMovable()) {
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
		return getMode().isMovable();
	}

	public boolean isPushedByFluid() {
		return !this.isSwimming() && getMode().isMovable();
	}

	// ------ ownable entity

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
	public Player getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	@Nullable
	public LivingEntity getLeader() {
		try {
			UUID uuid = this.getLeaderUUID();
			if (uuid == null) return null;
			if (!(level() instanceof ServerLevel sl)) return null;
			var e = sl.getEntity(uuid);
			if (!(e instanceof LivingEntity le)) return null;
			return le;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	@Nullable
	public UUID getLeaderUUID() {
		return leader;
	}

	public void setLeader(LivingEntity le) {
		leader = le.getUUID();
	}

	public final boolean isHostile() {
		return HostileGolemRegistry.isHostile(getOwnerUUID());
	}

	// ------ addition golem behavior

	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.addPersistentAngerSaveData(tag);
		var pvd = level().registryAccess();
		tag.put("auto-serial", Objects.requireNonNull(new TagCodec(pvd).toTag(new CompoundTag(), this)));
		GOLEM_DATA.write(pvd, tag, entityData);
	}

	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.readPersistentAngerSaveData(this.level(), tag);
		var pvd = level().registryAccess();
		if (tag.contains("auto-serial")) {
			Wrappers.run(() -> new TagCodec(pvd).fromTag(tag.getCompound("auto-serial"), this.getClass(), this));
		}
		updateAttributes(materials, Wrappers.cast(getUpgrades()), owner);
		GOLEM_DATA.read(pvd, tag, entityData);

	}

	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
		buffer.writeInt(getReforgeCount());
	}

	public void readSpawnData(RegistryFriendlyByteBuf data) {
		PacketCodec.from(data, Wrappers.cast(this.getClass()), getThis());
		updateAttributes(materials, Wrappers.cast(upgrades), owner);
		int reforge = data.readInt();
		if (reforge > 0)
			updateReforge(reforge);
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
		return !hasFlag(GolemFlags.PASSIVE) && super.canBeSeenAsEnemy();
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (target != null && !canAttack(target)) {
			return;
		}
		super.setTarget(target);
		if (target != null) {
			TargetManager.get(this).onSetTarget(this, target);
		}
		if (target instanceof Mob mob) {
			if (mob.getTarget() == null && mob.canAttack(this)) {
				mob.setTarget(this);
			}
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onSetTarget(this, mob, entry.getValue());
			}

		}
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return !hasFlag(GolemFlags.PASSIVE);
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		var owner = getOwner();
		var leader = getLeader();
		if (target == owner || target == leader)
			return false;
		if (target instanceof OwnableEntity own) {
			var parent = own.getOwner();
			if (parent != null && (owner == parent || leader == parent)) {
				return false;
			}
		}
		if (!target.canBeSeenAsEnemy()) return false;
		if (!target.isAlive()) return false;
		if (target instanceof AbstractGolemEntity<?, ?> other) {
			if (isHostile() != other.isHostile()) return true;
			if (isHostile() && other.isHostile() && getOwnerUUID() == other.getOwnerUUID()) {
				return false;
			}
		}
		var faction = HostileGolemRegistry.tryGetFaction(this);
		if (faction.isPresent() && faction.get().hostileGolemAttacks(this, target)) {
			return true;
		}
		var config = getConfigEntry(null);
		if (config == null) {
			if (target.getType().is(MGTagGen.GOLEM_FRIENDLY)) {
				return false;
			}
		} else {
			if (config.targetFilter.friendlyToward(target)) {
				return false;
			}
		}
		return !this.isAlliedTo(target) && canAttackType(target.getType()) && super.canAttack(target);
	}

	protected float getAttackDamage() {
		return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
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
		if (this.level().isClientSide) {
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onClientTick(this, entry.getValue());
			}
		}
		for (var slot : EquipmentSlot.values()) {
			var stack = this.getItemBySlot(slot);
			if (stack.getItem() instanceof TickEquipmentItem tickItem) {
				tickItem.tick(stack, this.level(), this);
			}
		}
		var tracker = getTracker();
		if (tracker != null)
			tracker.track(this);
	}

	public void repair(float amount) {
		setHealth(Math.min(getMaxHealth(), getHealth() + amount));
	}

	public static final ResourceLocation REFORGE_ID = ModularGolems.loc("golem_reforge");

	protected int getMaxReforge() {
		return GolemType.getGolemType(getType()).getBodyPart().toItem().count - 1;
	}

	protected int getReforgeBase() {
		int total = 0;
		for (var e : GolemType.getGolemType(getType()).values()) {
			total += e.toItem().count;
		}
		return total;
	}

	public void updateReforge(int reforge) {
		getPersistentData().putInt("GolemReforge", reforge);
		if (!level().isClientSide()) {
			var ins = getAttribute(Attributes.MAX_HEALTH);
			assert ins != null;
			ins.removeModifier(REFORGE_ID);
			ins.addPermanentModifier(new AttributeModifier(
					REFORGE_ID, -1d * reforge / getReforgeBase(),
					AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			));
		}
		sizeDirty = true;
		checkSize();
		if (!level().isClientSide()) {
			ModularGolems.HANDLER.toTrackingPlayers(ReforgeUpdatePacket.of(this, reforge), this);
		}
	}

	public void checkReforge() {
		if (isAlive() && getHealth() <= getMaxHealth() / 2) {
			int reforge = getPersistentData().getInt("GolemReforge");
			if (reforge < getMaxReforge()) {
				reforge++;
				updateReforge(reforge);
				repair(getMaxHealth() / 4);
			}
		}
	}

	public boolean isReforged() {
		return getPersistentData().getInt("GolemReforge") > 0;
	}

	public int getReforgeCount() {
		return getPersistentData().getInt("GolemReforge");
	}

	public void repairWithItem() {
		int reforge = getPersistentData().getInt("GolemReforge");
		if (getHealth() > 0.75 * getMaxHealth() && reforge > 0)
			updateReforge(reforge - 1);
		else repair(getMaxHealth() / 4);
	}

	@Override
	public void aiStep() {
		this.updateSwingTime();
		super.aiStep();
		if (!this.level().isClientSide && isAlive()) {
			if (this.tickCount % 20 == 0) {
				double heal = this.getAttributeValue(GolemTypes.GOLEM_REGEN.holder());
				for (var entry : getModifiers().entrySet()) {
					heal = entry.getKey().onHealTick(heal, this, entry.getValue());
				}
				if (heal > 0) {
					this.heal((float) heal);
				}
				if (hasFlag(GolemFlags.REFORGE)) {
					checkReforge();
				}
			}
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onAiStep(this, entry.getValue());
			}
			this.updatePersistentAnger((ServerLevel) this.level(), true);
			var target = getTarget();
			if (target != null && target.isAlive()) {
				TargetManager.get(this).tickTarget(this, target);
			}
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = getItemBySlot(slot);
			if (!stack.isEmpty()) {
				try {
					stack.inventoryTick(level(), this, slot.ordinal(), slot == EquipmentSlot.MAINHAND);
				} catch (Exception e) {
					ModularGolems.LOGGER.warn("Golem cannot use item " + stack, e);
					spawnAtLocation(stack);
					setItemSlot(slot, ItemStack.EMPTY);
				}
			}
		}
	}

	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity target) {
		Player player = getOwner();
		if (player != null) GolemTriggers.KILL.get().trigger((ServerPlayer) player, target);
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
	public boolean canChangeDimensions(Level from, Level to) {
		return getMode().canChangeDimensions() && super.canChangeDimensions(from, to);
	}

	private static final EntityDataAccessor<Optional<UUID>> CONFIG_ID = GOLEM_DATA.define(SyncedData.UUID, Optional.empty(), "config_owner");
	private static final EntityDataAccessor<Integer> CONFIG_COLOR = GOLEM_DATA.define(SyncedData.INT, 0, "config_color");
	private static final EntityDataAccessor<Integer> PATROL_STAGE = GOLEM_DATA.define(SyncedData.INT, 0, "patrol_stage");

	public int getConfigColor() {
		return entityData.get(CONFIG_COLOR);
	}

	@Nullable
	public GolemConfigEntry getConfigEntry(@Nullable Component dummy) {
		int configColor = getConfigColor();
		var opt = HostileGolemRegistry.tryGetFaction(this);
		if (opt.isPresent()) {
			return opt.get().getConfig(this, configColor);
		}
		UUID configOwner = entityData.get(CONFIG_ID).orElse(null);
		if (configColor < 0 || configOwner == null) return null;
		var storage = GolemConfigStorage.get(level());
		if (dummy == null) {
			return storage.getStorage(configOwner, configColor);
		} else {
			return storage.getOrCreateStorage(configOwner, configColor, dummy);
		}
	}

	public void setConfigCard(@Nullable UUID owner, int color) {
		entityData.set(CONFIG_ID, Optional.ofNullable(owner));
		entityData.set(CONFIG_COLOR, color);
	}

	public void setPatrolStage(int stage) {
		entityData.set(PATROL_STAGE, stage);
	}

	public int getPatrolStage() {
		return entityData.get(PATROL_STAGE);
	}

	public void advancePatrolStage() {
		var list = PathConfig.getPath(this);
		if (list == null || !list.match(level())) return;
		int stage = getPatrolStage();
		stage++;
		if (stage >= list.pos().size()) {
			stage = 0;
		}
		setPatrolStage(stage);
	}

	public List<BlockPos> getPatrolList() {
		var list = PathConfig.getPath(this);
		if (list == null || !list.match(level())) return List.of();
		int stage = getPatrolStage();
		if (stage > 0 && stage < list.pos().size()) {
			var first = list.pos().subList(stage, list.pos().size());
			var second = list.pos().subList(0, stage);
			var ans = new ArrayList<>(first);
			ans.addAll(second);
			return ans;
		}
		return list.pos();
	}

	// ------ persistent anger

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = GOLEM_DATA.define(SyncedData.INT, 0, null);
	private static final EntityDataAccessor<Boolean> IS_IN_RANGE_ATTACK = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.BOOLEAN);

	@Nullable
	private UUID persistentAngerTarget;

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		GOLEM_DATA.register(builder);
		builder.define(IS_IN_RANGE_ATTACK, false);
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

	public PlayerTeam getTeam() {
		LivingEntity owner = this.getOwner();
		if (owner != null) {
			return owner.getTeam();
		}
		return super.getTeam();
	}

	public boolean canModify(Player player) {
		return canModify(player, true);
	}

	public boolean canWandModify(Player player) {
		boolean bypass = MGConfig.COMMON.wandBypassConfig.get() || ConfigCard.filterMatch(player, this);
		return canModify(player, !bypass);
	}

	public boolean canModify(Player player, boolean checkLock) {
		var entry = getConfigEntry(null);
		if (checkLock && entry != null && entry.locked)
			return false;
		LivingEntity owner = this.getOwner();
		if (player == owner) {
			return true;
		}
		if (player.getAbilities().instabuild || getOwnerUUID() == null && !predicateTarget(player))
			return true;
		if (MGConfig.COMMON.ownerPickupOnly.get()) {
			return false;
		}
		return isAlliedTo(player);
	}

	public boolean isAlliedTo(Entity other) {
		if (other == this) return true;
		LivingEntity owner = this.getOwner();
		if (other == owner) {
			return true;
		}
		if (owner != null) {
			return owner.isAlliedTo(other) || other.isAlliedTo(owner);
		}
		var opt = HostileGolemRegistry.tryGetFaction(this);
		if (opt.isPresent() && opt.get().isAlliedTo(this, other)) {
			return true;
		}
		return super.isAlliedTo(other);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		return super.doHurtTarget(target);
	}

	public int aiHurtTarget(Entity target) {
		boolean ans = doHurtTarget(target);
		return ans ? -1 : 0;
	}

	protected void registerGoals() {
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

	public boolean predicateTarget(LivingEntity e) {
		return TargetManager.predicateTarget(this, e) != null;
	}

	public boolean isInSittingPose() {
		return false;
	}

	@Nullable
	public LivingEntity getFollowTarget() {
		if (getMode() == GolemModes.SQUAD) {
			return getCaptain();
		}
		var leader = getLeader();
		if (leader != null) return leader;
		return getOwner();
	}

	@Nullable
	public LivingEntity getCaptain() {
		if (level() instanceof ServerLevel sl) {
			var config = getConfigEntry(null);
			if (config == null) return null;
			var uuid = config.squadConfig.getCaptainId();
			if (uuid == null) return null;
			var captain = sl.getEntity(uuid);
			if (captain == null) return null;
			if (!captain.isAlive() || captain.level() != sl) return null;
			if (captain instanceof LivingEntity le) {
				return le;
			} else return null;
		} else return null;
	}

	public Vec3 getTargetPos() {
		if (getMode() == GolemModes.ROUTE) {
			var list = PathConfig.getPath(this);
			if (list != null && list.match(level())) {
				int target = getPatrolStage();
				if (!list.pos().isEmpty()) {
					return Vec3.atCenterOf(list.pos().get(Math.min(target, list.pos().size() - 1)));
				}
			}
			return position();
		}
		if (getMode().hasPos()) {
			BlockPos pos = getGuardPos();
			return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		}
		LivingEntity owner = getFollowTarget();
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
		untrack(GolemTracker.Status.DEATH, source.getEntity());
		if (!isHostile()) {
			ModularGolems.LOGGER.info("Golem {} died, message: '{}'", this, source.getLocalizedDeathMessage(this).getString());
			Player owner = getOwner();
			if (owner != null && !level().isClientSide) {
				owner.sendSystemMessage(source.getLocalizedDeathMessage(this));
			}
		}
		super.die(source);
	}

	public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity target) {
		return GolemMeleeGoal.calculateDistSqr(this, target);
	}

	public void checkRide(LivingEntity target) {
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
	public Entity changeDimension(DimensionTransition dim) {
		if (!MGConfig.COMMON.allowDimensionChange.get()) {
			return null;
		}
		return super.changeDimension(dim);
	}

	public boolean isInRangedMode() {
		return getMode() == GolemModes.STAND || getEntityData().get(IS_IN_RANGE_ATTACK);
	}

	public void setInRangeAttack(boolean flag) {
		getEntityData().set(IS_IN_RANGE_ATTACK, flag);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance ins) {
		if (effectImmunity.contains(ins.getEffect()))
			return false;
		return super.canBeAffected(ins);
	}

	@Override
	public void makeStuckInBlock(BlockState state, Vec3 vec) {
		if (hasFlag(GolemFlags.FREE_MOVE)) return;
		super.makeStuckInBlock(state, vec);
	}

	@Override
	public void setPosRaw(double x, double y, double z) {
		trackPos(x, y, z);
		super.setPosRaw(x, y, z);
	}

	public void trackPos(double x, double y, double z) {
		if (level().isClientSide() || !isAddedToLevel()) return;
		var tracker = getTracker();
		if (tracker != null)
			tracker.trackPos(getUUID(), x, y, z);
	}

	public void returnToInventory() {
		var leader = getLeader();
		ItemStack stack = GolemHolder.setEntity(getThis());
		if (leader != null && leader.isAlive()) {
			if (NeoForge.EVENT_BUS.post(new GolemToOwnerEvent(leader, stack)).isCanceled()) {
				return;
			}
		}
		Player player = getOwner();
		if (player != null && player.isAlive()) {
			if (NeoForge.EVENT_BUS.post(new GolemToOwnerEvent(player, stack)).isCanceled()) {
				return;
			}
			if (player instanceof ServerPlayer sp)
				GolemTransportHandler.addGolemToPlayer(sp, stack, this);
		} else {
			spawnAtLocation(stack);
		}
	}

	public List<IItemHandlerModifiable> aggregateInventories() {
		var ans = new ArrayList<IItemHandlerModifiable>();
		ans.add(new EntityHandsInvWrapper(this));
		ans.add(new EntityArmorInvWrapper(this));
		NeoForge.EVENT_BUS.post(new GolemCollectInventoryEvent(this, ans));
		return ans;
	}

	public IItemHandler getItemHandler() {
		return new CombinedInvWrapper(aggregateInventories().toArray(new IItemHandlerModifiable[0]));
	}

	public boolean hasRangeAttack() {
		return false;
	}

}

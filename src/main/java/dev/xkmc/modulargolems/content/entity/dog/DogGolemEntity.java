package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.TargetingAnimState;
import dev.xkmc.modulargolems.content.entity.goals.GolemRiddenMeleeGoal;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class DogGolemEntity extends AbstractGolemEntity<DogGolemEntity, DogGolemPartType> {
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getArrowSlot() { return super.getArrowSlot(); }
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getArrowSlot2() { return super.getArrowSlot2(); }
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getBannerSlot() { return super.getBannerSlot(); }
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getSkinSlot() { return super.getSkinSlot(); }
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getRouteSlot() { return super.getRouteSlot(); }
	public dev.xkmc.mob_weapon_api.api.ai.ItemWrapper getShoulderSlot() { return super.getShoulderSlot(); }

	public float getJumpStrength() {
		float ans = (float) getAttributeValue(GolemTypes.GOLEM_JUMP.get());
		ans *= getScale();
		MobEffectInstance ins = getEffect(MobEffects.JUMP);
		if (ins != null) {
			int lv = ins.getAmplifier() + 1;
			ans *= (1 + lv * 0.625f);
		}
		return ans;
	}

	public DogGolemEntity(EntityType<DogGolemEntity> type, Level level) {
		super(type, level);
		setMaxUpStep(1);
	}

	public float getTailAngle() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			float percentage = 1 - this.getGuardedDataImpl() / this.getMaxHealth();
			return (0.55F - percentage * 0.16F) * (float) Math.PI;
		}
	}

	// ride

	protected void tickRidden(Player player, Vec3 vec3) {
		super.tickRidden(player, vec3);
		Vec2 vec2 = this.getRiddenRotation(player);
		this.setRot(vec2.y, vec2.x);
		this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
		if (this.isControlledByLocalInstance()) {
			if (this.onGround() || this.isInFluidType()) {
				if (player.jumping) {
					this.executeRidersJump(vec3);
				}
			}
		}

	}

	protected Vec2 getRiddenRotation(LivingEntity rider) {
		return new Vec2(rider.getXRot() * 0.5F, rider.getYRot());
	}

	protected Vec3 getRiddenInput(Player player, Vec3 input) {
		float f = player.xxa * 0.5F;
		float f1 = player.zza;
		if (f1 <= 0.0F) {
			f1 *= 0.25F;
		}
		var ans = new Vec3(f, 0.0D, f1);
		if (player.isShiftKeyDown()) {
			ans = ans.add(0, -1, 0);
		}
		return ans;
	}

	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		if (entity instanceof Player pl) {
			return pl;
		}
		if (entity instanceof AbstractGolemEntity<?, ?> pl) {
			return pl;
		}
		return null;
	}

	protected float getRiddenSpeed(Player rider) {
		return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) *
				MGConfig.COMMON.riddenSpeedFactor.get());
	}

	EarthquakeHelper.Instance jumpAttack;
	int jumpAttackDelay = 0;

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		var target = getTarget();
		if (target == null) this.entityData.set(SHOULDER_TARGET, new Vector3f(0, 0, 0));
		else this.entityData.set(SHOULDER_TARGET, target.position().add(0, target.getBbHeight() / 2, 0).subtract(position()).toVector3f());
		syncShoulderItem();
		tickShoulderWeapon();
		dismountRidersWithShoulderWeapon();
		if (jumpAttackDelay > 0) jumpAttackDelay--;
		if (jumpAttackDelay <= 0 && jumpAttack != null && onGround()) {
			jumpAttack.modifier().performEarthQuake(this, jumpAttack.lv());
			jumpAttack = null;
		}
	}

	/**
	 * Runs on both sides, so the client mirrors the deploy state the server drives. The aim point
	 * itself comes from synced data, since the client cannot see the server side target.
	 */
	@Override
	public void aiStep() {
		super.aiStep();
		shoulderAnimState.tick(this);
	}

	private static final EntityDataAccessor<Vector3f> SHOULDER_TARGET = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.VECTOR3);
	private static final EntityDataAccessor<ItemStack> SHOULDER_ITEM = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	public final TargetingAnimState shoulderAnimState = new TargetingAnimState();

	/**
	 * Mirrors the metal golem: the shoulder slot lives in a plain field while the client reads it
	 * back from synced data, and the field is mirrored into that data every tick.
	 */
	private void syncShoulderItem() {
		var stack = getShoulderSlot().getItem();
		if (!ItemStack.matches(this.entityData.get(SHOULDER_ITEM), stack)) {
			this.entityData.set(SHOULDER_ITEM, stack.copy());
		}
	}

	/**
	 * The client copy of the shoulder weapon, read from synced data the same way the metal golem
	 * reads its shoulders, so tags written server side during an action reach the renderer. An item
	 * form is not in the world, so it has no synced data to read and falls back to the field.
	 */
	public ItemStack getShoulderSlotForRender() {
		if (!this.level().isClientSide) return getShoulderSlot().getItem();
		if (!isAddedToWorld()) return getShoulderSlot().getItem();
		return this.entityData.get(SHOULDER_ITEM);
	}

	/**
	 * The aim point is kept in synced data rather than read from the target directly, because the
	 * client has no access to the server side target and the shoulder pose has to run over there.
	 */
	@Override
	public Vec3 getTargetAimPos() {
		return new Vec3(this.entityData.get(SHOULDER_TARGET));
	}

	@Override
	public int getShoulderAnimStarting() {
		return shoulderAnimState.getStartingAnim();
	}

	@Override
	public int getShoulderAnimEnding() {
		return shoulderAnimState.getEndingAnim();
	}

	/**
	 * Shoulder weapons fire on their own, independently of the melee goal.
	 */
	private void tickShoulderWeapon() {
		var stack = getShoulderSlot().getItem();
		if (!stack.isEmpty() && stack.getItem() instanceof IShoulderWeapon weapon) {
			weapon.onTick(this, stack, InteractionHand.MAIN_HAND);
		}
	}

	/**
	 * A shoulder mounted weapon blocks riding, so anyone already on board is dropped off.
	 */
	private void dismountRidersWithShoulderWeapon() {
		if (getShoulderSlot().getItem().isEmpty()) return;
		for (var rider : List.copyOf(getPassengers())) {
			rider.stopRiding();
		}
	}

	protected void executeRidersJump(Vec3 action) {
		Vec3 vec3 = this.getDeltaMovement();
		float jump = getJumpStrength();
		this.hasImpulse = true;
		var ins = EarthquakeHelper.findMountInstance(this);
		if (ins != null) {
			ins.modifier().performJump(this, ins.lv());
			ins.addCD();
			ModularGolems.HANDLER.toServer(DogSkillToServer.of(ins));
			return;
		}
		this.setDeltaMovement(vec3.x, jump, vec3.z);
		ForgeHooks.onLivingJump(this);
		if (action.z <= 0.0D) return;
		float x0 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
		float z0 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
		this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * x0 * jump, 0.0D, 0.4F * z0 * jump));
	}

	public double getPassengersRidingOffset() {
		return this.getBbHeight() * 0.9 - 0.25;
	}

	protected void positionRider(Entity rider, Entity.MoveFunction setPos) {
		int index = this.getPassengers().indexOf(rider);
		int total = this.getPassengers().size();
		if (index < 0) return;
		float width = getBbWidth();
		float offset = index == 0 ? 0.7f : index + (getControllingPassenger() instanceof Player ? 1.7f : 1.2f);
		float pos = width / 2 - width / total * offset;
		double dy = rider.getMyRidingOffset() + getPassengersRidingOffset();
		Vec3 vec3 = new Vec3(0, 0, pos).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
		setPos.accept(rider, this.getX() + vec3.x, this.getY() + dy, this.getZ() + vec3.z);
		if (index > 0) {
			this.clampRotation(rider);
		}
	}

	public void onPassengerTurned(Entity rider) {
		if (this.getControllingPassenger() != rider) {
			this.clampRotation(rider);
		}
	}

	private void clampRotation(Entity rider) {
		rider.setYBodyRot(this.getYRot());
		float yr0 = rider.getYRot();
		float dyr = Mth.wrapDegrees(yr0 - this.getYRot());
		float yr1 = Mth.clamp(dyr, -160.0F, 160.0F);
		rider.yRotO += yr1 - dyr;
		float yr2 = yr0 + yr1 - dyr;
		rider.setYRot(yr2);
		rider.setYHeadRot(yr2);
	}

	protected boolean canAddPassenger(Entity entity) {
		// a shoulder mounted weapon takes up the back, so it cannot be ridden
		if (!getShoulderSlot().getItem().isEmpty()) return false;
		float total = 0;
		int count = 0;
		var list = new ArrayList<>(getPassengers());
		list.add(entity);
		for (var e : list) {
			count++;
			total += e.getBbWidth();
		}
		double size = getAttributeValue(GolemTypes.GOLEM_SIZE.get());
		return count <= Math.min(size * 2 - 1, 3) && total <= getBbWidth() + 1e-3;
	}

	@Override
	protected void addPassenger(Entity rider) {
		setInSittingPose(false);
		super.addPassenger(rider);
	}

	// sit

	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.BYTE);

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte) 0);
		this.entityData.define(SHOULDER_TARGET, new Vector3f(0, 0, 0));
		this.entityData.define(SHOULDER_ITEM, ItemStack.EMPTY);
	}

	public void addAdditionalSaveData(CompoundTag p_21819_) {
		super.addAdditionalSaveData(p_21819_);
		p_21819_.putBoolean("Sitting", isInSittingPose());
	}

	public void readAdditionalSaveData(CompoundTag p_21815_) {
		super.readAdditionalSaveData(p_21815_);
		this.setInSittingPose(p_21815_.getBoolean("Sitting"));
	}

	public boolean isInSittingPose() {
		return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
	}

	public void setInSittingPose(boolean sit) {
		if (isInSittingPose() == sit) return;
		byte b0 = this.entityData.get(DATA_FLAGS_ID);
		this.getNavigation().stop();
		this.setTarget(null);
		if (sit) {
			this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
		}

	}

	// ------ vanilla golem behavior

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, meleeGoal);
		this.goalSelector.addGoal(2, new GolemRiddenMeleeGoal(this));
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return super.canAttackType(type) && !isInSittingPose();
	}

	public boolean hurt(DamageSource source, float amount) {
		if (!this.level().isClientSide) {
			this.setInSittingPose(false);
		}
		return super.hurt(source, amount);
	}

	protected SoundEvent getHurtSound(DamageSource p_28872_) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.IRON_GOLEM_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4f * super.getSoundVolume();
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.5f;
	}

	protected void playStepSound(BlockPos p_28864_, BlockState p_28865_) {
		this.playSound(SoundEvents.WOLF_STEP, 1.0F, 1.0F);
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
	}

	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (MGConfig.COMMON.strictInteract.get() && !itemstack.isEmpty())
			return InteractionResult.PASS;
		if (!player.isShiftKeyDown() && (itemstack.isEmpty() || itemstack.getItem() instanceof IGolemEquipmentItem))
			return super.mobInteractImpl(player, hand);
		else {
			if (!this.level().isClientSide() && canModify(player)) {
				this.setInSittingPose(!this.isInSittingPose());
			}
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public boolean setTargetRaw(@Nullable LivingEntity target) {
		if (target != null && isInSittingPose()) {
			return false;
		}
		return super.setTargetRaw(target);
	}

	@Override
	public boolean isInRangedMode() {
		for (var e : getPassengers()) {
			if (e instanceof SweepGolemEntity<?, ?> h) {
				return h.isInRangedMode();
			}
		}
		return super.isInRangedMode() || isInSittingPose();
	}

	@Override
	public boolean hasRangeAttack() {
		for (var e : getPassengers()) {
			if (e instanceof SweepGolemEntity<?, ?> h) {
				return h.hasRangeAttack();
			}
		}
		return super.hasRangeAttack();
	}

	@Override
	public int getPreviewScale() {
		return 32;
	}
	
}


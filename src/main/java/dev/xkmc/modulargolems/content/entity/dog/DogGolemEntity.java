package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.goals.FollowOwnerGoal;
import dev.xkmc.modulargolems.content.entity.common.goals.GolemFloatGoal;
import dev.xkmc.modulargolems.content.entity.common.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.item.WandItem;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@SerialClass
public class DogGolemEntity extends AbstractGolemEntity<DogGolemEntity, DogGolemPartType> {
	private float standAnimO;
	protected boolean isJumping;

	public DogGolemEntity(EntityType<DogGolemEntity> type, Level level) {
		super(type, level);
		setMaxUpStep(1);
	}

	public float getTailAngle() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			float percentage = 1 - this.getHealth() / this.getMaxHealth();
			return (0.55F - percentage * 0.16F) * (float) Math.PI;
		}
	}

	//ride
	public void aiStep() {
		super.aiStep();
	}

	protected void positionRider(Entity p_289569_, Entity.MoveFunction p_289558_) {
		super.positionRider(p_289569_, p_289558_);
		if (this.standAnimO > 0.0F) {
			float f = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
			float f1 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
			float f2 = 0.7F * this.standAnimO;
			float f3 = 0.15F * this.standAnimO;
			p_289558_.accept(p_289569_, this.getX() + (double) (f2 * f), this.getY() + this.getPassengersRidingOffset() + p_289569_.getMyRidingOffset() + (double) f3, this.getZ() - (double) (f2 * f1));
			if (p_289569_ instanceof LivingEntity) {
				((LivingEntity) p_289569_).yBodyRot = this.yBodyRot;
			}
		}
	}

	protected void tickRidden(Player player, Vec3 vec3) {
		super.tickRidden(player, vec3);
		Vec2 vec2 = this.getRiddenRotation(player);
		this.setRot(vec2.y, vec2.x);
		this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
		if (this.isControlledByLocalInstance()) {
			if (this.onGround()) {
				if (player.jumping) {
					this.executeRidersJump(vec3);
				}
			}
		}

	}

	protected Vec2 getRiddenRotation(LivingEntity p_275502_) {
		return new Vec2(p_275502_.getXRot() * 0.5F, p_275502_.getYRot());
	}

	protected Vec3 getRiddenInput(Player p_278278_, Vec3 p_275506_) {
		float f = p_278278_.xxa * 0.5F;
		float f1 = p_278278_.zza;
		if (f1 <= 0.0F) {
			f1 *= 0.25F;
		}
		return new Vec3(f, 0.0D, f1);
	}

	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		if (entity instanceof LivingEntity) {
			return (LivingEntity) entity;
		}
		return null;
	}

	protected float getRiddenSpeed(Player p_278336_) {
		return (float) ((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)*0.8);
	}

	//jump when ridding
	public double getCustomJump() {
		return 2;
	}

	public void setIsJumping(boolean p_30656_) {
		this.isJumping = p_30656_;
	}

	protected void executeRidersJump(Vec3 p_275435_) {
		Vec3 vec3 = this.getDeltaMovement();
		this.setDeltaMovement(vec3.x, 0.50F, vec3.z);
		this.setIsJumping(true);
		this.hasImpulse = true;
		net.minecraftforge.common.ForgeHooks.onLivingJump(this);
		if (p_275435_.z > 0.0D) {
			float f = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
			float f1 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
			this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * f * 0.50F, 0.0D, 0.4F * f1 * 0.50F));
		}

	}
	// sit

	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.BYTE);

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte) 0);
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
		this.goalSelector.addGoal(0, new GolemFloatGoal(this));
		this.goalSelector.addGoal(1, new GolemMeleeGoal(this, 1.0D, true));
		this.goalSelector.addGoal(6, new FollowOwnerGoal(this));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		registerTargetGoals();
	}

	@Override
	protected boolean predicatePriorityTarget(LivingEntity e) {
		return !isInSittingPose() && super.predicatePriorityTarget(e);
	}

	@Override
	protected boolean predicateSecondaryTarget(LivingEntity e) {
		return !isInSittingPose() && super.predicateSecondaryTarget(e);
	}

	public boolean hurt(DamageSource source, float amount) {
		if (!this.level().isClientSide) {
			this.setInSittingPose(false);
		}
		return super.hurt(source, amount);
	}

	protected SoundEvent getHurtSound(DamageSource p_28872_) {
		return SoundEvents.WOLF_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WOLF_DEATH;
	}

	protected void playStepSound(BlockPos p_28864_, BlockState p_28865_) {
		this.playSound(SoundEvents.WOLF_STEP, 1.0F, 1.0F);
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
	}

	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getItemInHand(hand).getItem() instanceof WandItem) return InteractionResult.PASS;
		ItemStack itemstack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && itemstack.isEmpty())
			return super.mobInteract(player, hand);
		else {
			if (!this.level().isClientSide())
				this.setInSittingPose(!this.isInSittingPose());
			return InteractionResult.SUCCESS;
		}
	}

}


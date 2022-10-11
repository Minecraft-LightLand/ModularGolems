package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@SerialClass
public class DogGolemEntity extends AbstractGolemEntity<DogGolemEntity, DogGolemPartType> {

	public DogGolemEntity(EntityType<DogGolemEntity> type, Level level) {
		super(type, level);
	}

	public float getTailAngle() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			return (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float) Math.PI;
		}
	}

	// sit

	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.BYTE);
	private boolean orderedToSit;

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte) 0);
	}

	public void addAdditionalSaveData(CompoundTag p_21819_) {
		super.addAdditionalSaveData(p_21819_);
		p_21819_.putBoolean("Sitting", this.orderedToSit);
	}

	public void readAdditionalSaveData(CompoundTag p_21815_) {
		super.readAdditionalSaveData(p_21815_);
		this.orderedToSit = p_21815_.getBoolean("Sitting");
		this.setInSittingPose(this.orderedToSit);
	}

	public boolean isInSittingPose() {
		return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
	}

	public void setInSittingPose(boolean p_21838_) {
		byte b0 = this.entityData.get(DATA_FLAGS_ID);
		if (p_21838_) {
			this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
		}

	}

	public void setOrderedToSit(boolean p_21840_) {
		this.orderedToSit = p_21840_;
	}

	// ------ vanilla golem behavior

	private int attackAnimationTick;

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		registerTargetGoals();
	}

	public boolean doHurtTarget(Entity target) {
		boolean flag = target.hurt(DamageSource.mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
		if (flag) {
			this.doEnchantDamageEffects(this, target);
		}
		return flag;
	}

	public boolean hurt(DamageSource source, float amount) {
		if (!this.level.isClientSide) {
			this.setOrderedToSit(false);
		}
		return true;
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
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.isEmpty())
			return super.mobInteract(player, hand);
		else if (itemstack.getItem() == Items.BONE) {
			this.setInSittingPose(!this.isInSittingPose());
		}
		return InteractionResult.FAIL;
	}

}


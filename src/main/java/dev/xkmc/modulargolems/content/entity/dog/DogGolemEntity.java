package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@SerialClass
public class DogGolemEntity extends AbstractGolemEntity<DogGolemEntity, DogGolemPartType> {

	public float getJumpStrength() {
		float ans = (float) getAttributeValue(GolemTypes.GOLEM_JUMP.holder());
		ans *= getScale();
		MobEffectInstance ins = getEffect(MobEffects.JUMP_BOOST);
		if (ins != null) {
			int lv = ins.getAmplifier() + 1;
			ans *= (1 + lv * 0.625f);
		}
		return ans;
	}

	public DogGolemEntity(EntityType<DogGolemEntity> type, Level level) {
		super(type, level);
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
		if (player.isLocalPlayer()) {
			if (this.onGround() || this.isInWater()) {
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

	@Nullable
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

	protected void executeRidersJump(Vec3 action) {
		Vec3 vec3 = this.getDeltaMovement();
		float jump = getJumpStrength();
		this.setDeltaMovement(vec3.x, jump, vec3.z);
		this.needsSync = true;
		CommonHooks.onLivingJump(this);
		if (action.z > 0.0D) {
			float x0 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
			float z0 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
			this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * x0 * jump, 0.0D, 0.4F * z0 * jump));
		}
	}

	protected void positionRider(Entity rider, Entity.MoveFunction setPos) {
		int index = this.getPassengers().indexOf(rider);
		int total = this.getPassengers().size();
		if (index < 0) return;
		float width = getBbWidth();
		float offset = index == 0 ? 0.7f : index + (getControllingPassenger() instanceof Player ? 1.7f : 1.2f);
		float pos = width / 2 - width / total * offset;
		Vec3 off = new Vec3(0, 0, pos).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
		Vec3 vec3 = this.getPassengerRidingPosition(rider).add(off);
		Vec3 vec31 = rider.getVehicleAttachmentPoint(this);
		setPos.accept(rider, vec3.x - vec31.x, vec3.y - vec31.y, vec3.z - vec31.z);
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
		float total = 0;
		int count = 0;
		var list = new ArrayList<>(getPassengers());
		list.add(entity);
		for (var e : list) {
			count++;
			total += e.getBbWidth();
		}
		double size = getAttributeValue(GolemTypes.GOLEM_SIZE);
		return count <= Math.min(size * 2 - 1, 3) && total <= getBbWidth() + 1e-3;
	}


	@Override
	protected void addPassenger(Entity rider) {
		setInSittingPose(false);
		super.addPassenger(rider);
	}

	// sit

	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.BYTE);

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_FLAGS_ID, (byte) 0);
	}

	public void addAdditionalSaveData(ValueOutput tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Sitting", isInSittingPose());
	}

	public void readAdditionalSaveData(ValueInput tag) {
		super.readAdditionalSaveData(tag);
		this.setInSittingPose(tag.getBooleanOr("Sitting", false));
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
		this.goalSelector.addGoal(2, new GolemMeleeGoal(this));
		super.registerGoals();
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return !isInSittingPose() && super.canAttack(target);
	}

	public boolean hurtServer(ServerLevel sl, DamageSource source, float amount) {
		if (super.hurtServer(sl, source, amount)) {
			this.setInSittingPose(false);
			return true;
		}
		return false;
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

	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.WOLF_STEP.value(), 1.0F, 1.0F);
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
	}

	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (MGConfig.COMMON.strictInteract.get() && !itemstack.isEmpty())
			return InteractionResult.PASS;
		if (!player.isShiftKeyDown() && itemstack.isEmpty())
			return super.mobInteractImpl(player, hand);
		else {
			if (!level().isClientSide()) {
				ItemStack armor = getBodyArmorItem();
				if (!armor.isEmpty() && armor.isDamaged() && armor.isValidRepairItem(itemstack)) {
					itemstack.shrink(1);
					playSound(SoundEvents.WOLF_ARMOR_REPAIR);
					int i = (int) (armor.getMaxDamage() * 0.125F);
					armor.setDamageValue(Math.max(0, armor.getDamageValue() - i));
					return InteractionResult.SUCCESS;
				}
				if (canModify(player)) {
					if (itemstack.is(MGTagGen.C_WOLF_ARMORS)) {
						if (getItemBySlot(EquipmentSlot.BODY).isEmpty()) {
							if (!level().isClientSide()) {
								setItemSlot(EquipmentSlot.BODY, itemstack.split(1));
							}
							return InteractionResult.CONSUME;
						}
					}
				}
				setInSittingPose(!isInSittingPose());
			}
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (target != null && isInSittingPose()) {
			return;
		}
		super.setTarget(target);
	}

	// armor

	protected void actuallyHurt(ServerLevel sl, DamageSource source, float amount) {
		if (!this.canArmorAbsorb(source)) {
			super.actuallyHurt(sl, source, amount);
		} else {
			ItemStack stack = getBodyArmorItem();
			int i = stack.getDamageValue();
			int j = stack.getMaxDamage();
			stack.hurtAndBreak(Mth.ceil(amount), this, EquipmentSlot.BODY);
			if (Crackiness.WOLF_ARMOR.byDamage(i, j) != Crackiness.WOLF_ARMOR.byDamage(this.getBodyArmorItem())) {
				this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
				if (level() instanceof ServerLevel serverlevel) {
					ItemStack item = Items.ARMADILLO_SCUTE.getDefaultInstance();
					/*if (ModList.get().isLoaded(MoreWolfArmors.MODID)) {
						if (stack.getItem() instanceof WolfArmorItem armor) {
							item = armor.getBreakParticleItem(stack);
						}
					} TODO more wolf armor*/
					serverlevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item.getItem()),
							getX(), getY() + 1.0, getZ(), 20, 0.2, 0.1, 0.2, 0.1);
				}
			}
		}
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

	private boolean canArmorAbsorb(DamageSource source) {
		if (source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR) || source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			return false;
		}
		return this.hasArmor();
	}

	protected void hurtArmor(DamageSource source, float amount) {
		this.doHurtEquipment(source, amount, EquipmentSlot.BODY);
	}

	public boolean hasArmor() {
		return getBodyArmorItem().is(MGTagGen.C_WOLF_ARMORS);
	}


}


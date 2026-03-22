package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.SlotWrapper;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.equipments.CustomSweepBoxWeapon;
import dev.xkmc.modulargolems.content.item.equipments.ExtraAttackGolemWeapon;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.events.event.GolemRidingOffsetEvent;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.joml.Vector3f;

import java.util.List;

@SerialClass
public class MetalGolemEntity extends SweepGolemEntity<MetalGolemEntity, MetalGolemPartType> {

	private static final EntityDataAccessor<Vector3f> TARGET = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.VECTOR3);
	private static final EntityDataAccessor<ItemStack> LEFT_SHOULDER = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> RIGHT_SHOULDER = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	@SerialField
	private ItemStack leftShoulder = ItemStack.EMPTY;
	@SerialField
	private ItemStack rightShoulder = ItemStack.EMPTY;

	@SerialField
	public final TargetingAnimState animState = new TargetingAnimState();

	public MetalGolemEntity(EntityType<MetalGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.LARGE, type, level);
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TARGET, new Vector3f(0, 0, 0));
		builder.define(LEFT_SHOULDER, ItemStack.EMPTY);
		builder.define(RIGHT_SHOULDER, ItemStack.EMPTY);
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		if (getMainHandItem().getItem() instanceof CustomSweepBoxWeapon weapon) {
			return weapon.getAttackBoundingBox(this, target, range, getMainHandItem());
		}
		return target.getBoundingBox().inflate(range);
	}

	protected boolean performDamageTarget(Entity target, float damage, double kbres) {
		if (!(level() instanceof ServerLevel sl)) return false;
		double dokb = getAttributeValue(Attributes.ATTACK_KNOCKBACK);
		var source = level().damageSources().mobAttack(this);
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
			damage = EnchantmentHelper.modifyDamage(sl, this.getWeaponItem(), target, source, damage);
			float kbench = getKnockback(target, source);
			if (kbench > dokb) dokb += Math.sqrt(kbench - dokb);
		}
		boolean succeed = target.hurt(source, damage);
		if (getMainHandItem().getItem() instanceof ExtraAttackGolemWeapon item) {
			succeed |= item.repeatAttack(this, target, damage, succeed);
		}
		if (succeed) {
			dokb = Math.max(0, dokb - kbres);
			if (dokb > 0) {
				var hor = Math.max(0, dokb - 1) * 0.4;
				var ver = Math.sqrt(dokb);
				ver = Math.min(ver * 2, ver + 1) * 0.4;
				Vec3 kbVec = target.position().subtract(position()).normalize()
						.multiply(1, 0, 1)
						.scale(hor)
						.add(0, ver, 0);
				Vec3 vec = target.getDeltaMovement();
				vec = new Vec3(vec.x / 2 + kbVec.x, Math.max(kbVec.y, vec.y), vec.z / 2 + kbVec.z);
				target.hasImpulse = true;
				target.setDeltaMovement(vec);
			}
			EnchantmentHelper.doPostAttackEffects(sl, target, source);
		}
		return succeed;
	}

	// ------ vanilla golem behavior

	private int attackAnimationTick;

	public void aiStep() {
		super.aiStep();
		animState.tick(this);
		var right = getRightShoulder().getItem();
		if (!right.isEmpty() && right.getItem() instanceof IShoulderWeapon weapon)
			weapon.onTick(this, right, InteractionHand.MAIN_HAND);
		var left = getLeftShoulder().getItem();
		if (!left.isEmpty() && left.getItem() instanceof IShoulderWeapon weapon)
			weapon.onTick(this, left, InteractionHand.OFF_HAND);

		if (this.attackAnimationTick > 0) {
			--this.attackAnimationTick;
		}
		if (this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = Mth.floor(this.getX());
			int j = Mth.floor(this.getY() - (double) 0.2F);
			int k = Mth.floor(this.getZ());
			BlockPos pos = new BlockPos(i, j, k);
			BlockState blockstate = this.level().getBlockState(pos);
			if (!blockstate.isAir()) {
				this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
			}
		}
	}

	public boolean doHurtTarget(Entity target) {
		this.attackAnimationTick = 10;
		this.level().broadcastEntityEvent(this, (byte) 4);
		float damage = this.getAttackDamage();
		double kb;
		if (target instanceof LivingEntity le) {
			kb = le.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
		} else {
			kb = 0;
		}
		boolean flag = performRangedDamage(target, damage, kb);
		this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return flag;
	}

	public boolean hurt(DamageSource source, float amount) {
		Crackiness.Level crack = this.getCrackiness();
		boolean flag = super.hurt(source, amount);
		if (flag && this.getCrackiness() != crack) {
			this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		}
		return flag;
	}

	public Crackiness.Level getCrackiness() {
		return Crackiness.GOLEM.byFraction(this.getHealth() / this.getMaxHealth());
	}

	public void handleEntityEvent(byte event) {
		if (event == 4) {
			this.attackAnimationTick = 10;
			this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		} else {
			super.handleEntityEvent(event);
		}

	}

	public int getAttackAnimationTick() {
		return this.attackAnimationTick;
	}

	protected SoundEvent getHurtSound(DamageSource p_28872_) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.IRON_GOLEM_DEATH;
	}

	protected void playStepSound(BlockPos p_28864_, BlockState p_28865_) {
		this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
	}

	public boolean checkSpawnObstruction(LevelReader p_28853_) {
		BlockPos blockpos = this.blockPosition();
		BlockPos blockpos1 = blockpos.below();
		BlockState blockstate = p_28853_.getBlockState(blockpos1);
		if (!blockstate.entityCanStandOn(p_28853_, blockpos1, this)) {
			return false;
		} else {
			for (int i = 1; i < 3; ++i) {
				BlockPos blockpos2 = blockpos.above(i);
				BlockState blockstate1 = p_28853_.getBlockState(blockpos2);
				if (!NaturalSpawner.isValidEmptySpawnBlock(p_28853_, blockpos2, blockstate1, blockstate1.getFluidState(), EntityType.IRON_GOLEM)) {
					return false;
				}
			}

			return NaturalSpawner.isValidEmptySpawnBlock(p_28853_, blockpos, p_28853_.getBlockState(blockpos), Fluids.EMPTY.defaultFluidState(), EntityType.IRON_GOLEM) && p_28853_.isUnobstructed(this);
		}
	}

	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, 0.875F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
	}

	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (getMaterials().size() != MetalGolemPartType.values().length)
			return super.mobInteractImpl(player, hand);
		var mat = getMaterials().get(MetalGolemPartType.BODY.ordinal());
		Ingredient ing = GolemMaterialConfig.get().getRepairIngredient(mat.id());
		if (!ing.test(itemstack)) {
			if (MGConfig.COMMON.strictInteract.get() && !itemstack.isEmpty())
				return InteractionResult.PASS;
			return super.mobInteractImpl(player, hand);
		}
		if (!player.getAbilities().instabuild && isHostile()) return InteractionResult.PASS;
		if (getHealth() >= getMaxHealth() && !isReforged()) {
			return InteractionResult.PASS;
		}
		repairWithItem();
		if (!player.getAbilities().instabuild) {
			itemstack.shrink(1);
		}
		if (!this.level().isClientSide()) {
			GolemTriggers.HOT_FIX.get().trigger((ServerPlayer) player);
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	@Override
	public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
		var event = new GolemRidingOffsetEvent(this);
		event.setOffset(new Vec3(0, -getBbHeight() * 0.26 + 0.27, 0));
		NeoForge.EVENT_BUS.post(event);
		return event.getOffset();
	}

	@Override
	public void checkRide(LivingEntity target) {
		if (target instanceof DogGolemEntity dog && dog.getBbWidth() > getBbWidth()) {
			startRiding(target);
		}
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		var target = getTarget();
		if (target == null) entityData.set(TARGET, new Vector3f(0, 0, 0));
		else {
			var center = target.position().add(0, target.getBbHeight() / 2, 0);
			entityData.set(TARGET, center.subtract(position()).toVector3f());
		}
		if (!ItemStack.matches(entityData.get(LEFT_SHOULDER), leftShoulder)) {
			entityData.set(LEFT_SHOULDER, leftShoulder.copy());
		}
		if (!ItemStack.matches(entityData.get(RIGHT_SHOULDER), rightShoulder)) {
			entityData.set(RIGHT_SHOULDER, rightShoulder.copy());
		}
	}

	public Vec3 getTargetAimPos() {
		return new Vec3(entityData.get(TARGET));
	}

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean player) {
		super.dropCustomDeathLoot(level, source, player);
		var left = entityData.get(LEFT_SHOULDER);
		if (!left.isEmpty())
			spawnAtLocation(left);
		var right = entityData.get(RIGHT_SHOULDER);
		if (!right.isEmpty())
			spawnAtLocation(right);
	}

	@Override
	public List<IItemHandlerModifiable> aggregateInventories() {
		var ans = super.aggregateInventories();
		ans.add(new SlotWrapper(() -> entityData.get(LEFT_SHOULDER), e -> entityData.set(LEFT_SHOULDER, e)));
		ans.add(new SlotWrapper(() -> entityData.get(RIGHT_SHOULDER), e -> entityData.set(RIGHT_SHOULDER, e)));
		return ans;
	}

	@Override
	public void addItemsToList(List<ItemStack> list) {
		super.addItemsToList(list);
		var left = entityData.get(LEFT_SHOULDER);
		if (!left.isEmpty())
			list.add(left);
		var right = entityData.get(RIGHT_SHOULDER);
		if (!right.isEmpty())
			list.add(right);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		// Legacy
		if (tag.contains("left_shoulder", Tag.TAG_COMPOUND)) {
			leftShoulder = ItemStack.parseOptional(registryAccess(), tag.getCompound("left_shoulder"));
		}
		if (tag.contains("right_shoulder", Tag.TAG_COMPOUND)) {
			rightShoulder = ItemStack.parseOptional(registryAccess(), tag.getCompound("right_shoulder"));
		}
	}

	public ItemWrapper getLeftShoulder() {
		if (level().isClientSide())
			return ItemWrapper.simple(() -> entityData.get(LEFT_SHOULDER), e -> entityData.set(LEFT_SHOULDER, e));
		return ItemWrapper.simple(() -> leftShoulder, e -> leftShoulder = e);
	}

	public ItemWrapper getRightShoulder() {
		if (level().isClientSide())
			return ItemWrapper.simple(() -> entityData.get(RIGHT_SHOULDER), e -> entityData.set(RIGHT_SHOULDER, e));
		return ItemWrapper.simple(() -> rightShoulder, e -> rightShoulder = e);
	}

}

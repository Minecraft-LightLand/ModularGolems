package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.equipments.CustomSweepBoxWeapon;
import dev.xkmc.modulargolems.content.item.equipments.ExtraAttackGolemWeapon;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import org.joml.Vector3f;

import java.util.function.Predicate;

@SerialClass
public class MetalGolemEntity extends SweepGolemEntity<MetalGolemEntity, MetalGolemPartType> {

	private static final EntityDataAccessor<Vector3f> TARGET = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.VECTOR3);

	public MetalGolemEntity(EntityType<MetalGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.LARGE, type, level);
		this.setMaxUpStep(1);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(TARGET, new Vector3f(0, 0, 0));
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		if (getMainHandItem().getItem() instanceof CustomSweepBoxWeapon weapon) {
			return weapon.getAttackBoundingBox(this, target, range, getMainHandItem());
		}
		return target.getBoundingBox().inflate(range);
	}

	protected boolean performDamageTarget(Entity target, float damage, double kbres) {
		double dokb = getAttributeValue(Attributes.ATTACK_KNOCKBACK);
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
			damage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), le.getMobType());
			float kbench = (float) EnchantmentHelper.getKnockbackBonus(this);
			if (kbench > 0) dokb += Math.sqrt(kbench);
		}
		boolean succeed = target.hurt(level().damageSources().mobAttack(this), damage);
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
			this.doEnchantDamageEffects(this, target);
			int i = EnchantmentHelper.getFireAspect(this);
			if (i > 0) {
				target.setSecondsOnFire(i * 4);
			}
		}
		return succeed;
	}


	public ItemStack getProjectile(ItemStack pShootable) {
		ItemStack ans;
		if (pShootable.getItem() instanceof ProjectileWeaponItem) {
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) pShootable.getItem()).getSupportedHeldProjectiles();
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			ans = ForgeHooks.getProjectile(this, pShootable, stack);
		} else {
			ans = ForgeHooks.getProjectile(this, pShootable, ItemStack.EMPTY);
		}
		if (isHostile()) ans = ans.copy();
		return ans;
	}

	// ------ vanilla golem behavior

	private int attackAnimationTick;

	protected void registerGoals() {
		super.registerGoals();
	}

	public void aiStep() {
		super.aiStep();
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
		if (target instanceof LivingEntity livingentity) {
			kb = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
		} else {
			kb = 0;
		}
		boolean flag = performRangedDamage(target, damage, kb);
		this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return flag;
	}

	public boolean hurt(DamageSource source, float amount) {
		IronGolem.Crackiness crack = this.getCrackiness();
		boolean flag = super.hurt(source, amount);
		if (flag && this.getCrackiness() != crack) {
			this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		}
		return flag;
	}

	public IronGolem.Crackiness getCrackiness() {
		return IronGolem.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
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
		float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
		this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
		if (!player.getAbilities().instabuild) {
			itemstack.shrink(1);
		}
		if (!this.level().isClientSide()) {
			GolemTriggers.HOT_FIX.trigger((ServerPlayer) player);
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	@Override
	public double getMyRidingOffset() {
		return -0.5;
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
	}

	public Vec3 getTargetAimPos() {
		return new Vec3(entityData.get(TARGET));
	}

}

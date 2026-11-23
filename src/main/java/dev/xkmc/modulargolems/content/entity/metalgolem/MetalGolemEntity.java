package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.IShoulderWeaponSupportEntity;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.MountChargeManager;
import dev.xkmc.modulargolems.content.entity.dog.RideCooldownIndicator;
import dev.xkmc.modulargolems.content.entity.impl.GolemInvManager.ExtraSlot;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.equipments.CustomSweepBoxWeapon;
import dev.xkmc.modulargolems.content.item.equipments.ExtraAttackGolemWeapon;
import dev.xkmc.modulargolems.content.item.equipments.IAttackListenerWeapon;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.content.item.ranged.ShoulderSaddleItem;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

@SerialClass
public class MetalGolemEntity extends SweepGolemEntity<MetalGolemEntity, MetalGolemPartType>
		implements IShoulderWeaponSupportEntity, MountChargeManager.View {

	private static final EntityDataAccessor<Vector3f> TARGET = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.VECTOR3);
	// 骑乘冲锋/大跳的同步状态：位移由客户端本地预测（与犬形同一套做法），
	// 服务端只负责伤害、冷却和结束判定，通过这几个字段广播状态。
	public static final EntityDataAccessor<Boolean> DATA_CHARGE_ACTIVE = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Float> DATA_CHARGE_DIR_X = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> DATA_CHARGE_DIR_Z = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Integer> DATA_CHARGE_CD = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_JUMP_CD = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<ItemStack> LEFT_SHOULDER = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> RIGHT_SHOULDER = SynchedEntityData.defineId(MetalGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	@SerialClass.SerialField(toClient = true)
	private ItemStack leftShoulder = ItemStack.EMPTY;
	private final ExtraSlot leftShoulderHandler = registerExtraSlot(LEFT_SHOULDER, () -> leftShoulder, e -> leftShoulder = e);
	@SerialClass.SerialField(toClient = true)
	private ItemStack rightShoulder = ItemStack.EMPTY;
	private final ExtraSlot rightShoulderHandler = registerExtraSlot(RIGHT_SHOULDER, () -> rightShoulder, e -> rightShoulder = e);

	@SerialClass.SerialField(toClient = true)
	public final TargetingAnimState animState = new TargetingAnimState();

	/** 与犬形共用同一份骑乘冲锋/大跳状态机。 */
	private final MountChargeManager<MetalGolemEntity> chargeManager = new MountChargeManager<>(this);
	private EarthquakeHelper.Instance rideJump;
	private int rideJumpDelay;
	/** 客户端按键的边沿检测：按住不放不应该每 tick 重放技能。 */
	private boolean rideJumpHeld, rideChargeHeld;
	private int rideJumpClientCd;
	/** 上一 tick 左右肩鞍的座位数，用于检测肩鞍被取下时把对应乘客放下来。 */
	private int prevLeftSeats = -1, prevRightSeats = -1;

	public MetalGolemEntity(EntityType<MetalGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.LARGE, type, level);
		registerArrowSlot(true);
		this.setMaxUpStep(1);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(TARGET, new Vector3f(0, 0, 0));
		entityData.define(LEFT_SHOULDER, ItemStack.EMPTY);
		entityData.define(RIGHT_SHOULDER, ItemStack.EMPTY);
		entityData.define(DATA_CHARGE_ACTIVE, false);
		entityData.define(DATA_CHARGE_DIR_X, 0.0F);
		entityData.define(DATA_CHARGE_DIR_Z, 0.0F);
		entityData.define(DATA_CHARGE_CD, -1);
		entityData.define(DATA_JUMP_CD, -1);
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		if (getMainHandItem().getItem() instanceof CustomSweepBoxWeapon weapon) {
			return weapon.getAttackBoundingBox(this, target, range, getMainHandItem());
		}
		return target.getBoundingBox().inflate(range);
	}

	/**
	 * 大型傀儡的击退是「掀起来」：{@code ATTACK_KNOCKBACK} 里超过 1 的部分才转成水平推力，
	 * 其余全部转成上抛（默认 1.0 → 纯上抛 0.8 格/tick）。冲锋撞击复用同一份算法。
	 */
	@Override
	public void applyAttackKnockback(LivingEntity target) {
		launchKnockback(target, getAttributeValue(Attributes.ATTACK_KNOCKBACK));
	}

	private void launchKnockback(Entity target, double dokb) {
		dokb = Math.max(0, dokb);
		if (dokb <= 0) return;
		var hor = Math.max(0, dokb - 1) * 0.4;
		var ver = Math.sqrt(dokb);
		ver = Math.min(ver * 2, ver + 1) * 0.4;
		Vec3 kbVec = target.position().subtract(position()).normalize()
				.multiply(1, 0, 1)
				.scale(hor)
				.add(0, ver, 0);
		Vec3 vec = target.getDeltaMovement();
		target.hasImpulse = true;
		target.setDeltaMovement(new Vec3(vec.x / 2 + kbVec.x, Math.max(kbVec.y, vec.y), vec.z / 2 + kbVec.z));
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
			launchKnockback(target, dokb - kbres);
			this.doEnchantDamageEffects(this, target);
			int i = EnchantmentHelper.getFireAspect(this);
			if (i > 0) {
				target.setSecondsOnFire(i * 4);
			}
		}
		return succeed;
	}

	// ------ vanilla golem behavior

	private int attackAnimationTick;

	public void aiStep() {
		super.aiStep();
		if (!level().isClientSide()) tickSaddleSeats();
		animState.tick(this);
		var right = getRightShoulder().getItem();
		if (!right.isEmpty() && right.getItem() instanceof IShoulderWeapon weapon)
			weapon.onTick(this, right, InteractionHand.MAIN_HAND);
		var left = getLeftShoulder().getItem();
		if (!left.isEmpty() && left.getItem() instanceof IShoulderWeapon weapon)
			weapon.onTick(this, left, InteractionHand.OFF_HAND);
		if (getMainHandItem().getItem() instanceof IAttackListenerWeapon weapon)
			weapon.onTick(this, getMainHandItem(), InteractionHand.MAIN_HAND);
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
		if (flag && shouldHurtWeapon(getMainHandItem())) {
			getMainHandItem().hurtAndBreak(1, this, self -> self.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
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
		return IronGolem.Crackiness.byFraction(this.getGuardedDataImpl() / this.getMaxHealth());
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
		if (getMaterials().size() != MetalGolemPartType.values().length)
			return super.mobInteractImpl(player, hand);
		var result = tryRepairWithBodyMaterial(player, hand, getMaterials().get(MetalGolemPartType.BODY.ordinal()));
		if (result != null) return result;
		if (MGConfig.COMMON.strictInteract.get() && !player.getItemInHand(hand).isEmpty())
			return InteractionResult.PASS;
		return super.mobInteractImpl(player, hand);
	}

	@Override
	public double getMyRidingOffset() {
		return -getBbHeight() * 0.26 + 0.27;
	}

	public boolean hasShoulderSaddle() {
		return getLeftShoulder().getItem().getItem() instanceof ShoulderSaddleItem
				|| getRightShoulder().getItem().getItem() instanceof ShoulderSaddleItem;
	}

	/** 体型越大，单个肩鞍能坐的人越多；左右肩各算一个肩鞍。 */
	public int getSaddleSeats() {
		return saddleSeats(getLeftShoulder().getItem()) + saddleSeats(getRightShoulder().getItem());
	}

	private int saddleSeats(ItemStack stack) {
		return stack.getItem() instanceof ShoulderSaddleItem ? Math.max(1, Math.round(getScale())) : 0;
	}

	/**
	 * 座位在实体本地坐标中的横向/前后偏移。
	 * 实体朝向 +Z 时 +X 是它的左手侧，所以左侧座位取正 X。
	 */
	public record SeatOffset(float x, float z) {
	}

	public SeatOffset getSeatOffset(int index) {
		int leftCount = saddleSeats(getLeftShoulder().getItem());
		int rightCount = saddleSeats(getRightShoulder().getItem());
		float width = getBbWidth();
		// 优先坐左侧：只有左侧没鞍时才落到右侧
		if (leftCount > 0 && index < leftCount) {
			return sideSeat(index, leftCount, width * 0.35F, width);
		}
		if (rightCount > 0) {
			return sideSeat(index - leftCount, rightCount, -width * 0.35F, width);
		}
		return sideSeat(index, 1, width * 0.35F, width);
	}

	private SeatOffset sideSeat(int index, int count, float sideX, float width) {
		float z = count <= 1 ? 0 : (index - (count - 1) / 2F) * width * 0.45F;
		return new SeatOffset(sideX, z);
	}

	@Override
	public boolean canAddPassenger(Entity rider) {
		if (!hasShoulderSaddle()) return false;
		if (!(rider instanceof Player) && !(rider instanceof AbstractGolemEntity<?, ?>)) return false;
		return getPassengers().size() < getSaddleSeats();
	}

	@Override
	protected void positionRider(Entity rider, Entity.MoveFunction setPos) {
		int index = getPassengers().indexOf(rider);
		if (index < 0) return;
		var seat = getSeatOffset(index);
		// 座位偏移必须跟着身体朝向一起转：否则傀儡转身时乘客留在世界里的固定方向，
		// 表现为玩家一转头视角就绕着傀儡打转。
		Vec3 local = new Vec3(seat.x(), 0, seat.z()).yRot(-yBodyRot * ((float) Math.PI / 180F));
		setPos.accept(rider, getX() + local.x, getY() + rider.getMyRidingOffset() + getPassengersRidingOffset(), getZ() + local.z);
	}

	/** 肩鞍座位高度系数（相对 bbHeight）。调大坐得更高。 */
	public static final double SADDLE_SEAT_RATIO = 0.7D;

	public double getPassengersRidingOffset() {
		return getBbHeight() * SADDLE_SEAT_RATIO;
	}

	/**
	 * 肩鞍被取下时，把坐在那个肩鞍上的乘客放下来（否则乘客索引会挪到另一个肩鞍的座位、与其重叠）。
	 * 座位顺序：左侧占 index [0, prevLeftSeats)，右侧占 [prevLeftSeats, prevLeftSeats + prevRightSeats)。
	 */
	private void tickSaddleSeats() {
		int left = saddleSeats(getLeftShoulder().getItem());
		int right = saddleSeats(getRightShoulder().getItem());
		if (prevLeftSeats < 0) {
			prevLeftSeats = left;
			prevRightSeats = right;
			return;
		}
		if (left >= prevLeftSeats && right >= prevRightSeats) {
			prevLeftSeats = left;
			prevRightSeats = right;
			return;
		}
		List<Entity> ps = new ArrayList<>(getPassengers());
		List<Entity> eject = new ArrayList<>();
		if (left < prevLeftSeats) {
			for (int i = 0; i < Math.min(prevLeftSeats, ps.size()); i++) eject.add(ps.get(i));
		}
		if (right < prevRightSeats) {
			int start = prevLeftSeats;
			int end = Math.min(prevLeftSeats + prevRightSeats, ps.size());
			for (int i = start; i < end; i++) eject.add(ps.get(i));
		}
		prevLeftSeats = left;
		prevRightSeats = right;
		for (var e : eject) e.stopRiding();
	}

	/**
	 * 走路时身体左右摆动的角度（度）。渲染乘客时用它跟随摆动，和 {@code MetalGolemRenderer#setupRotations}
	 * 用的是同一份算法，两边不会漂移。
	 */
	public float getRideSwayRoll(float partialTick) {
		if (walkAnimation.speed() < 0.01F) return 0;
		float period = 13.0F;
		float f1 = walkAnimation.position() - walkAnimation.speed() * (1.0F - partialTick) + 6.0F;
		float f2 = (Math.abs(f1 % period - 6.5F) - 3.25F) / 3.25F;
		return 6.5F * f2;
	}

	@Override
	public LivingEntity getControllingPassenger() {
		// 只有玩家能真正操控坐骑。若把另一个傀儡当成 controlling passenger，
		// 原版 Mob#updateControlFlags 会以为坐骑被 Mob 控制，直接关掉 MOVE/JUMP/LOOK 标志
		// → 傀儡只会被推着走，所有 AI 技能和目标攻击全部失效。
		return getPlayerPassenger();
	}

	protected Vec2 getRiddenRotation(LivingEntity rider) {
		return new Vec2(rider.getXRot() * 0.5F, rider.getYRot());
	}

	@Override
	protected Vec3 getRiddenInput(Player player, Vec3 input) {
		float forward = player.zza;
		if (forward <= 0) forward *= 0.25F;
		Vec3 ans = new Vec3(player.xxa * 0.5F, 0, forward);
		if (player.isShiftKeyDown()) ans = ans.add(0, -1, 0);
		return ans;
	}

	@Override
	protected float getRiddenSpeed(Player rider) {
		return (float) (getAttributeValue(Attributes.MOVEMENT_SPEED) * MGConfig.COMMON.riddenSpeedFactor.get());
	}

	public float getJumpStrength() {
		var attr = getAttribute(GolemTypes.GOLEM_JUMP.get());
		float ans = attr == null ? 0.5F : (float) attr.getValue();
		ans *= getScale();
		var ins = getEffect(net.minecraft.world.effect.MobEffects.JUMP);
		if (ins != null) ans *= 1 + (ins.getAmplifier() + 1) * 0.625F;
		return ans;
	}

	// ------ 骑乘（与犬形共用 MountChargeManager）

	@Override
	public MountChargeManager<?> chargeManager() {
		return chargeManager;
	}

	@Override
	public void setChargeActive(boolean active) {
		entityData.set(DATA_CHARGE_ACTIVE, active);
	}

	@Override
	public boolean isChargeActive() {
		return entityData.get(DATA_CHARGE_ACTIVE);
	}

	@Override
	public void setChargeDir(float x, float z) {
		entityData.set(DATA_CHARGE_DIR_X, x);
		entityData.set(DATA_CHARGE_DIR_Z, z);
	}

	@Override
	public float getChargeDirX() {
		return entityData.get(DATA_CHARGE_DIR_X);
	}

	@Override
	public float getChargeDirZ() {
		return entityData.get(DATA_CHARGE_DIR_Z);
	}

	@Override
	public int getJumpCd() {
		return entityData.get(DATA_JUMP_CD);
	}

	@Override
	public void setJumpCd(int cd) {
		entityData.set(DATA_JUMP_CD, cd);
	}

	@Override
	public int getChargeCd() {
		return entityData.get(DATA_CHARGE_CD);
	}

	@Override
	public void setChargeCd(int cd) {
		entityData.set(DATA_CHARGE_CD, cd);
	}

	@Override
	public EarthquakeHelper.Instance getJumpAttack() {
		return rideJump;
	}

	@Override
	public void setJumpAttack(EarthquakeHelper.Instance ins) {
		rideJump = ins;
	}

	@Override
	public int getJumpDelay() {
		return rideJumpDelay;
	}

	@Override
	public void setJumpDelay(int delay) {
		rideJumpDelay = delay;
	}

	@Override
	public net.minecraft.sounds.SoundEvent rideCooldownPing() {
		return net.minecraft.sounds.SoundEvents.VILLAGER_CELEBRATE;
	}

	@Override
	protected void tickRidden(Player player, Vec3 vec3) {
		super.tickRidden(player, vec3);
		Vec2 rot = getRiddenRotation(player);
		setRot(rot.y, rot.x);
		yRotO = yBodyRot = yHeadRot = getYRot();
		if (isControlledByLocalInstance()) {
			controlledChargeTick(player);
			if (this.onGround() || this.isInFluidType()) {
				if (player.jumping) {
					this.executeRidersJump(vec3);
				}
			}
		}
	}

	protected void executeRidersJump(Vec3 action) {
		Vec3 vec3 = this.getDeltaMovement();
		float jump = getJumpStrength();
		this.hasImpulse = true;
		var ins = dev.xkmc.modulargolems.content.modifier.special.RideSkillPick.jump(this);
		if (ins != null) {
			ins.modifier().performJump(this, ins.lv());
			ins.addCD();
			ModularGolems.HANDLER.toServer(dev.xkmc.modulargolems.content.entity.dog.DogSkillToServer.of(ins));
			return;
		}
		this.setDeltaMovement(vec3.x, jump, vec3.z);
		net.minecraftforge.common.ForgeHooks.onLivingJump(this);
		if (action.z <= 0.0D) return;
		float x0 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
		float z0 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
		this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * x0 * jump, 0.0D, 0.4F * z0 * jump));
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
		chargeManager.tickCharge();
		var target = getTarget();
		if (target == null) entityData.set(TARGET, new Vector3f(0, 0, 0));
		else {
			var center = target.position().add(0, target.getBbHeight() / 2, 0);
			entityData.set(TARGET, center.subtract(position()).toVector3f());
		}
	}

	@Override
	public Vec3 getTargetAimPos() {
		return new Vec3(entityData.get(TARGET));
	}

	@Override
	public int getShoulderAnimStarting() {
		return animState.getStartingAnim();
	}

	@Override
	public int getShoulderAnimEnding() {
		return animState.getEndingAnim();
	}

	@Override
	public int[] shoulderSlotIndexes() {
		return new int[]{-1, 1};
	}

	@Deprecated(forRemoval = true)
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		// Legacy
		if (tag.contains("left_shoulder", Tag.TAG_COMPOUND)) {
			leftShoulder = ItemStack.of(tag.getCompound("left_shoulder"));
		}
		if (tag.contains("right_shoulder", Tag.TAG_COMPOUND)) {
			rightShoulder = ItemStack.of(tag.getCompound("right_shoulder"));
		}
	}

	public ItemWrapper getLeftShoulder() {
		return leftShoulderHandler.wrapper();
	}

	public ItemWrapper getRightShoulder() {
		return rightShoulderHandler.wrapper();
	}

	@Override
	public int getPreviewScale() {
		return 20;
	}

	public boolean shouldHurtWeapon(ItemStack stack) {
		return stack.isDamageableItem() && stack.is(MGTagGen.GOLEM_DAMAGEABLE);
	}

}
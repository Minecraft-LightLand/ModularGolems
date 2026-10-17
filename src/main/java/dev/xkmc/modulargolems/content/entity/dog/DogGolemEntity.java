package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.goals.GolemRiddenMeleeGoal;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import java.util.List;
import dev.xkmc.modulargolems.init.data.MGLangData;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@SerialClass
public class DogGolemEntity extends AbstractGolemEntity<DogGolemEntity, DogGolemPartType> {

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

	private final List<RideCooldownIndicator> rideIndicators = List.of(
			new RideCooldownIndicator(new RideCooldownIndicator.Entry(
					MGLangData.RIDE_CHARGE_READY::get, this::getChargeCd, MGLangData.RIDE_CHARGE_CD::get)),
			new RideCooldownIndicator(new RideCooldownIndicator.Entry(
					MGLangData.RIDE_JUMP_READY::get, this::getJumpCd, MGLangData.RIDE_JUMP_CD::get)));

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
			this.updateRideIndicators(player);
			// 骑乘冲锋由客户端权威：服务端不回推位置，本地实例必须跑同样的位移，
			// 否则骑乘者眼前傀儡会站着不动
			if (this.entityData.get(DATA_CHARGE_ACTIVE)) {
				Vec3 d = new Vec3(getLookAngle().x, 0, getLookAngle().z);
				if (d.lengthSqr() < 1e-8) d = getChargeDirClient();
				d = d.normalize();
				ChargeHelper.moveOneTick(this, d);
				// 正在跑的技能此时已在冷却中，必须不带冷却过滤来反查
				var ins = ChargeHelper.findRunningInstance(this);
				if (ins != null && ins.modifier().hasChargeParticle() && level().getRandom().nextInt(2) == 0) {
					ChargeHelper.spawnChargeParticles(this, d, ins.modifier().getChargeParticle(), ins.modifier().hasChargeSmoke());
				}
			}
			boolean sprint = player.isSprinting() || DogRideClient.isSprintKeyDown();
			if (sprint && !this.wasSprinting) {
				this.executeRidersCharge();
			}
			this.wasSprinting = sprint;
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
		// 冲刺期间位移归冲锋所有，转向只会把它的路线带偏
		if (this.entityData.get(DATA_CHARGE_ACTIVE)) return Vec3.ZERO;
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
		setJumpCd(calcJumpCd());
		setChargeCd(calcChargeCd());
		if (jumpAttackDelay > 0) jumpAttackDelay--;
		if (jumpAttackDelay <= 0 && jumpAttack != null && onGround()) {
			jumpAttack.modifier().performEarthQuake(this, jumpAttack.lv());
			jumpAttack.modifier().onEarthQuakeLanded(this, jumpAttack.lv());
			// 落点计入冲锋技能冷却，否则冲锋会看见刚跳到的目标，一落地就冲过去
			ChargeHelper.addChargeCooldown(this);
			this.level().broadcastEntityEvent(this, EarthquakeHelper.FLAG);
			jumpAttack = null;
		}
		// 骑乘冲锋由服务端权威推进（伤害/结束判定都在这里），漏掉这个调用会让
		// DATA_CHARGE_ACTIVE 永远清不掉 → 客户端一直向前冲且没有伤害。
		if (isMountCharging()) tickMountCharge();
	}

	// 骑乘冲锋状态。chargeRider 是身上那只傀儡的技能，chargeMount 是自身的，
	// 这样双傀儡叠罗汉时可以同时用两半的技能冲刺。
	ChargeHelper.Instance chargeRider, chargeMount;
	int chargeTotal = 0;
	double chargeMoved = 0;
	int chargeTicks;
	boolean chargeBlocked;
	boolean chargeTimeLimit;
	Vec3 chargeDir;
	boolean wasSprinting;

	private static final double RIDE_CHARGE_RANGE = 16;

	/**
	 * 骑乘冲锋的防刷闸门。各技能自身冷却长得多，这里只防止按住按键在两个技能
	 * 冷却同时结束时被反复触发。
	 */
	private static final long RIDE_CHARGE_INTERVAL = 20;
	private static final String LAST_CHARGE_KEY = "lastmountcharge";

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

	void startMountCharge(ChargeHelper.Instance ins) {
		Vec3 dir = new Vec3(getLookAngle().x, 0, getLookAngle().z);
		if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
		double total = ins.modifier().getChargeDistance(ins);
		if (getGolemController() != null) {
			LivingEntity t = getGolemController().getTarget();
			if (t != null && (!t.isAlive() || !predicateTarget(t))) t = null;
			if (t == null && predicateTarget(getTarget())) t = getTarget();
			if (t != null) {
				dir = new Vec3(t.getX() - getX(), 0, t.getZ() - getZ());
				if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
				if (ins.owner() != this) total = Math.sqrt(t.distanceToSqr(this)) + 2;
			}
		}
		startMountCharge(ins, dir.normalize(), total);
	}

	void startMountCharge(ChargeHelper.Instance ins, Vec3 dir, double total) {
		long lastCharge = this.getPersistentData().getLong(LAST_CHARGE_KEY);
		if (this.level().getGameTime() < lastCharge + RIDE_CHARGE_INTERVAL) return;
		this.getPersistentData().putLong(LAST_CHARGE_KEY, this.level().getGameTime());
		if (ins.owner() == this) {
			this.chargeMount = ins;
		} else {
			this.chargeRider = ins;
		}
		this.chargeTicks = 0;
		this.chargeTotal = (int) Math.ceil(total);
		this.chargeMoved = 0;
		// 玩家手动操控，冲锋跟随视角，只由 tick 上限终止
		this.chargeTimeLimit = getControllingPassenger() instanceof Player;
		this.chargeDir = dir;
		this.chargeBlocked = false;
		this.getNavigation().stop();
		this.entityData.set(DATA_CHARGE_ACTIVE, true);
		this.entityData.set(DATA_CHARGE_DIR_X, (float) dir.x);
		this.entityData.set(DATA_CHARGE_DIR_Z, (float) dir.z);
		ins.modifier().setCharging(ins, true);
		ins.modifier().performCharge(ins);
		ins.modifier().onChargeStart(ins.owner());
	}

	private void tickMountCharge() {
		this.chargeTicks++;
		if (chargeTicks > ChargeHelper.MAX_TICKS || chargeBlocked) {
			endMountCharge();
			return;
		}
		Vec3 dir = chargeDir;
		if (chargeTimeLimit) {
			Vec3 look = new Vec3(getLookAngle().x, 0, getLookAngle().z);
			if (look.lengthSqr() > 1e-8) dir = look.normalize();
		}
		if (!ChargeHelper.moveOneTick(this, dir)) {
			chargeBlocked = true;
			endMountCharge();
			return;
		}
		var active = chargeRider != null ? chargeRider : chargeMount;
		if (active != null && active.modifier().hasChargeParticle() && level().getRandom().nextInt(3) == 0) {
			ChargeHelper.spawnChargeParticles(this, dir, active.modifier().getChargeParticle(), active.modifier().hasChargeSmoke());
		}
		chargeMoved += getAttributeValue(Attributes.MOVEMENT_SPEED) * ChargeHelper.MOVE_SPEED_MULT;
		if (chargeMoved >= chargeTotal && !chargeTimeLimit) {
			endMountCharge();
			return;
		}
		if (chargeRider != null) chargeRider.modifier().tickCharge(chargeRider);
		if (chargeMount != null) chargeMount.modifier().tickCharge(chargeMount);
		AbstractGolemEntity<?, ?> attacker = chargeRider != null ? chargeRider.owner() : this;
		AABB aabb = getBoundingBox().inflate(0.5);
		for (var e : level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!attacker.predicateTarget(e)) continue;
			float dmg = 0;
			if (chargeRider != null) {
				dmg += chargeRider.modifier().chargeDamage(chargeRider, e);
			}
			if (chargeMount != null) {
				dmg += chargeMount.modifier().chargeDamage(chargeMount, e);
			}
			if (dmg > 0) {
				// 只在真正造成伤害时才触发 onChargeTouch：受击无敌帧会让 hurt 返回 false，
				// 否则贴着目标冲锋时每 tick 都会播一次撞击音（听起来像连成一片）。
				boolean hit = e.hurt(attacker.damageSources().mobAttack(attacker), dmg);
				if (hit) {
					if (chargeRider != null) chargeRider.modifier().onChargeTouch(chargeRider, e);
					if (chargeMount != null) chargeMount.modifier().onChargeTouch(chargeMount, e);
				}
			}
		}
	}

	private void endMountCharge() {
		if (chargeRider != null) chargeRider.modifier().endCharge(chargeRider);
		if (chargeMount != null) chargeMount.modifier().endCharge(chargeMount);
		if (chargeRider != null) {
			chargeRider.modifier().setCharging(chargeRider, false);
			chargeRider = null;
		}
		if (chargeMount != null) {
			chargeMount.modifier().setCharging(chargeMount, false);
			chargeMount = null;
		}
		this.chargeTotal = 0;
		this.chargeMoved = 0;
		this.entityData.set(DATA_CHARGE_ACTIVE, false);
	}

	public boolean isMountCharging() {
		return chargeRider != null || chargeMount != null;
	}

	@Nullable
	public ChargeHelper.Instance getChargeRider() {
		return chargeRider;
	}

	@Nullable
	public ChargeHelper.Instance getChargeMount() {
		return chargeMount;
	}

	protected void executeRidersCharge() {
		var ins = ChargeHelper.findInstance(this);
		if (ins == null) return;
		ModularGolems.HANDLER.toServer(DogSkillToServer.of(ins));
	}

	private Vec3 getChargeDirClient() {
		Vec3 d = new Vec3(this.entityData.get(DATA_CHARGE_DIR_X), 0, this.entityData.get(DATA_CHARGE_DIR_Z));
		if (d.lengthSqr() < 1e-8) d = new Vec3(0, 0, 1);
		return d.normalize();
	}

	/**
	 * AI 骑乘冲锋：AI 傀儡先从自身、再从坐骑的修饰符里挑一个冲锋技能，用坐骑的身体跑。
	 */
	private void tickRiddenCharge() {
		if (jumpAttack != null || jumpAttackDelay > 0 || !onGround()) return;
		var rider = getGolemController();
		if (rider == null || rider.isInRangedMode() || isInRangedMode()) return;
		LivingEntity target = rider.getTarget();
		if (target == null || !target.isAlive() || !predicateTarget(target)) {
			target = getTarget();
		}
		if (target == null || !target.isAlive() || !predicateTarget(target)) return;
		double dist = distanceTo(target);
		if (dist <= 2 || dist > RIDE_CHARGE_RANGE) return;
		var all = findValidCharges(rider);
		all.addAll(findValidCharges(this));
		if (all.isEmpty()) return;
		var ins = all.get(getRandom().nextInt(all.size()));
		ins.addCD();
		startMountCharge(ins);
	}

	private List<ChargeHelper.Instance> findValidCharges(AbstractGolemEntity<?, ?> golem) {
		List<ChargeHelper.Instance> ans = new ArrayList<>();
		if (golem.hasFlag(GolemFlags.PASSIVE)) return ans;
		long time = level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof ChargeHelper.Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long cd = m.getCooldown(golem, e.getValue());
				if ((last + cd < time || last > time) && m.canCharge(golem, e.getValue())) {
					ans.add(new ChargeHelper.Instance(golem, m, e.getValue()));
				}
			}
		}
		return ans;
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
	private static final EntityDataAccessor<Integer> DATA_JUMP_CD = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.INT);
	// 同步冲锋方向：骑乘端在本地预测位移，首帧视线还没压平时可以退回用它
	private static final EntityDataAccessor<Boolean> DATA_CHARGE_ACTIVE = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> DATA_CHARGE_DIR_X = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_CHARGE_DIR_Z = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.FLOAT);
	// 剩余冲锋冷却，只用于给骑乘端显示倒计时
	private static final EntityDataAccessor<Integer> DATA_CHARGE_CD = SynchedEntityData.defineId(DogGolemEntity.class, EntityDataSerializers.INT);

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte) 0);
		this.entityData.define(DATA_JUMP_CD, -1);
		this.entityData.define(DATA_CHARGE_ACTIVE, false);
		this.entityData.define(DATA_CHARGE_DIR_X, 0.0F);
		this.entityData.define(DATA_CHARGE_DIR_Z, 0.0F);
		this.entityData.define(DATA_CHARGE_CD, -1);
	}

	private void updateRideIndicators(Player player) {
		RideCooldownIndicator.render(player, rideIndicators);
	}

	public int getJumpCd() {
		return this.entityData.get(DATA_JUMP_CD);
	}

	private void setJumpCd(int cd) {
		this.entityData.set(DATA_JUMP_CD, cd);
	}

	public int getChargeCd() {
		return this.entityData.get(DATA_CHARGE_CD);
	}

	private void setChargeCd(int cd) {
		this.entityData.set(DATA_CHARGE_CD, cd);
	}

	private int calcJumpCd() {
		int ans = -1;
		long time = level().getGameTime();
		for (var e : getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof EarthquakeHelper.Modifier m) {
				long last = getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long left = last + m.getCoolDown(this, e.getValue()) - time;
				ans = Math.max(ans, (int) Math.max(0, Math.min(left, Integer.MAX_VALUE)));
			}
		}
		return ans;
	}

	private int calcChargeCd() {
		int ans = -1;
		long time = level().getGameTime();
		for (var e : getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof ChargeHelper.Modifier m) {
				long last = getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				long left = last + m.getCooldown(this, e.getValue()) - time;
				ans = Math.max(ans, (int) Math.max(0, Math.min(left, Integer.MAX_VALUE)));
			}
		}
		return ans;
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


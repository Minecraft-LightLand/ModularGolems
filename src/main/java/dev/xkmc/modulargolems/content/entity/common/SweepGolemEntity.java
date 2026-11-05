package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemUser;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponManager;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.entity.impl.GolemInvManager.ExtraSlot;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;

import java.util.function.Predicate;

@SerialClass
public abstract class SweepGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolemEntity<T, P>
		implements RangedAttackMob, IWeaponHolder, CrossbowAttackMob {

	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ItemStack> BACKUP_SLOT = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	public final GolemWeaponManager<T> weaponManager;

	private boolean doReassessGoal = false;

	@SerialClass.SerialField
	private ItemStack backupHand = ItemStack.EMPTY;

	private final ExtraSlot backupSlotHandler = registerExtraSlot(BACKUP_SLOT, () -> backupHand, e -> backupHand = e);

	protected SweepGolemEntity(GolemWeaponRegistry<T> reg, EntityType<T> type, Level level) {
		super(type, level);
		weaponManager = new GolemWeaponManager<>(reg, getThis(), meleeGoal);
		if (!this.level().isClientSide) {
			weaponManager.reassessWeaponGoal();
		}
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_CHARGING_CROSSBOW, false);
		this.entityData.define(BACKUP_SLOT, ItemStack.EMPTY);
	}

	protected boolean performRangedDamage(Entity target, float damage, double kb) {
		boolean flag = performDamageTarget(target, damage, kb);
		double range = getAttributeValue(GolemTypes.GOLEM_SWEEP.get());
		if (range > 0 && canSweep()) {
			var list = level().getEntities(target, getAttackBoundingBox(target, range),
					e -> e instanceof LivingEntity le && predicateTarget(le) && this.canAttack(le));
			for (Entity t : list) {
				flag |= performDamageTarget(t, damage, kb);
			}
		}
		return flag;
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		return target.getBoundingBox().inflate(range);
	}

	/**
	 * please be aware of lastHurtByPlayer
	 */
	protected abstract boolean performDamageTarget(Entity target, float damage, double kb);

	@Override
	public boolean canSweep() {
		return getAttributeValue(GolemTypes.GOLEM_SWEEP.get()) > 0;
	}

	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
		super.setItemSlot(pSlot, pStack);
		if (!this.level().isClientSide) {
			doReassessGoal = true;
		}
	}

	public void readAdditionalSaveData(CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
		weaponManager.reassessWeaponGoal();
	}

	public boolean canFireProjectileWeapon(ProjectileWeaponItem pProjectileWeapon) {
		return true;
	}

	@Override
	public InteractionHand getWeaponHand() {
		return InteractionHand.MAIN_HAND;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power) {
		weaponManager.performRangedAttack(target, power);
	}

	@Override
	public ISmartUser toUser() {
		return new GolemUser(this, getTarget());
	}

	public void switchWeapon(ItemWrapper mainhand, ItemWrapper offhand) {
		LivingEntity target = getTarget();
		ItemStack main = mainhand.getItem();
		ItemStack off = offhand.getItem();
		if (weaponManager.checkSwitch(target, mainhand, offhand)) {
			mainhand.setItem(off);
			offhand.setItem(main);
			doReassessGoal = true;
			inventoryTick = 10;
		}
	}

	@Override
	public void aiStep() {
		if (doReassessGoal || tickCount % 100 == 0) {
			weaponManager.reassessWeaponGoal();
			doReassessGoal = false;
		}
		super.aiStep();
		if (level().isClientSide()) return;
		if (inventoryTick > 0) return;
		inventoryTick = 4;
		switchWeapon(
				getWrapperOfHand(EquipmentSlot.MAINHAND),
				getAltWeaponHand()
		);
	}

	public void triggerReassess() {
		doReassessGoal = true;
	}

	@Override
	public boolean hasRangeAttack() {
		return weaponManager.isRangedModeAvailable(getMainHandItem()) ||
				weaponManager.isRangedModeAvailable(getAltWeaponHand().getItem());
	}

	// weapon switch

	protected ItemWrapper getAltWeaponHand() {
		return !backupHand.isEmpty() ? getBackupHand() :
				getWrapperOfHand(EquipmentSlot.OFFHAND);
	}

	public ItemWrapper getBackupHand() {
		return backupSlotHandler.wrapper();
	}

	// bow and crossbow

	public boolean isChargingCrossbow() {
		return this.entityData.get(IS_CHARGING_CROSSBOW);
	}

	public void setChargingCrossbow(boolean pIsCharging) {
		this.entityData.set(IS_CHARGING_CROSSBOW, pIsCharging);
	}

	@Override
	public void onCrossbowAttackPerformed() {
		noActionTime = 0;
	}

	@Override
	public void shootCrossbowProjectile(LivingEntity target, ItemStack stack, Projectile e, float a) {
		shootCrossbowProjectile(this, target, e, a, 3);
	}

	public void shootCrossbowProjectile(LivingEntity user, LivingEntity target, Projectile e, float a, float v) {
		ShootUtils.getShootVector(target, e.position(), v, 0.05f, 0).shoot(e, a);
		user.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (user.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	public void performCrossbowAttack(LivingEntity pUser, float pVelocity) {
		InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(pUser, item -> item instanceof CrossbowItem);
		ItemStack itemstack = pUser.getItemInHand(interactionhand);
		if (pUser.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
			CrossbowItem.performShooting(pUser.level(), pUser, interactionhand, itemstack, pVelocity, 0);
		}
		onCrossbowAttackPerformed();
	}

}

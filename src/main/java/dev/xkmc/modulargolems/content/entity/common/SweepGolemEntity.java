package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.humanoid.SlotWrapper;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemUser;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponManager;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SerialClass
public abstract class SweepGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolemEntity<T, P>
		implements RangedAttackMob, IWeaponHolder, CrossbowAttackMob {

	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ItemStack> BACKUP_SLOT = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> ARROW_SLOT = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	public final GolemWeaponManager<T> weaponManager;

	private boolean doReassessGoal = false;

	@SerialClass.SerialField
	private ItemStack backupHand = ItemStack.EMPTY;
	@SerialClass.SerialField
	private ItemStack arrowSlot = ItemStack.EMPTY;

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
		this.entityData.define(ARROW_SLOT, ItemStack.EMPTY);
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

	public ItemStack getProjectile(ItemStack pShootable) {
		ItemStack ans;
		if (pShootable.getItem() instanceof ProjectileWeaponItem) {
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) pShootable.getItem()).getSupportedHeldProjectiles();
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			if (stack.isEmpty() && !arrowSlot.isEmpty() && predicate.test(arrowSlot)) {
				stack = arrowSlot;
			}
			ans = ForgeHooks.getProjectile(this, pShootable, stack);
		} else {
			ans = ForgeHooks.getProjectile(this, pShootable, ItemStack.EMPTY);
		}
		if (isHostile()) ans = ans.copy();
		return ans;
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		if (!ItemStack.matches(entityData.get(BACKUP_SLOT), backupHand)) {
			entityData.set(BACKUP_SLOT, backupHand.copy());
		}
		if (!ItemStack.matches(entityData.get(ARROW_SLOT), arrowSlot)) {
			entityData.set(ARROW_SLOT, arrowSlot.copy());
		}
	}

	// weapon switch

	protected ItemWrapper getAltWeaponHand() {
		return !backupHand.isEmpty() ? getBackupHand() :
				getWrapperOfHand(EquipmentSlot.OFFHAND);
	}

	public ItemWrapper getBackupHand() {
		if (level().isClientSide())
			return ItemWrapper.simple(() -> entityData.get(BACKUP_SLOT), e -> entityData.set(BACKUP_SLOT, e));
		return ItemWrapper.simple(() -> this.backupHand, e -> this.backupHand = e);
	}

	public ItemWrapper getArrowSlot() {
		if (level().isClientSide())
			return ItemWrapper.simple(() -> entityData.get(ARROW_SLOT), e -> entityData.set(ARROW_SLOT, e));
		return ItemWrapper.simple(() -> this.arrowSlot, e -> this.arrowSlot = e);
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

	// extra drops

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int i, boolean b) {
		super.dropCustomDeathLoot(source, i, b);
		if (!arrowSlot.isEmpty() && arrowSlot.getEnchantmentLevel(Enchantments.VANISHING_CURSE) <= 0)
			spawnAtLocation(arrowSlot);
		arrowSlot = ItemStack.EMPTY;
		if (!backupHand.isEmpty() && backupHand.getEnchantmentLevel(Enchantments.VANISHING_CURSE) <= 0)
			spawnAtLocation(backupHand);
		backupHand = ItemStack.EMPTY;
	}

	@Override
	protected List<IItemHandlerModifiable> aggregateInventories() {
		var ans = new ArrayList<IItemHandlerModifiable>();
		ans.add(new EntityHandsInvWrapper(this));
		ans.add(new EntityArmorInvWrapper(this));
		ans.add(new SlotWrapper(() -> arrowSlot, e -> arrowSlot = e));
		ans.add(new SlotWrapper(() -> backupHand, e -> backupHand = e));
		MinecraftForge.EVENT_BUS.post(new GolemCollectInventoryEvent(this, ans));
		return ans;
	}

	@Override
	public void addItemsToList(List<ItemStack> list) {
		super.addItemsToList(list);
		if (!backupHand.isEmpty()) list.add(backupHand);
		if (!arrowSlot.isEmpty()) list.add(arrowSlot);
	}

}

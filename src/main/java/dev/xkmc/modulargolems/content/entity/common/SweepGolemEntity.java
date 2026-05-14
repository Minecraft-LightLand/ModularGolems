package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.weapon.GolemUser;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponManager;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EntityArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SerialClass
public abstract class SweepGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolemEntity<T, P>
		implements RangedAttackMob, IWeaponHolder, CrossbowAttackMob {

	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ItemStack> BACKUP_SLOT = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> ARROW_SLOT = SynchedEntityData.defineId(SweepGolemEntity.class, EntityDataSerializers.ITEM_STACK);

	private final GolemWeaponManager<T> weaponManager;

	@SerialField
	private ItemStack backupHand = ItemStack.EMPTY;
	@SerialField
	private ItemStack arrowSlot = ItemStack.EMPTY;

	protected SweepGolemEntity(GolemWeaponRegistry<T> reg, EntityType<T> type, Level level) {
		super(type, level);
		weaponManager = new GolemWeaponManager<>(reg, getThis());
		if (!this.level().isClientSide()) {
			weaponManager.reassessWeaponGoal();
		}
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_CHARGING_CROSSBOW, false);
		builder.define(BACKUP_SLOT, ItemStack.EMPTY);
		builder.define(ARROW_SLOT, ItemStack.EMPTY);
	}

	public ItemStack getProjectile(ItemStack pShootable) {
		ItemStack ans;
		if (pShootable.getItem() instanceof ProjectileWeaponItem) {
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) pShootable.getItem()).getSupportedHeldProjectiles(pShootable);
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			if (stack.isEmpty() && !arrowSlot.isEmpty() && predicate.test(arrowSlot)) {
				stack = arrowSlot;
			}
			ans = CommonHooks.getProjectile(this, pShootable, stack);
		} else {
			ans = CommonHooks.getProjectile(this, pShootable, ItemStack.EMPTY);
		}
		if (isHostile()) ans = ans.copy();
		return ans;
	}

	protected boolean performRangedDamage(Entity target, float damage, double kb) {
		boolean flag = performDamageTarget(target, damage, kb);
		double range = getAttributeValue(GolemTypes.GOLEM_SWEEP.holder());
		if (range > 0 && canSweep()) {
			var list = level().getEntities(target, getAttackBoundingBox(target, range),
					e -> e instanceof LivingEntity le && predicateTarget(le) && this.canAttack(le));
			for (Entity t : list) {
				flag |= performDamageTarget(t, damage, kb);
			}
		}
		return flag;
	}

	protected boolean canSweep() {
		return true;
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		return target.getBoundingBox().inflate(range);
	}

	/**
	 * please be aware of lastHurtByPlayer
	 */
	protected abstract boolean performDamageTarget(Entity target, float damage, double kb);


	public void readAdditionalSaveData(ValueInput pCompound) {
		super.readAdditionalSaveData(pCompound);
		weaponManager.reassessWeaponGoal();
	}

	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
		super.setItemSlot(pSlot, pStack);
		if (!this.level().isClientSide()) {
			doReassessGoal = true;
		}
	}

	@Override
	public InteractionHand getWeaponHand() {
		return InteractionHand.MAIN_HAND;
	}

	public boolean canFireProjectileWeapon(ProjectileWeaponItem pProjectileWeapon) {
		return true;
	}

	@Override
	public void performRangedAttack(LivingEntity pTarget, float dist) {
		weaponManager.performRangedAttack(pTarget, dist);
	}


	private boolean doReassessGoal = false;

	@Override
	public void aiStep() {
		if (doReassessGoal || tickCount % 100 == 0) {
			weaponManager.reassessWeaponGoal();
			doReassessGoal = false;
		}
		super.aiStep();
		attackStep();
	}

	public void triggerReassess() {
		doReassessGoal = true;
	}

	@Override
	public ISmartUser toUser() {
		return new GolemUser(this, getTarget());
	}


	public void attackStep() {
		if (level().isClientSide()) return;
		if (inventoryTick > 0) return;
		inventoryTick = 4;
		switchWeapon(
				getWrapperOfHand(EquipmentSlot.MAINHAND),
				getAltWeaponHand()
		);
	}

	private void switchWeapon(ItemWrapper mainhand, ItemWrapper offhand) {
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
	public boolean hasRangeAttack() {
		return weaponManager.isRangedModeAvailable(getMainHandItem()) ||
				weaponManager.isRangedModeAvailable(getAltWeaponHand().getItem());
	}

	@Override
	protected void customServerAiStep(ServerLevel sl) {
		super.customServerAiStep(sl);
		if (!ItemStack.matches(entityData.get(BACKUP_SLOT), backupHand)) {
			entityData.set(BACKUP_SLOT, backupHand.copy());
		}
		if (!ItemStack.matches(entityData.get(ARROW_SLOT), arrowSlot)) {
			entityData.set(ARROW_SLOT, arrowSlot.copy());
		}
	}

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

	public void performCrossbowAttack(LivingEntity pUser, float pVelocity) {
		InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(pUser, item -> item instanceof CrossbowItem);
		ItemStack itemstack = pUser.getItemInHand(interactionhand);
		if (itemstack.getItem() instanceof CrossbowItem cross) {
			cross.performShooting(pUser.level(), pUser, interactionhand, itemstack, pVelocity, 0, getTarget());
		}
		this.onCrossbowAttackPerformed();
	}

	protected ItemWrapper getAltWeaponHand() {
		return !backupHand.isEmpty() ? getBackupHand() : getWrapperOfHand(EquipmentSlot.OFFHAND);
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

	@Override
	protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean player) {
		super.dropCustomDeathLoot(level, source, player);
		if (!arrowSlot.isEmpty() && EnchHelper.getLv(arrowSlot, Enchantments.VANISHING_CURSE) <= 0)
			spawnAtLocation(level, arrowSlot);
		arrowSlot = ItemStack.EMPTY;
		if (!backupHand.isEmpty() && EnchHelper.getLv(backupHand, Enchantments.VANISHING_CURSE) <= 0)
			spawnAtLocation(level, backupHand);
		backupHand = ItemStack.EMPTY;
	}


	@Override
	public List<IItemHandlerModifiable> aggregateInventories() {
		var ans = new ArrayList<IItemHandlerModifiable>();
		ans.add(new EntityHandsInvWrapper(this));
		ans.add(new EntityArmorInvWrapper(this));
		ans.add(new SlotWrapper(() -> arrowSlot, e -> arrowSlot = e));
		ans.add(new SlotWrapper(() -> backupHand, e -> backupHand = e));
		NeoForge.EVENT_BUS.post(new GolemCollectInventoryEvent(this, ans));
		return ans;
	}

	@Override
	public void addItemsToList(List<ItemStack> list) {
		super.addItemsToList(list);
		if (!backupHand.isEmpty()) list.add(backupHand);
		if (!arrowSlot.isEmpty()) list.add(arrowSlot);
	}

}

package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemUser;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponManager;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@SerialClass
public abstract class SweepGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolemEntity<T, P>
		implements RangedAttackMob, IWeaponHolder {

	private final GolemWeaponManager<T> weaponManager;

	protected SweepGolemEntity(GolemWeaponRegistry<T> reg, EntityType<T> type, Level level) {
		super(type, level);
		weaponManager = new GolemWeaponManager<>(reg, getThis());
		if (!this.level().isClientSide) {
			weaponManager.reassessWeaponGoal();
		}
	}

	protected boolean performRangedDamage(Entity target, float damage, double kb) {
		boolean flag = performDamageTarget(target, damage, kb);
		double range = getAttributeValue(GolemTypes.GOLEM_SWEEP.holder());
		if (range > 0 && canSweep()) {
			var list = level().getEntities(target, getAttackBoundingBox(target, range),
					e -> e instanceof LivingEntity le && e instanceof Enemy && (!(e instanceof Creeper)) && this.canAttack(le));
			for (Entity t : list) {
				flag |= performDamageTarget(t, damage, kb);
			}
		}
		return flag;
	}

	protected boolean canSweep(){
		return true;
	}

	protected AABB getAttackBoundingBox(Entity target, double range) {
		return target.getBoundingBox().inflate(range);
	}

	/**
	 * please be aware of lastHurtByPlayer
	 */
	protected abstract boolean performDamageTarget(Entity target, float damage, double kb);


	public void readAdditionalSaveData(CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
		weaponManager.reassessWeaponGoal();
	}

	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
		super.setItemSlot(pSlot, pStack);
		if (!this.level().isClientSide) {
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

	protected ItemWrapper getAltWeaponHand() {
		return getWrapperOfHand(EquipmentSlot.OFFHAND);
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

}

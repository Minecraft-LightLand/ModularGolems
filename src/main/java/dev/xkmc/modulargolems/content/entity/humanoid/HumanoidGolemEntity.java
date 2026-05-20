package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.entity.common.ShieldUsingGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemDisableShieldEvent;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.events.event.GolemRidingOffsetEvent;
import dev.xkmc.modulargolems.events.event.GolemSweepEvent;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Arrays;

@SerialClass
public class HumanoidGolemEntity extends ShieldUsingGolemEntity<HumanoidGolemEntity, HumanoidGolemPartType> {

	public HumanoidGolemEntity(EntityType<HumanoidGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.HUMANOID, type, level);
		if (!this.level().isClientSide()) {
			this.groundNavigation.setCanOpenDoors(true);
		}
	}

	// ------ common golem behavior

	@Override
	public void onEquippedItemBroken(Item item, EquipmentSlot slot) {
		Player player = getOwnerPlayer();
		if (player != null) {
			GolemTriggers.BREAK.get().trigger((ServerPlayer) player);
		}
	}

	public boolean doHurtTarget(ServerLevel sl, Entity target) {
		boolean can_sweep = getMainHandItem().canPerformAction(ItemAbilities.SWORD_SWEEP);
		if (!can_sweep) {
			if (super.doHurtTarget(sl, target)) {
				ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
				stack.hurtAndBreak(1, this, EquipmentSlot.MAINHAND);
				return true;
			}
		} else {
			if (performRangedDamage(target, 0, 0)) {// trigger vanilla attack code, ignore values
				ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
				stack.hurtAndBreak(1, this, EquipmentSlot.MAINHAND);
				return true;
			}
		}
		return false;
	}

	@Override
	protected AABB getAttackBoundingBox(Entity target, double range) {
		GolemSweepEvent event = new GolemSweepEvent(this, getMainHandItem(), target, range);
		NeoForge.EVENT_BUS.post(event);
		return event.getBox();
	}

	@Override
	protected boolean performDamageTarget(Entity target, float damage, double kb) {
		if (!(level() instanceof ServerLevel sl)) return true;
		return super.doHurtTarget(sl, target);
	}

	@Override
	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (MGConfig.COMMON.strictInteract.get() && !itemstack.isEmpty())
			return InteractionResult.PASS;
		if (player.isShiftKeyDown()) {
			if (canModify(player) && level() instanceof ServerLevel sl) {
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					dropSlot(sl, slot, false);
				}
			}
			if (itemstack.isEmpty()) {
				super.mobInteractImpl(player, hand);
			}
			return InteractionResult.SUCCESS;
		}
		if (itemstack.isEmpty()) {
			return super.mobInteractImpl(player, hand);
		}
		if ((itemstack.getItem() instanceof GolemHolder) ||
				!itemstack.canFitInsideContainerItems() ||
				!canModify(player)) {
			return InteractionResult.FAIL;
		}
		GolemEquipItemEvent event = new GolemEquipItemEvent(this, itemstack);
		NeoForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			if (!(level() instanceof ServerLevel sl)) {
				return InteractionResult.SUCCESS;
			}
			for (var e : event.getSlot()) {
				if (getItemBySlot(e).isEmpty()) {
					setItemSlot(e, itemstack.split(event.getAmount()));
					int count = (int) Arrays.stream(EquipmentSlot.values()).filter(s -> !getItemBySlot(s).isEmpty()).count();
					GolemTriggers.EQUIP.get().trigger((ServerPlayer) player, count);
					return InteractionResult.SUCCESS;
				}
			}
			for (var e : event.getSlot()) {
				dropSlot(sl, e, false);
				if (hasItemInSlot(e)) continue;
				setItemSlot(e, itemstack.split(event.getAmount()));
				int count = (int) Arrays.stream(EquipmentSlot.values()).filter(s -> !getItemBySlot(s).isEmpty()).count();
				GolemTriggers.EQUIP.get().trigger((ServerPlayer) player, count);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}
		return InteractionResult.FAIL;
	}

	// ------ player equipment hurt

	@Override
	protected void hurtArmor(DamageSource damageSource, float damage) {
		this.doHurtEquipment(damageSource, damage, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
	}

	@Override
	protected void hurtHelmet(DamageSource damageSource, float damage) {
		this.doHurtEquipment(damageSource, damage, EquipmentSlot.HEAD);
	}

	protected void hurtBlockingItem(BlocksAttacks self, Level level, ItemStack item, LivingEntity user, InteractionHand hand, float damage, int fixedDamage) {
		int itemDamage = fixedDamage < 0 ? self.itemDamage().apply(damage) : fixedDamage;
		if (itemDamage > 0 && level instanceof ServerLevel serverLevel) {
			item.hurtAndBreak(itemDamage, serverLevel, user, it -> {
				user.onEquippedItemBroken(it, hand.asEquipmentSlot());
				stopUsingItem();
			});
		}
	}

	@Override
	public void checkRide(LivingEntity target) {
		if (target instanceof DogGolemEntity || target instanceof AbstractHorse) {
			startRiding(target);
		}
	}

	@Override
	public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
		var event = new GolemRidingOffsetEvent(this);
		event.setOffset(new Vec3(0, getBbHeight() * 0.345, 0));
		NeoForge.EVENT_BUS.post(event);
		return event.getOffset();
	}

	protected SoundEvent getHurtSound(DamageSource p_28872_) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.IRON_GOLEM_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.6f * super.getSoundVolume();
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.25f;
	}

}

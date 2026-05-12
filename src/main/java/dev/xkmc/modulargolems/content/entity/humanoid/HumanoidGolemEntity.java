package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.*;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Arrays;

@SerialClass
public class HumanoidGolemEntity extends SweepGolemEntity<HumanoidGolemEntity, HumanoidGolemPartType> {

	@SerialField
	public int shieldCooldown = 0;

	public HumanoidGolemEntity(EntityType<HumanoidGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.HUMANOID, type, level);
		if (!this.level().isClientSide) {
			this.groundNavigation.setCanOpenDoors(true);
		}
	}


	public InteractionHand getWeaponHand() {
		ItemStack stack = this.getMainHandItem();
		InteractionHand hand = InteractionHand.MAIN_HAND;
		if (stack.canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
			hand = InteractionHand.OFF_HAND;
		}
		return hand;
	}

	protected boolean rendering, render_trigger = false;

	@Override
	public boolean isBlocking() {
		boolean ans = shieldCooldown == 0 && isAggressive() && shieldSlot() != null;
		if (ans && rendering) {
			render_trigger = true;
		}
		return ans;
	}

	public ItemStack getUseItem() {
		ItemStack ans = super.getUseItem();
		if (rendering && render_trigger) {
			render_trigger = false;
			InteractionHand hand = shieldSlot();
			if (hand != null) return getItemInHand(hand);
		}
		return ans;
	}

	// ------ common golem behavior


	@Override
	public void onEquippedItemBroken(Item item, EquipmentSlot slot) {
		Player player = getOwner();
		if (player != null) {
			GolemTriggers.BREAK.get().trigger((ServerPlayer) player);
		}
	}

	public boolean doHurtTarget(Entity target) {
		boolean can_sweep = getMainHandItem().canPerformAction(ItemAbilities.SWORD_SWEEP);
		if (!can_sweep) {
			if (super.doHurtTarget(target)) {
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
		return super.doHurtTarget(target);
	}

	@Override
	protected InteractionResult mobInteractImpl(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (MGConfig.COMMON.strictInteract.get() && !itemstack.isEmpty())
			return InteractionResult.PASS;
		if (player.isShiftKeyDown()) {
			if (canModify(player)) {
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					dropSlot(slot, false);
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
				!itemstack.getItem().canFitInsideContainerItems() ||
				!canModify(player)) {
			return InteractionResult.FAIL;
		}
		GolemEquipEvent event = new GolemEquipEvent(this, itemstack);
		NeoForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			if (level().isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			if (hasItemInSlot(event.getSlot())) {
				dropSlot(event.getSlot(), false);
			}
			if (hasItemInSlot(event.getSlot())) {
				return InteractionResult.FAIL;
			}
			setItemSlot(event.getSlot(), itemstack.split(event.getAmount()));
			int count = (int) Arrays.stream(EquipmentSlot.values()).filter(e -> !getItemBySlot(e).isEmpty()).count();
			GolemTriggers.EQUIP.get().trigger((ServerPlayer) player, count);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

	// ------ player equipment hurt

	@Override
	protected void hurtArmor(DamageSource source, float damage) {
		if (damage <= 0.0F) return;
		damage /= 4.0F;
		if (damage < 1.0F) {
			damage = 1.0F;
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack itemstack = this.getItemBySlot(slot);
			if ((!source.is(DamageTypeTags.IS_FIRE) || !itemstack.getItem().canBeHurtBy(itemstack, source)) && itemstack.getItem() instanceof ArmorItem) {
				itemstack.hurtAndBreak((int) damage, this, slot);
			}
		}
	}

	@Nullable
	public InteractionHand shieldSlot() {
		return getItemBySlot(EquipmentSlot.MAINHAND).canPerformAction(ItemAbilities.SHIELD_BLOCK) ? InteractionHand.MAIN_HAND :
				getItemBySlot(EquipmentSlot.OFFHAND).canPerformAction(ItemAbilities.SHIELD_BLOCK) ? InteractionHand.OFF_HAND :
				null;
	}

	protected void hurtCurrentlyUsedShield(float damage) {
		InteractionHand hand = shieldSlot();
		if (hand == null) return;
		ItemStack stack = getItemInHand(hand);
		int i = damage < 3f ? 0 : 1 + Mth.floor(damage);
		GolemDamageShieldEvent event = new GolemDamageShieldEvent(this, stack, hand, damage, i);
		NeoForge.EVENT_BUS.post(event);
		i = event.getCost();
		if (i > 0) {
			stack.hurtAndBreak(i, this, LivingEntity.getSlotForHand(hand));
		}
		if (stack.isEmpty()) {
			this.setItemInHand(hand, ItemStack.EMPTY);
			this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + level().random.nextFloat() * 0.4F);
		} else {
			this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + level().random.nextFloat() * 0.4F);
		}
	}

	protected void blockUsingShield(LivingEntity source) {
		super.blockUsingShield(source);
		InteractionHand hand = shieldSlot();
		if (hand == null) return;
		ItemStack stack = getItemInHand(hand);
		boolean canDisable = source.canDisableShield() || source.getMainHandItem().canDisableShield(stack, this, source);
		int cd = 100;
		if (source.getType().is(MGTagGen.SHIELD_BREAKER)) {
			canDisable = true;
			cd *= 2;
		}
		GolemDisableShieldEvent event = new GolemDisableShieldEvent(this, stack, hand, source, canDisable);
		NeoForge.EVENT_BUS.post(event);
		if (event.shouldDisable()) {
			this.shieldCooldown = cd;
			this.level().broadcastEntityEvent(this, EntityEvent.SHIELD_DISABLED);
		}
	}

	@Override
	public void handleEntityEvent(byte event) {
		if (event == EntityEvent.SHIELD_DISABLED) {
			shieldCooldown = 100;
		}
		super.handleEntityEvent(event);
	}

	@Override
	public void tick() {
		super.tick();
		shieldCooldown = Mth.clamp(shieldCooldown - 1, 0, 100);
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

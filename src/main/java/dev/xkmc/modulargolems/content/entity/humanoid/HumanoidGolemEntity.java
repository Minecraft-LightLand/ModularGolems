package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.ShieldUsingGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.sound.MobSoundManager;
import dev.xkmc.modulargolems.content.entity.humanoid.sound.SoundManager;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.events.event.GolemRidingOffsetEvent;
import dev.xkmc.modulargolems.events.event.GolemSweepEvent;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Arrays;

@SerialClass
public class HumanoidGolemEntity extends ShieldUsingGolemEntity<HumanoidGolemEntity, HumanoidGolemPartType> {

	private static final EntityDataAccessor<String> DATA_MAID_MODEL_ID = SynchedEntityData.defineId(HumanoidGolemEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<String> DATA_SOUND_PACK_ID = SynchedEntityData.defineId(HumanoidGolemEntity.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<String> DATA_PLAYER_SKIN = SynchedEntityData.defineId(HumanoidGolemEntity.class, EntityDataSerializers.STRING);

	public String getMaidModelId() {
		return entityData.get(DATA_MAID_MODEL_ID);
	}

	public void setMaidModelId(String id) {
		entityData.set(DATA_MAID_MODEL_ID, id);
		if (!id.isEmpty()) {
			entityData.set(DATA_PLAYER_SKIN, "");
		}
	}

	public String getSoundPackId() {
		return entityData.get(DATA_SOUND_PACK_ID);
	}

	public void setSoundPackId(String id) {
		entityData.set(DATA_SOUND_PACK_ID, id);
	}

	public String getPlayerSkin() {
		return entityData.get(DATA_PLAYER_SKIN);
	}

	public void setPlayerSkin(String skin) {
		entityData.set(DATA_PLAYER_SKIN, skin);
		if (!skin.isEmpty()) {
			entityData.set(DATA_MAID_MODEL_ID, "");
			entityData.set(DATA_SOUND_PACK_ID, "");
		}
	}

	public HumanoidGolemEntity(EntityType<HumanoidGolemEntity> type, Level level) {
		super(GolemWeaponRegistry.HUMANOID, type, level);
		if (!this.level().isClientSide()) {
			this.groundNavigation.setCanOpenDoors(true);
		}
	}


	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_MAID_MODEL_ID, "");
		builder.define(DATA_SOUND_PACK_ID, "");
		builder.define(DATA_PLAYER_SKIN, "");
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput tag) {
		super.addAdditionalSaveData(tag);
		tag.putString("maidModelId", getMaidModelId());
		tag.putString("soundPackId", getSoundPackId());
		tag.putString("playerSkin", getPlayerSkin());
	}

	@Override
	public void readAdditionalSaveData(ValueInput tag) {
		super.readAdditionalSaveData(tag);
		tag.getString("maidModelId").ifPresent(this::setMaidModelId);
		tag.getString("soundPackId").ifPresent(this::setSoundPackId);
		tag.getString("playerSkin").ifPresent(this::setPlayerSkin);
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

	private boolean useMaidSounds() {
		return !getSoundPackId().isEmpty() || !getMaidModelId().isEmpty();
	}

	private SoundManager getSoundManager() {
		/*
		if (ModList.get().isLoaded(TouhouLittleMaid.MOD_ID)) {
			if (useMaidSounds()) {
				return MaidSoundManager.INS;
			}
		} */
		var playerSkin = getPlayerSkin();
		if (playerSkin.contains(":")) {
			var id = Identifier.tryParse(playerSkin);
			if (id != null) {
				if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
					var type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
					var mob = MobSoundManager.MAP.get(type);
					if (mob != null) {
						return mob;
					}
				}
			}
		}
		return SoundManager.INS;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return getSoundManager().getAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return getSoundManager().getHurtSound(source);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSoundManager().getDeathSound();
	}

	@Override
	protected float getSoundVolume() {
		return getSoundManager().getSoundVolume() * super.getSoundVolume();
	}

	@Override
	public float getVoicePitch() {
		return getSoundManager().getVoicePitch() * super.getVoicePitch();
	}

	@Override
	public void playSound(SoundEvent soundEvent, float volume, float pitch) {
		if (getSoundManager().playSound(this, soundEvent, volume, pitch)) return;
		super.playSound(soundEvent, volume, pitch);
	}

	@Override
	public int getPreviewScale() {
		return 24;
	}

}

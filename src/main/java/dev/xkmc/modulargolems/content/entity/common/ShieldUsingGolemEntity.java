package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.events.event.GolemDisableShieldEvent;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;

@SerialClass
public abstract class ShieldUsingGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends SweepGolemEntity<T, P> {

	private static final EntityDataAccessor<Boolean> MAY_BLOCK = SynchedEntityData.defineId(ShieldUsingGolemEntity.class, EntityDataSerializers.BOOLEAN);

	@SerialField
	public int shieldCooldown = 0;

	protected ShieldUsingGolemEntity(GolemWeaponRegistry<T> reg, EntityType<T> type, Level level) {
		super(reg, type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(MAY_BLOCK, true);
	}

	@Override
	public boolean isBlocking() {
		return entityData.get(MAY_BLOCK) && isAggressive() && shieldSlot() != null;
	}

	public InteractionHand getWeaponHand() {
		ItemStack stack = this.getMainHandItem();
		InteractionHand hand = InteractionHand.MAIN_HAND;
		if (stack.has(DataComponents.BLOCKS_ATTACKS)) {
			hand = InteractionHand.OFF_HAND;
		}
		return hand;
	}

	@Nullable
	public InteractionHand shieldSlot() {
		var main = getItemBySlot(EquipmentSlot.MAINHAND);
		var off = getItemBySlot(EquipmentSlot.OFFHAND);
		return main.has(DataComponents.BLOCKS_ATTACKS) ? InteractionHand.MAIN_HAND :
				off.has(DataComponents.BLOCKS_ATTACKS) ? InteractionHand.OFF_HAND :
				null;
	}

	@Override
	public @Nullable ItemStack getItemBlockingWith() {
		if (!isBlocking()) return null;
		var slot = shieldSlot();
		if (slot == null) return null;
		return getItemInHand(slot);
	}

	public boolean setupRendering = false;

	@Override
	public boolean isUsingItem() {
		return super.isUsingItem() || setupRendering && getItemBlockingWith() != null;
	}

	@Override
	public ItemStack getUseItem() {
		var ans = super.getUseItem();
		if (!ans.isEmpty()) return ans;
		ans = getItemBlockingWith();
		if (ans != null) return ans;
		return ItemStack.EMPTY;
	}

	public float applyItemBlocking(ServerLevel level, DamageSource source, float damage) {
		if (damage <= 0) return 0;
		ItemStack stack = getItemBlockingWith();
		if (stack == null)
			return 0;
		BlocksAttacks data = stack.get(DataComponents.BLOCKS_ATTACKS);
		if (data == null) return 0;
		if (source.getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0)
			return 0;
		Vec3 src = source.getSourcePosition();
		double angle;
		if (src != null) {
			Vec3 view = calculateViewVector(0, getYHeadRot());
			Vec3 diff = src.subtract(position());
			diff = new Vec3(diff.x, 0, diff.z).normalize();
			angle = Math.acos(diff.dot(view));
		} else {
			angle = (float) Math.PI;
		}
		float reduction = data.resolveBlockedDamage(source, damage, angle);
		var ev = CommonHooks.onDamageBlock(this, damageContainers.peek(), reduction, !data.bypassedBy().map(t -> t.contains(source.typeHolder())).orElse(false));
		if (!ev.getBlocked()) return 0;
		reduction = ev.getBlockedDamage();
		damageContainers.peek().setBlockedDamage(ev);
		hurtBlockingItem(data, level(), stack, this, getUsedItemHand(), reduction, ev.shieldDamage());
		if (reduction > 0 && !source.is(DamageTypeTags.IS_PROJECTILE) && source.getDirectEntity() instanceof LivingEntity le) {
			blockUsingItem(level, le);
		}
		return reduction;
	}

	protected void hurtBlockingItem(BlocksAttacks self, Level level, ItemStack item, LivingEntity user, InteractionHand hand, float damage, int fixedDamage) {
	}

	@Override
	protected void blockUsingItem(ServerLevel level, LivingEntity attacker) {
		super.blockUsingItem(level, attacker);
		InteractionHand hand = shieldSlot();
		if (hand == null) return;
		ItemStack stack = getItemInHand(hand);
		BlocksAttacks data = stack.get(DataComponents.BLOCKS_ATTACKS);
		if (data == null) return;
		int cd = (int) (attacker.getSecondsToDisableBlocking() * 20);
		if (attacker.getType().builtInRegistryHolder().is(MGTagGen.SHIELD_BREAKER)) {
			cd = Math.min(cd * 2, 200);
		}
		GolemDisableShieldEvent event = new GolemDisableShieldEvent(this, stack, hand, attacker, cd);
		NeoForge.EVENT_BUS.post(event);
		cd = event.shieldCoolDown();
		if (cd > 0) {
			data.disable(level, this, cd / 20f, stack);
			int cooldownTicks = (int) (data.disableCooldownScale() * cd);
			if (cooldownTicks > 0) {
				this.shieldCooldown = cd;
				entityData.set(MAY_BLOCK, false);
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide()) {
			shieldCooldown = Mth.clamp(shieldCooldown - 1, 0, 100);
			entityData.set(MAY_BLOCK, shieldCooldown == 0);
		}
	}

}

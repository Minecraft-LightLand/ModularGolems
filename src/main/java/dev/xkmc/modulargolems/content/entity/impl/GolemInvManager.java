package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.SlotWrapper;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.CustomDropGolemWeapon;
import dev.xkmc.modulargolems.content.item.equipments.TickEquipmentItem;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Owns the equipment state and behaviour of a single golem. The entity only supplies the backing
 * serialized fields and extra slot registrations through {@link View}.
 */
public class GolemInvManager {

	private final AbstractGolemEntity<?, ?> owner;
	private final List<ExtraSlot> extraSlots = new ArrayList<>();

	@Nullable
	private ExtraSlot bannerSlot;
	@Nullable
	private ExtraSlot arrowSlot;
	@Nullable
	private ExtraSlot arrowSlot2;

	public GolemInvManager(AbstractGolemEntity<?, ?> owner) {
		this.owner = owner;
	}

	/**
	 * All extra slots registered by this golem. Backed by the entity so slots can be registered
	 * from field initializers throughout the class hierarchy.
	 */
	public List<ExtraSlot> getExtraSlots() {
		return extraSlots;
	}

	/**
	 * Registers a slot that is not part of vanilla equipment. This handles dropping it on death,
	 * exposing it to the inventory, including it in item listings, and, when an accessor is given,
	 * keeping it synced to the client every tick. The accessor must already be defined in the
	 * entity's {@code defineSynchedData}.
	 */
	public ExtraSlot registerExtraSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		return registerExtraSlot(null, getter, setter);
	}

	public ExtraSlot registerExtraSlot(@Nullable EntityDataAccessor<ItemStack> accessor, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		ExtraSlot slot = new ExtraSlot(accessor, getter, setter);
		extraSlots.add(slot);
		return slot;
	}

	// ------ known golem slots

	public ExtraSlot registerBannerSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		return bannerSlot = registerExtraSlot(getter, setter);
	}

	/**
	 * Opts this golem into the first arrow slot. Golems that never call this keep the backing
	 * fields for compatibility, but {@link #getArrowSlot()} returns an empty wrapper.
	 */
	public ExtraSlot registerArrowSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		if (arrowSlot == null) {
			arrowSlot = registerExtraSlot(AbstractGolemEntity.ARROW_SLOT, getter, setter);
		}
		return arrowSlot;
	}

	/**
	 * Opts this golem into the second arrow slot. Only meaningful together with
	 * {@link #registerArrowSlot(Supplier, Consumer)}.
	 */
	public ExtraSlot registerArrowSlot2(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		if (arrowSlot2 == null) {
			arrowSlot2 = registerExtraSlot(AbstractGolemEntity.ARROW_SLOT_2, getter, setter);
		}
		return arrowSlot2;
	}

	private ItemWrapper wrap(@Nullable ExtraSlot slot) {
		return slot == null ? ItemWrapper.EMPTY : slot.wrapper();
	}

	public ItemWrapper getSkinSlot() {
		return ModList.get().isLoaded("curios") ? CurioCompatRegistry.getSlotWrapper(owner, "golem_skin") : ItemWrapper.EMPTY;
	}

	public ItemWrapper getRouteSlot() {
		return ModList.get().isLoaded("curios") ? CurioCompatRegistry.getSlotWrapper(owner, "golem_route") : ItemWrapper.EMPTY;
	}

	public ItemWrapper getBannerSlot() {
		return wrap(bannerSlot);
	}

	public ItemWrapper getArrowSlot() {
		return wrap(arrowSlot);
	}

	public ItemWrapper getArrowSlot2() {
		return wrap(arrowSlot2);
	}

	// ------ inventory

	public void tickExtraSlots() {
		if (owner.level().isClientSide()) return;
		for (var slot : extraSlots) {
			slot.sync();
		}
	}

	public void dropExtraSlot() {
		for (var slot : extraSlots) {
			var stack = slot.get();
			if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) {
				owner.spawnAtLocation(stack);
			}
			slot.set(ItemStack.EMPTY);
		}
	}

	public void aggregateExtraSlots(List<IItemHandlerModifiable> ans) {
		for (var slot : extraSlots) {
			ans.add(new SlotWrapper(slot::get, slot::set));
		}
	}

	public void addItemsToList(List<ItemStack> list) {
		for (var e : EquipmentSlot.values()) {
			var item = owner.getItemBySlot(e);
			if (!item.isEmpty())
				list.add(item);
		}
		for (var slot : extraSlots) {
			var item = slot.get();
			if (!item.isEmpty())
				list.add(item);
		}
	}

	public List<IItemHandlerModifiable> aggregateInventories() {
		var ans = new ArrayList<IItemHandlerModifiable>();
		ans.add(new EntityHandsInvWrapper(owner));
		ans.add(new EntityArmorInvWrapper(owner));
		aggregateExtraSlots(ans);
		MinecraftForge.EVENT_BUS.post(new GolemCollectInventoryEvent(owner, ans));
		return ans;
	}

	public void tickInventory() {
		for (var slot : EquipmentSlot.values()) {
			var stack = owner.getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof TickEquipmentItem tickItem) {
				tickItem.tick(stack, owner.level(), owner);
			} else try {
				stack.inventoryTick(owner.level(), owner, slot.ordinal(), slot == EquipmentSlot.MAINHAND);
			} catch (Exception e) {
				ModularGolems.LOGGER.warn("Golem cannot use item " + stack, e);
				owner.spawnAtLocation(stack);
				owner.setItemSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	public void dropGolemLoot(DamageSource source) {
		boolean skip = false;
		if (source.getDirectEntity() instanceof MetalGolemEntity golem &&
				golem.getMainHandItem().getItem() instanceof CustomDropGolemWeapon item) {
			skip = item.dropCustomDeathLoot(owner, golem, golem.getMainHandItem(), source);
		}
		if (!skip) {
			Map<Item, Integer> drop = new HashMap<>();
			for (GolemMaterial mat : owner.getMaterials()) {
				Item item = GolemMaterialConfig.get().getCraftIngredient(mat.id()).getItems()[0].getItem();
				drop.compute(item, (e, old) -> (old == null ? 0 : old) + 1);
			}
			drop.forEach((k, v) -> owner.spawnAtLocation(new ItemStack(k, v)));
		}
		if (!owner.isHostile()) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				dropSlot(slot, true);
			}
		}
		dropExtraSlot();
	}

	public void dropSlot(EquipmentSlot slot, boolean isDeath) {
		ItemStack itemstack = owner.getItemBySlot(slot);
		if (itemstack.isEmpty()) return;
		if (!isDeath && EnchantmentHelper.hasBindingCurse(itemstack)) return;
		if (isDeath && EnchantmentHelper.hasVanishingCurse(itemstack)) return;
		owner.spawnAtLocation(itemstack);
		owner.setItemSlot(slot, ItemStack.EMPTY);
	}

	public void hurtArmorImpl(DamageSource source, float damage) {
		if (damage <= 0.0F) return;
		damage /= 4.0F;
		if (damage < 1.0F) {
			damage = 1.0F;
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
			ItemStack stack = owner.getItemBySlot(slot);
			if (!shouldHurtArmor(source, stack)) continue;
			stack.hurtAndBreak((int) damage, owner, (entity) -> entity.broadcastBreakEvent(slot));
		}
	}

	public boolean shouldHurtArmor(DamageSource source, ItemStack stack) {
		if (!stack.isDamageableItem()) return false;
		if (source.is(DamageTypeTags.IS_FIRE) && stack.getItem().isFireResistant()) return false;
		return stack.getItem() instanceof ArmorItem || stack.is(MGTagGen.GOLEM_DAMAGEABLE);
	}

	public ItemStack getProjectile(ItemStack pShootable) {
		ItemStack ans;
		if (pShootable.getItem() instanceof ProjectileWeaponItem) {
			Predicate<ItemStack> predicate = ((ProjectileWeaponItem) pShootable.getItem()).getSupportedHeldProjectiles();
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(owner, predicate);
			ItemStack arrow = getArrowSlot().getItem();
			if (stack.isEmpty() && !arrow.isEmpty() && predicate.test(arrow)) {
				stack = arrow;
			}
			ItemStack arrow2 = getArrowSlot2().getItem();
			if (stack.isEmpty() && !arrow2.isEmpty() && predicate.test(arrow2)) {
				stack = arrow2;
			}
			ans = ForgeHooks.getProjectile(owner, pShootable, stack);
		} else {
			ans = ForgeHooks.getProjectile(owner, pShootable, ItemStack.EMPTY);
		}
		if (owner.isHostile()) ans = ans.copy();
		return ans;
	}

	/**
	 * A single extra slot. The backing fields stay authoritative on the server; the optional
	 * accessor is only read on the client so menus and overlays see the synced value.
	 */
	public final class ExtraSlot {

		@Nullable
		private final EntityDataAccessor<ItemStack> accessor;
		private final Supplier<ItemStack> getter;
		private final Consumer<ItemStack> setter;

		private ExtraSlot(@Nullable EntityDataAccessor<ItemStack> accessor, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
			this.accessor = accessor;
			this.getter = getter;
			this.setter = setter;
		}

		private boolean useClientView() {
			return accessor != null && owner.isAddedToWorld() && owner.level().isClientSide();
		}

		public ItemStack get() {
			return useClientView() ? owner.entityData().get(accessor) : getter.get();
		}

		public void set(ItemStack stack) {
			if (useClientView()) owner.entityData().set(accessor, stack);
			else setter.accept(stack);
		}

		public ItemWrapper wrapper() {
			return ItemWrapper.simple(this::get, this::set);
		}

		private void sync() {
			if (accessor == null || owner.level().isClientSide()) return;
			ItemStack stack = getter.get();
			if (!ItemStack.matches(owner.entityData().get(accessor), stack)) {
				owner.entityData().set(accessor, stack.copy());
			}
		}

	}

	/**
	 * Exposes the inventory manager through the golem entity, following the same pattern as
	 * {@link TransientStateContainer.View}.
	 */
	public interface View extends GolemImplBase {

		default GolemInvManager invManager() {
			return asGolem().invManager;
		}

		// ------ slot access

		default List<ExtraSlot> getExtraSlots() {
			return invManager().getExtraSlots();
		}

		default ItemWrapper getSkinSlot() {
			return invManager().getSkinSlot();
		}

		default ItemWrapper getRouteSlot() {
			return invManager().getRouteSlot();
		}

		default ItemWrapper getBannerSlot() {
			return invManager().getBannerSlot();
		}

		default ItemWrapper getArrowSlot() {
			return invManager().getArrowSlot();
		}

		default ItemWrapper getArrowSlot2() {
			return invManager().getArrowSlot2();
		}

		// ------ registration

		default ExtraSlot registerExtraSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
			return invManager().registerExtraSlot(getter, setter);
		}

		default ExtraSlot registerExtraSlot(@Nullable EntityDataAccessor<ItemStack> accessor, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
			return invManager().registerExtraSlot(accessor, getter, setter);
		}

		default ExtraSlot registerBannerSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
			return invManager().registerBannerSlot(getter, setter);
		}

		// ------ inventory

		default void tickExtraSlots() {
			invManager().tickExtraSlots();
		}

		default void dropExtraSlot() {
			invManager().dropExtraSlot();
		}

		default void aggregateExtraSlots(List<IItemHandlerModifiable> ans) {
			invManager().aggregateExtraSlots(ans);
		}

		default void addItemsToList(List<ItemStack> list) {
			invManager().addItemsToList(list);
		}

		default List<IItemHandlerModifiable> aggregateInventories() {
			return invManager().aggregateInventories();
		}

		default void tickInventory() {
			invManager().tickInventory();
		}

		default void dropGolemLoot(DamageSource source) {
			invManager().dropGolemLoot(source);
		}

		default void dropSlot(EquipmentSlot slot, boolean isDeath) {
			invManager().dropSlot(slot, isDeath);
		}

		default void hurtArmorImpl(DamageSource source, float damage) {
			invManager().hurtArmorImpl(source, damage);
		}

		default boolean shouldHurtArmor(DamageSource source, ItemStack stack) {
			return invManager().shouldHurtArmor(source, stack);
		}

	}

}

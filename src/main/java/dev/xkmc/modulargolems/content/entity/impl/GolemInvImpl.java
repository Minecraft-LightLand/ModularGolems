package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.CustomDropGolemWeapon;
import dev.xkmc.modulargolems.content.item.equipments.TickEquipmentItem;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GolemInvImpl extends GolemImplBase {

	default void addItemsToList(List<ItemStack> list) {
		var self = asGolem();
		for (var e : EquipmentSlot.values()) {
			var item = self.getItemBySlot(e);
			if (!item.isEmpty())
				list.add(item);
		}
	}

	default List<IItemHandlerModifiable> aggregateInventories() {
		var self = asGolem();
		var ans = new ArrayList<IItemHandlerModifiable>();
		ans.add(new EntityHandsInvWrapper(self));
		ans.add(new EntityArmorInvWrapper(self));
		self.aggregateExtraSlots(ans);
		MinecraftForge.EVENT_BUS.post(new GolemCollectInventoryEvent(self, ans));
		return ans;
	}

	default void tickInventory() {
		var self = asGolem();
		for (var slot : EquipmentSlot.values()) {
			var stack = self.getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof TickEquipmentItem tickItem) {
				tickItem.tick(stack, self.level(), self);
			} else try {
				stack.inventoryTick(self.level(), self, slot.ordinal(), slot == EquipmentSlot.MAINHAND);
			} catch (Exception e) {
				ModularGolems.LOGGER.warn("Golem cannot use item " + stack, e);
				self.spawnAtLocation(stack);
				self.setItemSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	default void dropGolemLoot(DamageSource source) {
		var self = asGolem();
		boolean skip = false;
		if (source.getDirectEntity() instanceof MetalGolemEntity golem &&
				golem.getMainHandItem().getItem() instanceof CustomDropGolemWeapon item) {
			skip = item.dropCustomDeathLoot(self, golem, golem.getMainHandItem(), source);
		}
		if (!skip) {
			Map<Item, Integer> drop = new HashMap<>();
			for (GolemMaterial mat : self.getMaterials()) {
				Item item = GolemMaterialConfig.get().getCraftIngredient(mat.id()).getItems()[0].getItem();
				drop.compute(item, (e, old) -> (old == null ? 0 : old) + 1);
			}
			drop.forEach((k, v) -> self.spawnAtLocation(new ItemStack(k, v)));
		}
		if (!self.isHostile()) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				dropSlot(slot, true);
			}
		}
		self.dropExtraSlot();
	}

	default void dropSlot(EquipmentSlot slot, boolean isDeath) {
		var self = asGolem();
		ItemStack itemstack = self.getItemBySlot(slot);
		if (itemstack.isEmpty()) return;
		if (!isDeath && EnchantmentHelper.hasBindingCurse(itemstack)) return;
		if (isDeath && EnchantmentHelper.hasVanishingCurse(itemstack)) return;
		self.spawnAtLocation(itemstack);
		self.setItemSlot(slot, ItemStack.EMPTY);
	}

	default void hurtArmorImpl(DamageSource source, float damage) {
		var self = asGolem();
		if (damage <= 0.0F) return;
		damage /= 4.0F;
		if (damage < 1.0F) {
			damage = 1.0F;
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
			ItemStack stack = self.getItemBySlot(slot);
			if (!shouldHurtArmor(source, stack)) continue;
			stack.hurtAndBreak((int) damage, self, (entity) -> entity.broadcastBreakEvent(slot));
		}
	}

	default boolean shouldHurtArmor(DamageSource source, ItemStack stack) {
		if (!stack.isDamageableItem()) return false;
		if (source.is(DamageTypeTags.IS_FIRE) && stack.getItem().isFireResistant()) return false;
		return stack.getItem() instanceof ArmorItem || stack.is(MGTagGen.GOLEM_DAMAGEABLE);
	}

}

package dev.xkmc.modulargolems.content.item.golem;

import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.content.item.data.GolemEquipments;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.events.CraftEventListeners;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.LinkedHashSet;

public record GolemEquipUtil(boolean isClient, @Nullable Level level) {

	private static boolean isGolemCurio(GolemHolder<?, ?> holder, ItemStack stack) {
		if (!ModList.get().isLoaded(CuriosApi.MODID)) return false;
		var set = CuriosApi.getEntitySlots(holder.getEntityType().type(), false).keySet();
		for (var e : set) {
			if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, e)))) {
				return true;
			}
		}
		return false;
	}

	private EquipmentSlot getSlot(ItemStack item, GolemHolder<?, ?> holder, ItemStack golem) {
		if (level == null) {
			var ans = item.getEquipmentSlot();
			if (ans == null) return EquipmentSlot.MAINHAND;
			return ans;
		}
		return holder.createDummy(golem, level).getEquipmentSlotForItem(item);
	}

	public ItemStack applyItemOnHolder(GolemHolder<?, ?> holder, ItemStack first, ItemStack second) {
		if (second.getItem() instanceof ConfigCard card) {
			var id = ConfigCard.getUUID(second);
			if (id == null) return ItemStack.EMPTY;
			GolemHolder.setGolemConfig(first, id, card.getColor().ordinal());
			return first;
		} else if (second.getItem() instanceof UpgradeItem upgrade) {
			return CraftEventListeners.appendUpgrade(first, holder, upgrade);
		} else if (GolemEquipUtil.isGolemCurio(holder, second)) {
			return equipCurioOnHolder(holder, first, second);
		} else if (holder.getEntityType() == GolemTypes.TYPE_GOLEM.get()) {
			EquipmentSlot slot;
			if (!second.is(MGTagGen.LARGE_GOLEM_WEAPONS)) {
				if (!(second.getItem() instanceof GolemEquipmentItem equipment)) return ItemStack.EMPTY;
				if (!equipment.isFor(GolemTypes.ENTITY_GOLEM.get())) return ItemStack.EMPTY;
				slot = equipment.getSlot();
			} else slot = getSlot(second, holder, first);
			return equipOnHolder(holder, first, second, slot);
		} else if (holder.getEntityType() == GolemTypes.TYPE_HUMANOID.get()) {
			EquipmentSlot slot = getSlot(second, holder, first);
			return equipOnHolder(holder, first, second, slot);
		} else return ItemStack.EMPTY;
	}

	public <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemStack equipCurioOnHolder(GolemHolder<T, P> holder, ItemStack golem, ItemStack equip) {
		if (level == null) return ItemStack.EMPTY;
		if (!ModList.get().isLoaded(CuriosApi.MODID)) return ItemStack.EMPTY;
		if (!golem.has(GolemItems.ENTITY.get())) {
			var equipments = GolemItems.EQUIPMENTS.getOrDefault(golem, new GolemEquipments()).copy();
			var golemSlots = CuriosApi.getEntitySlots(holder.getEntityType().type(), isClient).keySet();
			var itemSlots = new LinkedHashSet<>(CuriosApi.getItemStackSlots(equip, isClient).keySet());
			itemSlots.retainAll(golemSlots);
			for (var slot : itemSlots) {
				if (!equipments.equipments().containsKey(slot)) {
					equipments.equipments().put(slot, equip.copy());
					GolemItems.EQUIPMENTS.set(golem, equipments);
					return golem;
				}
			}
		}
		T entity = holder.createDummy(golem, level);
		if (entity == null) return ItemStack.EMPTY;
		var opt = CuriosApi.getCuriosInventory(entity);
		if (opt.isEmpty()) return ItemStack.EMPTY;
		equip = equip.copy();
		equip.setCount(1);
		for (var slot : CuriosApi.getItemStackSlots(equip, entity).keySet()) {
			var handler = opt.get().getStacksHandler(slot);
			if (handler.isEmpty()) continue;
			var stacks = handler.get().getStacks();
			for (int i = 0; i < stacks.getSlots(); i++) {
				if (stacks.getStackInSlot(i).isEmpty()) {
					stacks.setStackInSlot(i, equip);
					return GolemHolder.setEntity(entity);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemStack equipOnHolder(GolemHolder<T, P> holder, ItemStack golem, ItemStack equip, EquipmentSlot slot) {
		if (!golem.has(GolemItems.ENTITY.get())) {
			var equipments = GolemItems.EQUIPMENTS.getOrDefault(golem, new GolemEquipments()).copy();
			if (!equipments.equipments().containsKey(slot.name())) {
				equipments.equipments().put(slot.name(), equip.copy());
				GolemItems.EQUIPMENTS.set(golem, equipments);
				return golem;
			}
		}
		if (level == null) return ItemStack.EMPTY;
		T entity = holder.createDummy(golem, level);
		if (entity == null) return ItemStack.EMPTY;
		if (!entity.getItemBySlot(slot).isEmpty()) return ItemStack.EMPTY;
		equip = equip.copy();
		equip.setCount(1);
		entity.setItemSlot(slot, equip);
		return GolemHolder.setEntity(entity);
	}

	public static boolean giveItemToGolem(AbstractGolemEntity<?, ?> golem, ItemStack item, String ent) {
		try {
			var slot = Enum.valueOf(EquipmentSlot.class, ent);
			golem.setItemSlot(slot, item);
			return true;
		} catch (Exception ignored) {
		}
		if (!ModList.get().isLoaded(CuriosApi.MODID)) return false;
		return giveCurioToGolem(golem, item, ent);
	}

	public static boolean giveCurioToGolem(AbstractGolemEntity<?, ?> golem, ItemStack item, String ent) {
		var opt = CuriosApi.getCuriosInventory(golem);
		if (opt.isEmpty()) return false;
		var handler = opt.get().getStacksHandler(ent);
		if (handler.isEmpty()) return false;
		var stacks = handler.get().getStacks();
		for (int i = 0; i < stacks.getSlots(); i++) {
			if (stacks.getStackInSlot(i).isEmpty()) {
				stacks.setStackInSlot(i, item);
				return true;
			}
		}
		return false;
	}

	public static void addItemsToGolem(AbstractGolemEntity<?, ?> golem, ItemStack root, boolean dropExtra) {
		var equipMap = GolemItems.EQUIPMENTS.get(root);
		if (equipMap != null) {
			for (var ent : equipMap.equipments().entrySet()) {
				var item = ent.getValue();
				if (!giveItemToGolem(golem, item, ent.getKey())) {
					if (dropExtra) {
						golem.spawnAtLocation(item);
					}
				}
			}
		}
	}

}

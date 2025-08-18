package dev.xkmc.modulargolems.content.item.golem;

import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.LinkedHashSet;

public class GolemEquipUtil {

	public static boolean isGolemCurio(GolemHolder<?, ?> holder, ItemStack stack) {
		if (!ModList.get().isLoaded(Curios.MODID)) return false;
		var set = CuriosApi.getEntitySlots(holder.getEntityType().type(), false).keySet();
		return ForgeRegistries.ITEMS.tags().getReverseTag(stack.getItem())
				.map(e -> e.getTagKeys().anyMatch(t ->
						t.location().getNamespace().equals(Curios.MODID) &&
								set.contains(t.location().getPath())))
				.orElse(false);
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemStack equipCurio(GolemHolder<T, P> holder, ItemStack golem, ItemStack equip, Level level) {
		if (!ModList.get().isLoaded(Curios.MODID)) return ItemStack.EMPTY;
		var tag = golem.getTag();
		if (tag != null && !tag.contains(GolemHolder.KEY_ENTITY)) {
			var eq = tag.getCompound(GolemHolder.KEY_EQUIPMENTS);
			var golemSlots = CuriosApi.getEntitySlots(holder.getEntityType().type(), level).keySet();
			var itemSlots = new LinkedHashSet<>(CuriosApi.getItemStackSlots(equip, level).keySet());
			itemSlots.retainAll(golemSlots);
			for (var slot : itemSlots) {
				if (!eq.contains(slot)) {
					eq.put(slot, equip.save(new CompoundTag()));
					tag.put(GolemHolder.KEY_EQUIPMENTS, eq);
					return golem;
				}
			}
		}
		T entity = holder.createDummy(golem, level);
		if (entity == null) return ItemStack.EMPTY;
		var opt = CuriosApi.getCuriosInventory(entity).resolve();
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

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemStack equip(GolemHolder<T, P> holder, ItemStack golem, ItemStack equip, EquipmentSlot slot, Level level) {
		var tag = golem.getTag();
		if (tag != null && !tag.contains(GolemHolder.KEY_ENTITY)) {
			var eq = tag.getCompound(GolemHolder.KEY_EQUIPMENTS);
			if (!eq.contains(slot.name())) {
				eq.put(slot.name(), equip.save(new CompoundTag()));
				tag.put(GolemHolder.KEY_EQUIPMENTS, eq);
				return golem;
			}
		}
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
		if (!ModList.get().isLoaded(Curios.MODID)) return false;
		return giveCurioToGolem(golem, item, ent);
	}

	public static boolean giveCurioToGolem(AbstractGolemEntity<?, ?> golem, ItemStack item, String ent) {
		var opt = CuriosApi.getCuriosInventory(golem).resolve();
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

	public static void addItemsToGolem(AbstractGolemEntity<?, ?> golem, CompoundTag root, boolean dropExtra) {
		if (root.contains(GolemHolder.KEY_EQUIPMENTS, Tag.TAG_COMPOUND)) {
			var equipMap = root.getCompound(GolemHolder.KEY_EQUIPMENTS);
			for (var ent : equipMap.getAllKeys()) {
				if (!equipMap.contains(ent, Tag.TAG_COMPOUND)) continue;
				var item = ItemStack.of(equipMap.getCompound(ent));
				if (!GolemEquipUtil.giveItemToGolem(golem, item, ent)) {
					if (dropExtra) {
						golem.spawnAtLocation(item);
					}
				}
			}
		}
	}

}

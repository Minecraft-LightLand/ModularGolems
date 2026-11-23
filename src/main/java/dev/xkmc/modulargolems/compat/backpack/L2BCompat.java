package dev.xkmc.modulargolems.compat.backpack;

import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class L2BCompat {

	private static boolean hasEnder(Player player) {
		List<ItemStack> list = new ArrayList<>();
		list.add(player.getMainHandItem());
		list.add(player.getOffhandItem());
		list.add(player.getItemBySlot(EquipmentSlot.CHEST));
		for (var e : list) {
			if (e.is(BackpackItems.ENDER_BACKPACK.get())) {
				return true;
			}
		}
		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			return CurioCompatRegistry.hasItem(player, BackpackItems.ENDER_BACKPACK.get());
		}
		return false;
	}

	private static List<ItemStack> getAllContainers(Player player, boolean hasEnder) {
		List<ItemStack> list = new ArrayList<>();
		list.add(player.getOffhandItem());
		list.add(player.getItemBySlot(EquipmentSlot.CHEST));
		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			list.addAll(CurioCompatRegistry.getItems(player, e -> e.getItem() instanceof WorldChestItem));
		}
		for (int i = 0; i < 36; i++) {
			list.add(player.getInventory().getItem(i));
		}
		if (hasEnder) {
			for (int i = 0; i < 27; i++) {
				list.add(player.getEnderChestInventory().getItem(i));
			}
		}
		list.removeIf(e -> !(e.getItem() instanceof WorldChestItem));
		return list;
	}

	/**
	 * 只取出仍然存在的绑定成员：玩家背包 / 末影箱 / 空间背包里的实际物品。
	 * 已经丢到地上的不会出现在这里，因此不会被重复放出。
	 */
	@Nullable
	public static ItemStack takeBondPartner(ServerPlayer player, java.util.UUID id, java.util.UUID itemId, ItemStack main) {
		if (id == null || itemId == null) return null;
		var inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			ItemStack found = takeBond(stack, id, itemId, main);
			if (found != null) {
				inv.setItem(i, ItemStack.EMPTY);
				return found;
			}
		}
		if (hasEnder(player)) {
			var ender = player.getEnderChestInventory();
			for (int i = 0; i < ender.getContainerSize(); i++) {
				ItemStack stack = ender.getItem(i);
				ItemStack found = takeBond(stack, id, itemId, main);
				if (found != null) {
					ender.setItem(i, ItemStack.EMPTY);
					return found;
				}
			}
		}
		for (ItemStack stack : getAllContainers(player, hasEnder(player))) {
			if (!(stack.getItem() instanceof WorldChestItem chest)) continue;
			var opt = chest.getContainer(stack, player.serverLevel());
			if (opt.isEmpty()) continue;
			var cont = opt.get().container;
			for (int i = 0; i < cont.getContainerSize(); i++) {
				ItemStack found = takeBond(cont.getItem(i), id, itemId, main);
				if (found != null) {
					cont.setItem(i, ItemStack.EMPTY);
					return found;
				}
			}
		}
		return null;
	}

	@Nullable
	private static ItemStack takeBond(ItemStack stack, java.util.UUID id, java.util.UUID itemId, ItemStack main) {
		if (stack == main || stack.isEmpty()) return null;
		if (!id.equals(GolemHolder.getBondId(stack))) return null;
		if (!itemId.equals(GolemHolder.getBondItemId(stack))) return null;
		return stack.copy();
	}

	public static boolean addGolemToPlayer(ServerPlayer player, ItemStack golem, AbstractGolemEntity<?, ?> entity) {
		//TODO has upgrade
		var hasEnder = hasEnder(player);
		var config = GolemHolder.getGolemConfig(golem);
		if (config.isPresent()) {
			var list = getAllContainers(player, hasEnder);
			for (var stack : list) {
				if (!(stack.getItem() instanceof WorldChestItem chest)) continue;
				if (chest.color.getId() != config.get().getSecond()) continue;
				var owner = WorldChestItem.getOwner(stack);
				if (owner.isEmpty() || !owner.get().equals(config.get().getFirst())) continue;
				var opt = chest.getContainer(stack, player.serverLevel());
				if (opt.isEmpty()) continue;
				var cont = opt.get().container;
				if (cont.addItem(golem).isEmpty()) {
					entity.setRetrivedTo(GolemTracker.RetrieveTarget.DIMENSIONAL);
					return true;
				}
			}
		}
		if (hasEnder) {
			if (player.getEnderChestInventory().addItem(golem).isEmpty()) {
				entity.setRetrivedTo(GolemTracker.RetrieveTarget.ENDER);
				return true;
			}
		}
		return false;
	}

	public static boolean summonGolemFromPlayer(ServerPlayer player, Predicate<ItemStack> use) {
		var hasEnder = hasEnder(player);
		var list = getAllContainers(player, hasEnder);
		for (var stack : list) {
			if (!(stack.getItem() instanceof WorldChestItem chest)) continue;
			var opt = chest.getContainer(stack, player.serverLevel());
			if (opt.isEmpty()) continue;
			var cont = opt.get().container;
			for (int i = 0; i < cont.getContainerSize(); i++) {
				var item = cont.getItem(i);
				var config = GolemHolder.getGolemConfig(item);
				if (config.isPresent() && config.get().getSecond() == chest.color.getId()) {
					//TODO has upgrade
					if (use.test(item)) {
						return true;
					}
				}
			}
		}
		if (hasEnder) {
			for (int i = 0; i < 27; i++) {
				var stack = player.getEnderChestInventory().getItem(i);
				//TODO has upgrade
				if (use.test(stack)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void tickPlayer(ServerPlayer player) {
		var hasEnder = hasEnder(player);
		var list = getAllContainers(player, hasEnder);
		for (var stack : list) {
			if (!(stack.getItem() instanceof WorldChestItem chest)) continue;
			var opt = chest.getContainer(stack, player.serverLevel());
			if (opt.isEmpty()) continue;
			var cont = opt.get().container;
			for (int i = 0; i < cont.getContainerSize(); i++) {
				var item = cont.getItem(i);
				if (item.getItem() instanceof GolemHolder<?, ?> holder) {
					holder.inventoryTick(item, player.serverLevel(), player, 0, false);
				}
			}
		}
		if (hasEnder) {
			for (int i = 0; i < 27; i++) {
				var stack = player.getEnderChestInventory().getItem(i);
				if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
					holder.inventoryTick(stack, player.serverLevel(), player, 0, false);
				}
			}
		}
	}

	/**
	 * 傀儡装备未绑定所有者的空间背包时，自动绑定到傀儡主人。
	 */
	@SubscribeEvent
	public static void onGolemEquipOld(GolemEquipEvent event) {
		bindBackpack(event.getEntity(), event.getStack());
	}

	@SubscribeEvent
	public static void onGolemEquip(GolemEquipItemEvent event) {
		bindBackpack(event.getEntity(), event.getStack());
	}

	public static void bindBackpack(AbstractGolemEntity<?, ?> golem, ItemStack stack) {
		if (golem.level().isClientSide) return;
		if (!(stack.getItem() instanceof WorldChestItem chest)) return;
		if (WorldChestItem.getOwner(stack).isPresent()) return;
		UUID owner = golem.getOwnerUUID();
		if (owner == null) return;
		CompoundTag tag = stack.getOrCreateTag();
		tag.putUUID("owner_id", owner);
		var player = golem.getOwner();
		tag.putString("owner_name", player == null ? "" : player.getName().getString());
		tag.putLong("password", chest.color.getId());
	}

	@SubscribeEvent
	public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp && sp.isAlive() && sp.tickCount % 20 == 0) {
			tickPlayer(sp);
		}
	}

}

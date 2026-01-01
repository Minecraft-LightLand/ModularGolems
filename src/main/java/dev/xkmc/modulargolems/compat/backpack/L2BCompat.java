package dev.xkmc.modulargolems.compat.backpack;

import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
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

	public static boolean addGolemToPlayer(ServerPlayer player, ItemStack golem) {
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
				if (cont.addItem(golem).isEmpty())
					return true;
			}
		}
		if (hasEnder) {
			if (player.getEnderChestInventory().addItem(golem).isEmpty())
				return true;
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

}

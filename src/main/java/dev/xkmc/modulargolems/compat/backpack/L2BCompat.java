package dev.xkmc.modulargolems.compat.backpack;

import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
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
			if (e.is(LBItems.ENDER_BACKPACK.get())) {
				return true;
			}
		}
		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			return CurioCompatRegistry.hasItem(player, LBItems.ENDER_BACKPACK.get());
		}
		return false;
	}

	private static List<ItemStack> getAllContainers(Player player, boolean hasEnder) {
		List<ItemStack> list = new ArrayList<>();
		list.add(player.getOffhandItem());
		list.add(player.getItemBySlot(EquipmentSlot.CHEST));
		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			list.addAll(CurioCompatRegistry.getItems(player, e -> e.getItem() instanceof DimensionalItem));
		}
		for (int i = 0; i < 36; i++) {
			list.add(player.getInventory().getItem(i));
		}
		if (hasEnder) {
			for (int i = 0; i < 27; i++) {
				list.add(player.getEnderChestInventory().getItem(i));
			}
		}
		list.removeIf(e -> !(e.getItem() instanceof DimensionalItem));
		return list;
	}

	public static boolean addGolemToPlayer(ServerPlayer player, ItemStack golem, AbstractGolemEntity<?, ?> entity) {
		//TODO has upgrade
		var hasEnder = hasEnder(player);
		var config = GolemHolder.getGolemConfig(golem);
		if (config.isPresent()) {
			var list = getAllContainers(player, hasEnder);
			for (var stack : list) {
				if (!(stack.getItem() instanceof DimensionalItem chest)) continue;
				if (chest.color.getId() != config.get().color()) continue;
				var owner = LBItems.DC_OWNER_ID.get(stack);
				if (owner == null || !owner.equals(config.get().id())) continue;
				var opt = chest.getContainer(stack, player.serverLevel());
				if (opt.isEmpty()) continue;
				var cont = opt.get().get();
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
			if (!(stack.getItem() instanceof DimensionalItem chest)) continue;
			var opt = chest.getContainer(stack, player.serverLevel());
			if (opt.isEmpty()) continue;
			var cont = opt.get().get();
			for (int i = 0; i < cont.getContainerSize(); i++) {
				var item = cont.getItem(i);
				var config = GolemHolder.getGolemConfig(item);
				if (config.isPresent() && config.get().color() == chest.color.getId()) {
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

package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class GolemTransportHandler {

	public static void addGolemToPlayer(ServerPlayer player, ItemStack stack, AbstractGolemEntity<?, ?> golem) {
		/*if (ModList.get().isLoaded(L2Backpack.MODID)) {
			if (L2BCompat.addGolemToPlayer(player, stack, golem)) {
				return;
			}
		}TODO L2backpack*/
		if (player.addItem(stack)) {
			golem.setRetrivedTo(GolemTracker.RetrieveTarget.INVENTORY);
			return;
		}
		player.drop(stack, false);
	}

	public static void summonGolemFromPlayer(ServerPlayer player, Predicate<ItemStack> use) {
		/*if (ModList.get().isLoaded(L2Backpack.MODID)) {
			if (L2BCompat.summonGolemFromPlayer(player, use)) {
				return;
			}
		}TODO L2backpack*/
		if (use.test(player.getOffhandItem())) return;
		for (int i = 0; i < 36; i++) {
			if (use.test(player.getInventory().getItem(i)))
				return;
		}
	}

}

package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.modulargolems.compat.backpack.L2BCompat;
import dev.xkmc.modulargolems.content.item.golem.FlagTest;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.function.Predicate;

public class GolemTransportHandler {

	public static void addGolemToPlayer(ServerPlayer player, ItemStack stack) {
		if (ModList.get().isLoaded(L2Backpack.MODID)) {
			if (L2BCompat.addGolemToPlayer(player, stack)) {
				return;
			}
		}
		if (player.addItem(stack)) return;
		player.drop(stack, false);
	}

	public static void summonGolemFromPlayer(ServerPlayer player, Predicate<ItemStack> use) {
		if (ModList.get().isLoaded(L2Backpack.MODID)) {
			if (L2BCompat.summonGolemFromPlayer(player, use)) {
				return;
			}
		}
		if (use.test(player.getOffhandItem())) return;
		for (int i = 0; i < 36; i++) {
			if (use.test(player.getInventory().getItem(i)))
				return;
		}
	}

}

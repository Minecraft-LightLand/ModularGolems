package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.modulargolems.compat.backpack.L2BCompat;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class GolemTransportHandler {

	public static void addGolemToPlayer(ServerPlayer player, ItemStack stack, AbstractGolemEntity<?, ?> golem) {
		if (ModList.get().isLoaded(L2Backpack.MODID)) {
			if (L2BCompat.addGolemToPlayer(player, stack, golem)) {
				return;
			}
		}
		if (player.addItem(stack)) {
			golem.setRetrivedTo(GolemTracker.RetrieveTarget.INVENTORY);
			return;
		}
		player.drop(stack, false);
	}

	/**
	 * 犬形被一个或多个傀儡骑乘时，把犬形和所有骑手一起收进物品，并互相打上绑定标记。
	 * 空手回收和回收手杖共用；只有点的是犬形本身才会触发。
	 */
	public static boolean retrieveRiddenGroup(ServerPlayer sp, Player user, AbstractGolemEntity<?, ?> golem) {
		return retrieveRiddenGroup(sp, user, golem, false);
	}

	public static boolean retrieveRiddenGroup(ServerPlayer sp, Player user, AbstractGolemEntity<?, ?> golem, boolean toMainHand) {
		if (!(golem instanceof DogGolemEntity dog)) return false;
		List<AbstractGolemEntity<?, ?>> riders = new ArrayList<>();
		for (var e : dog.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> rider && rider != dog) {
				riders.add(rider);
			}
		}
		if (riders.isEmpty()) return false;

		List<ItemStack> items = new ArrayList<>();
		List<AbstractGolemEntity<?, ?>> owners = new ArrayList<>();
		for (var rider : riders) {
			items.add(rider.toItem(user));
			owners.add(rider);
		}
		items.add(dog.toItem(user));
		owners.add(dog);

		UUID id = UUID.randomUUID();
		for (var item : items) GolemHolder.setBondItemId(item, UUID.randomUUID());
		List<ItemStack> clean = new ArrayList<>();
		for (var item : items) {
			ItemStack copy = item.copy();
			GolemHolder.clearBound(copy);
			clean.add(copy);
		}
		for (int i = 0; i < items.size(); i++) {
			List<ItemStack> others = new ArrayList<>();
			for (int j = 0; j < clean.size(); j++) {
				if (i != j) others.add(clean.get(j).copy());
			}
			GolemHolder.setBonds(items.get(i), others, id);
		}
		for (int i = 0; i < items.size(); i++) {
			if (toMainHand && i == items.size() - 1) {
				// 空手回收：犬形本体回到主手，骑手仍按正常回收通道处理
				sp.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, items.get(i));
			} else {
				addGolemToPlayer(sp, items.get(i), owners.get(i));
			}
		}
		return true;
	}

	/**
	 * 从玩家实际持有的地方取出一件绑定成员；不在背包/末影箱/空间背包里的（例如丢在地上）返回空。
	 */
	@Nullable
	public static ItemStack takeBondItem(Player player, @Nullable UUID bondId, @Nullable UUID itemId, ItemStack main) {
		if (!(player instanceof ServerPlayer sp) || bondId == null || itemId == null) return ItemStack.EMPTY;
		if (ModList.get().isLoaded(L2Backpack.MODID)) {
			ItemStack found = L2BCompat.takeBondPartner(sp, bondId, itemId, main);
			return found == null ? ItemStack.EMPTY : found;
		}
		var inv = sp.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack == main || stack.isEmpty()) continue;
			if (!bondId.equals(GolemHolder.getBondId(stack))) continue;
			if (!itemId.equals(GolemHolder.getBondItemId(stack))) continue;
			ItemStack ans = stack.copy();
			inv.setItem(i, ItemStack.EMPTY);
			return ans;
		}
		return ItemStack.EMPTY;
	}

	public static void summonGolemFromPlayer(ServerPlayer player, Predicate<ItemStack> use) {
		// 必须先过滤再执行召唤；Predicate.and 是左先求值，写成 use.and(filter) 会先召唤再判断配置
		use = dev.xkmc.modulargolems.content.item.card.ConfigCard.getStackFilter(player).and(use);
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

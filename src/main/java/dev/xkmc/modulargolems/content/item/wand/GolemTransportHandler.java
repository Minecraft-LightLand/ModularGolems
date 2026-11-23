package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.modulargolems.compat.backpack.L2BCompat;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
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
		AbstractGolemEntity<?, ?> mount;
		if (golem instanceof DogGolemEntity dog) {
			mount = dog;
		} else if (golem instanceof MetalGolemEntity metal) {
			// 肩鞍已经取下来时不能整组回收，走普通回收
			if (!metal.hasShoulderSaddle()) return false;
			mount = metal;
		} else {
			return false;
		}
		List<AbstractGolemEntity<?, ?>> riders = new ArrayList<>();
		for (var e : mount.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> rider && rider != mount) {
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
		items.add(mount.toItem(user));
		owners.add(mount);

		// 傀儡组只存在于物品上，放出来的实体本身不带组；回收犬形时把坐骑和当前背上的
		// 所有乘客重新绑成一个组（"想继续是一组就回收犬形/手杖回收"）。
		// 这里只给"当前没有组"的物品打组，已有组的物品保持不动，避免拆散别的组。
		List<Integer> loose = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			if (GolemHolder.getBondId(items.get(i)) == null) loose.add(i);
		}
		if (loose.size() >= 2) {
			UUID id = UUID.randomUUID();
			for (int idx : loose) GolemHolder.setBondItemId(items.get(idx), UUID.randomUUID());
			List<ItemStack> clean = new ArrayList<>();
			for (int idx : loose) {
				ItemStack copy = items.get(idx).copy();
				GolemHolder.clearBound(copy);
				clean.add(copy);
			}
			for (int a = 0; a < loose.size(); a++) {
				List<ItemStack> others = new ArrayList<>();
				for (int b = 0; b < clean.size(); b++) {
					if (a != b) others.add(clean.get(b).copy());
				}
				GolemHolder.setBonds(items.get(loose.get(a)), others, id);
			}
		}
		// 注意顺序：必须先把坐骑放进主手，再放乘客。
		// 否则 addGolemToPlayer 的 player.addItem 可能把乘客塞进此时还空着的主手槽，
		// 随后 setItemSlot(MAINHAND, 坐骑) 会把那件乘客物品直接覆盖掉 → 乘客"消失"。
		int mountIdx = items.size() - 1;
		if (toMainHand) {
			sp.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, items.get(mountIdx));
			for (int i = 0; i < mountIdx; i++) {
				addGolemToPlayer(sp, items.get(i), owners.get(i));
			}
		} else {
			for (int i = 0; i < items.size(); i++) {
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

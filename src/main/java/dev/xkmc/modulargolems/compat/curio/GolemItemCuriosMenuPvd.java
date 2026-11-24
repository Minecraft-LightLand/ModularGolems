package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2tabs.compat.CuriosWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 由背包里的傀儡物品打开的饰品界面。
 * <p>
 * 物品在世界里没有实体，所以两端各自按物品数据造一个临时 dummy，饰品槽读写都落在 dummy 身上，
 * 关闭/变化时再写回物品（{@code GolemHolder.setEntity} 会把 {@code ForgeCaps} 一起存进 golem_entity，
 * 饰品正是存在那个能力里）。
 */
public record GolemItemCuriosMenuPvd(ItemStack stack, int slot, int page) implements IMenuPvd {

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	public void writeBuffer(FriendlyByteBuf buf) {
		buf.writeItem(stack);
		buf.writeInt(slot);
		buf.writeInt(page);
	}

	@Nullable
	public static AbstractGolemEntity<?, ?> createDummy(ItemStack stack, net.minecraft.world.level.Level level) {
		return stack.getItem() instanceof GolemHolder<?, ?> holder ? holder.createDummy(stack, level) : null;
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		var compat = CurioCompatRegistry.get();
		if (compat == null) return null;
		AbstractGolemEntity<?, ?> dummy = createDummy(stack, player.level());
		if (dummy == null) return null;
		return new GolemItemCuriosListMenu(compat.itemMenuType.get(), wid, inv, stack, slot, new CuriosWrapper(dummy, page));
	}

}

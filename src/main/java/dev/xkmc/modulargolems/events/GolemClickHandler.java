package dev.xkmc.modulargolems.events;

import dev.xkmc.l2screentracker.click.SlotClickHandler;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeMenu;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GolemClickHandler extends SlotClickHandler implements MenuProvider {

	public GolemClickHandler(ResourceLocation rl) {
		super(rl);
	}

	@Override
	public boolean isAllowed(ItemStack stack) {
		return stack.getItem() instanceof GolemHolder<?, ?>;
	}

	@Override
	public void handle(ServerPlayer sp, int i, int i1, int i2) {
		sp.openMenu(this);
	}

	@Override
	public Component getDisplayName() {
		return MGLangData.TAB_UPGRADES.get();
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return new GolemUpgradeMenu(GolemMiscs.UPGRADES.get(), wid, inv);
	}

}

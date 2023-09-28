package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.UUID;

public record ConfigMenuProvider(UUID id, int color, GolemConfigEditor editor) implements MenuProvider {

	public void writeBuffer(FriendlyByteBuf buf) {
		buf.writeUUID(id);
		buf.writeInt(color);
	}

	@Override
	public Component getDisplayName() {
		return editor().getDisplayName();
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return new ToggleGolemConfigMenu(GolemMiscs.CONFIG_TOGGLE.get(), wid, inv, editor());
	}
}

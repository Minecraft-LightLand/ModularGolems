package dev.xkmc.modulargolems.content.menu.target;

import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.UUID;

public record TargetConfigMenuProvider(UUID id, int color, GolemConfigEditor editor) implements IMenuPvd {

	public static TargetConfigMenuProvider fromPacket(ServerLevel level, GolemConfigEntry entry) {
		return new TargetConfigMenuProvider(entry.getID(), entry.getColor(), new GolemConfigEditor.Writable(level, entry));
	}

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
		return new TargetConfigMenu(GolemMiscs.CONFIG_TARGET.get(), wid, inv, editor.target(), editor.target());
	}
}

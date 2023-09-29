package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public record EquipmentsMenuPvd(AbstractGolemEntity<?, ?> e) implements IMenuPvd {

	@Override
	public Component getDisplayName() {
		return e.getDisplayName();
	}

	public void writeBuffer(FriendlyByteBuf buf) {
		buf.writeInt(e.getId());
	}

	public void open(ServerPlayer player) {
		NetworkHooks.openScreen(player, this, this::writeBuffer);
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return new EquipmentsMenu(GolemMiscs.EQUIPMENTS.get(), wid, inv, e);
	}
}

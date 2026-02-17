package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2tabs.compat.CuriosEventHandler;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

@SerialClass
public class OpenTableMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public TableTabType type;

	@SerialClass.SerialField
	public UUID uuid;


	@Deprecated
	public OpenTableMenuToServer() {

	}

	public OpenTableMenuToServer(TableTabType type) {
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		ItemStack stack = menu.getCarried();
		menu.setCarried(ItemStack.EMPTY);
		NetworkHooks.openScreen(player, type);
		menu = player.containerMenu;
		menu.setCarried(stack);
	}

}

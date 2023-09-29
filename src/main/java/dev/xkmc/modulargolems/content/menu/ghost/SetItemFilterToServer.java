package dev.xkmc.modulargolems.content.menu.ghost;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetItemFilterToServer extends SerialPacketBase {

	@SerialClass.SerialField
	private ItemStack stack;
	@SerialClass.SerialField
	private int slot;

	@Deprecated
	public SetItemFilterToServer() {

	}

	public SetItemFilterToServer(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer sender = context.getSender();
		if (sender == null) return;
		if (sender.containerMenu instanceof ItemConfigMenu menu) {
			menu.setSlotContent(slot, stack);
		}
	}

}

package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.menu.config.ConfigMenuProvider;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigMenuProvider;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigMenuProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;
import java.util.function.BiFunction;

@SerialClass
public class OpenConfigMenuToServer extends SerialPacketBase {

	public enum Type {
		TOGGLE(ConfigMenuProvider::fromPacket),
		ITEM(ItemConfigMenuProvider::fromPacket),
		TARGET(TargetConfigMenuProvider::fromPacket);

		private final BiFunction<ServerLevel, GolemConfigEntry, IMenuPvd> func;

		Type(BiFunction<ServerLevel, GolemConfigEntry, IMenuPvd> func) {
			this.func = func;
		}

		public IMenuPvd construct(ServerLevel level, GolemConfigEntry entry) {
			return func.apply(level, entry);
		}
	}

	@SerialClass.SerialField
	public Type type;

	@SerialClass.SerialField
	public UUID uuid;

	@SerialClass.SerialField
	public int color;

	@Deprecated
	public OpenConfigMenuToServer() {

	}

	public OpenConfigMenuToServer(UUID uuid, int color, Type type) {
		this.uuid = uuid;
		this.color = color;
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var entry = GolemConfigStorage.get(player.level()).getStorage(uuid, color);
		if (entry == null) return;
		if (!player.getUUID().equals(uuid)) return;
		IMenuPvd pvd = type.construct(player.serverLevel(), entry);
		NetworkHooks.openScreen(player, pvd, pvd::writeBuffer);
	}

}

package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.menu.config.ConfigMenuProvider;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigMenuProvider;
import dev.xkmc.modulargolems.content.menu.path.PathConfigMenuProvider;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigMenuProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiFunction;

public record OpenConfigMenuToServer(
		UUID uuid, int color, Type type
) implements SerialPacketBase<OpenConfigMenuToServer> {

	public enum Type {
		TOGGLE(ConfigMenuProvider::fromPacket),
		ITEM(ItemConfigMenuProvider::fromPacket),
		TARGET(TargetConfigMenuProvider::fromPacket),
		PATH(PathConfigMenuProvider::fromPacket);

		private final BiFunction<ServerLevel, GolemConfigEntry, IMenuPvd> func;

		Type(BiFunction<ServerLevel, GolemConfigEntry, IMenuPvd> func) {
			this.func = func;
		}

		public IMenuPvd construct(ServerLevel level, GolemConfigEntry entry) {
			return func.apply(level, entry);
		}
	}

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		var entry = GolemConfigStorage.get(player.level()).getStorage(uuid, color);
		if (entry == null) return;
		if (!player.getUUID().equals(uuid)) return;
		IMenuPvd pvd = type.construct(sp.serverLevel(), entry);
		sp.openMenu(pvd, pvd::writeBuffer);
	}

}

package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2tabs.compat.common.CuriosEventHandler;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenuPvd;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public record OpenEquipmentMenuToServer(
		UUID uuid, Type type
) implements SerialPacketBase<OpenEquipmentMenuToServer> {

	public enum Type {
		EQUIPMENT(EquipmentsMenuPvd::new),
		CURIOS(CurioCompatRegistry::create);

		private final Function<AbstractGolemEntity<?, ?>, IMenuPvd> func;

		Type(Function<AbstractGolemEntity<?, ?>, IMenuPvd> func) {
			this.func = func;
		}

		@Nullable
		public IMenuPvd construct(AbstractGolemEntity<?, ?> entry) {
			return func.apply(entry);
		}
	}


	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		var entry = sp.serverLevel().getEntity(uuid);
		if (!(entry instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!golem.canModify(player)) return;
		IMenuPvd pvd = type.construct(golem);
		if (pvd != null) {
			CuriosEventHandler.openMenuWrapped(sp, () -> player.openMenu(pvd, pvd::writeBuffer));
		}
	}

}

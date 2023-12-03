package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2tabs.compat.CuriosEventHandler;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenuPvd;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

@SerialClass
public class OpenEquipmentMenuToServer extends SerialPacketBase {

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

	@SerialClass.SerialField
	public Type type;

	@SerialClass.SerialField
	public UUID uuid;


	@Deprecated
	public OpenEquipmentMenuToServer() {

	}

	public OpenEquipmentMenuToServer(UUID uuid, Type type) {
		this.uuid = uuid;
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var entry = player.serverLevel().getEntity(uuid);
		if (!(entry instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!entry.isAlliedTo(player)) return;
		IMenuPvd pvd = type.construct(golem);
		if (pvd != null) {
			CuriosEventHandler.openMenuWrapped(player, () -> NetworkHooks.openScreen(player, pvd, pvd::writeBuffer));
		}
	}

}

package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2tabs.compat.BaseCuriosListMenu;
import dev.xkmc.l2tabs.compat.CuriosWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class GolemCuriosListMenu extends BaseCuriosListMenu<GolemCuriosListMenu> {

	@Nullable
	public static GolemCuriosListMenu fromNetwork(MenuType<GolemCuriosListMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		int id = buf.readInt();
		int page = buf.readInt();
		ClientLevel level = Proxy.getClientWorld();
		assert level != null;
		Entity entity = level.getEntity(id);
		if (entity instanceof AbstractGolemEntity<?, ?> golem)
			return new GolemCuriosListMenu(type, wid, plInv, new CuriosWrapper(golem, page));
		return null;
	}

	protected GolemCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, CuriosWrapper curios) {
		super(type, wid, plInv, curios);
	}

	@Override
	public void switchPage(ServerPlayer serverPlayer, int i) {
		if (curios.entity instanceof AbstractGolemEntity<?, ?> golem) {
			var pvd = new GolemCuriosMenuPvd(golem, i);
			NetworkHooks.openScreen(serverPlayer, pvd, pvd::writeBuffer);
		}
	}

}

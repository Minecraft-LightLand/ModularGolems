package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2tabs.compat.BaseCuriosListMenu;
import dev.xkmc.l2tabs.compat.CuriosEventHandler;
import dev.xkmc.l2tabs.compat.CuriosWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
		if (entity instanceof LivingEntity le)
			return new GolemCuriosListMenu(type, wid, plInv, new CuriosWrapper(le, page));
		return null;
	}

	protected GolemCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, CuriosWrapper curios) {
		super(type, wid, plInv, curios);
	}

	@Override
	public void switchPage(ServerPlayer player, int i) {
		if (curios.entity.isAlive()) {
			var pvd = new GolemCuriosMenuPvd(curios.entity, i);
			CuriosEventHandler.openMenuWrapped(player, () -> NetworkHooks.openScreen(player, pvd, pvd::writeBuffer));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if (!player.isAlive()) return false;
		if (curios.entity instanceof AbstractGolemEntity<?, ?> golem)
			return !golem.isRemoved() && golem.getGuardedDataImpl() > 0;
		return curios.entity.isAlive();
	}

}

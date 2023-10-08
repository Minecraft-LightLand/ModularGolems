package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class GolemCuriosListMenu extends BaseContainerMenu<GolemCuriosListMenu> {

	public static final SpriteManager[] MANAGER;

	static {
		MANAGER = new SpriteManager[4];
		for (int i = 0; i < 4; i++) {
			MANAGER[i] = new SpriteManager(L2Tabs.MODID, "curios_" + (i + 3));
		}
	}

	private static SpriteManager getManager(int size) {
		int n = (size + 8) / 9;
		return MANAGER[Math.min(Math.max(n - 3, 0), 3)];
	}

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

	protected CuriosWrapper curios;

	protected GolemCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, CuriosWrapper curios) {
		super(type, wid, plInv, getManager(curios.getSize()), e -> new BaseContainer<>(curios.getSize(), e), false);
		addCurioSlot("grid", curios);
		this.curios = curios;
	}

	protected void addCurioSlot(String name, CuriosWrapper curios) {
		int current = added;
		sprite.get().getSlot(name, (x, y) -> {
			int i = added - current;
			if (i >= curios.getSize()) return null;
			var ans = curios.get(i).toSlot(x, y);
			added++;
			return ans;
		}, this::addSlot);
	}

}

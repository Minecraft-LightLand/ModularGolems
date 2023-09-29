package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ToggleGolemConfigMenu extends BaseContainerMenu<ToggleGolemConfigMenu> {

	protected static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "config_toggle");

	public static ToggleGolemConfigMenu fromNetwork(MenuType<ToggleGolemConfigMenu> type, int wid, Inventory inv, FriendlyByteBuf buf) {
		var id = buf.readUUID();
		int color = buf.readInt();
		var level = Proxy.getClientWorld();
		var editor = new GolemConfigEditor.Readable(level, id, color);
		return new ToggleGolemConfigMenu(type, wid, inv, editor);
	}

	final GolemConfigEditor editor;

	protected ToggleGolemConfigMenu(MenuType<?> type, int wid, Inventory plInv, GolemConfigEditor editor) {
		super(type, wid, plInv, MANAGER, menu -> new BaseContainer<>(1, menu), true);
		this.editor = editor;
		this.addSlot("hand", e -> e.getItem() instanceof GolemHolder<?, ?>);
	}

	@Override
	protected void securedServerSlotChange(Container cont) {
		GolemHolder.setGolemConfig(cont.getItem(0), editor.entry().getID(), editor.entry().getColor());
	}

}

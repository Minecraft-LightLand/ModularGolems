package dev.xkmc.modulargolems.content.menu.target;

import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;
import dev.xkmc.modulargolems.content.capability.TargetFilterConfig;
import dev.xkmc.modulargolems.content.capability.TargetFilterEditor;
import dev.xkmc.modulargolems.content.capability.TargetFilterLine;
import dev.xkmc.modulargolems.content.item.card.TargetFilterCard;
import dev.xkmc.modulargolems.content.menu.ghost.GhostItemMenu;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class TargetConfigMenu extends GhostItemMenu {

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "config_target");

	public static TargetConfigMenu fromNetwork(MenuType<TargetConfigMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		var id = buf.readUUID();
		int color = buf.readInt();
		return new TargetConfigMenu(type, wid, plInv, new SimpleContainer(TargetFilterConfig.LINE * 2),
				GolemConfigEditor.readable(id, color).target());
	}

	protected final TargetFilterEditor editor;

	protected TargetConfigMenu(MenuType<?> type, int wid, Inventory plInv, Container container, TargetFilterEditor editor) {
		super(type, wid, plInv, MANAGER, container);
		this.editor = editor;
		addSlot("hostile", e -> e.getItem() instanceof TargetFilterCard);
		addSlot("friendly", e -> e.getItem() instanceof TargetFilterCard);
	}

	protected TargetFilterLine getContainer(int slot) {
		return slot < TargetFilterConfig.LINE ? getConfig().targetHostile() : getConfig().targetFriendly();
	}

	protected TargetFilterEditor getConfig() {
		return editor;
	}

}

package dev.xkmc.modulargolems.content.menu.table;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2menustacker.click.quickaccess.DummyHandler;
import dev.xkmc.l2menustacker.click.quickaccess.SimpleMenuAction;
import dev.xkmc.l2menustacker.compat.arclight.AnvilMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.GrindstoneMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.SmithingMenuArclight;
import dev.xkmc.l2menustacker.compat.arclight.StonecutterMenuArclight;
import dev.xkmc.l2tabs.tabs.core.ITabType;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public enum TableTabType implements MenuProvider, ITabType<TableGroup> {
	DISINTEGRATE(MGLangData.TAB_DISINTEGRATE, GolemMiscs.DISINTEGRATE),
	UPGRADE(MGLangData.TAB_UPGRADES, GolemMiscs.UPGRADES),
	CRAFT(MenuType.CRAFTING, Items.CRAFTING_TABLE, CraftingMenu::new, "container.crafting"),
	STONECUTTER(MenuType.STONECUTTER, Items.STONECUTTER, StonecutterMenuArclight::new, "container.stonecutter"),
	ANVIL(MenuType.ANVIL, Items.ANVIL, AnvilMenuArclight::new, "container.repair"),
	SMITHING(MenuType.SMITHING, Items.SMITHING_TABLE, SmithingMenuArclight::new, "container.upgrade"),
	GRINDSTONE(MenuType.GRINDSTONE, Items.GRINDSTONE, GrindstoneMenuArclight::new, "container.grindstone_title");

	private final Component lang;
	private final MenuFactory factory;

	public final MenuType<?> menu;

	private TabToken<TableGroup, ?> tab;

	TableTabType(MGLangData lang, MenuEntry<?> factory) {
		this.lang = lang.get();
		this.factory = factory::create;
		menu = factory.get();
	}

	TableTabType(MenuType<?> type, Item item, SimpleMenuAction.MenuFactory factory, String id) {
		lang = Component.translatable(id);
		this.factory = (wid, inv) -> factory.create(wid, inv, new DummyHandler(inv.player));
		menu = type;
	}

	@Override
	public Component getDisplayName() {
		return lang;
	}

	@Override
	public Component getTitle() {
		return lang;
	}

	@Override
	public void reverseMap(TabToken<TableGroup, ?> tabToken) {
		tab = tabToken;
	}

	@Override
	public @Nullable TabToken<TableGroup, ?> getMapped() {
		return tab;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return factory.create(id, inv);
	}


	public interface MenuFactory {
		AbstractContainerMenu create(int wid, Inventory inv);
	}

}

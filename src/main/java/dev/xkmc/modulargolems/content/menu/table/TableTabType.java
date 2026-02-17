package dev.xkmc.modulargolems.content.menu.table;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.createmod.catnip.annotations.ClientOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum TableTabType implements MenuProvider {
	UPGRADE(MGLangData.TAB_UPGRADES, GolemMiscs.UPGRADES),
	DISINTEGRATE(MGLangData.TAB_DISINTEGRATE, GolemMiscs.DISINTEGRATE),
	;

	private final MGLangData lang;
	private final MenuEntry<?> factory;

	TableTabType(MGLangData lang, MenuEntry<?> factory) {
		this.lang = lang;
		this.factory = factory;
	}

	@Override
	public Component getDisplayName() {
		return lang.get();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return factory.create(id, inv);
	}

}

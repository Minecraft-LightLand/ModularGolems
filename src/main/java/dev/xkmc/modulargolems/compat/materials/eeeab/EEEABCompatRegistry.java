package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class EEEABCompatRegistry {

	public static final ItemEntry<Item> REALM_CUBE;

	static {
		REALM_CUBE = GolemItems.item(EEEABDispatch.MODID, "realm_cube", Item::new);
	}

	public static void register() {
	}

}

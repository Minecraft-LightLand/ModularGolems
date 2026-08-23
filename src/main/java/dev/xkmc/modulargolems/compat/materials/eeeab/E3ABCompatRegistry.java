package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class E3ABCompatRegistry {

	public static final ItemEntry<Item> CLOUD_CUBE;

	static {

		CLOUD_CUBE = GolemItems.item(E3ABDispatch.MODID, "cloud_cube", Item::new);
	}

	public static void register() {
	}

}

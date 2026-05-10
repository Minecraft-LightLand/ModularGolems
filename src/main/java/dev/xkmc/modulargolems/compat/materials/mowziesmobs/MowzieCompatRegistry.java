package dev.xkmc.modulargolems.compat.materials.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class MowzieCompatRegistry {

	public static final Val<SlamModifier> AXE_SLAM;
	public static final ItemEntry<SimpleUpgradeItem> UPGRADE_SLAM;
	public static final ItemEntry<Item> WROUGHTNAUT_INGOT;

	public static final TagKey<Item> WROUGHTNAUT_ITEMS = ItemTags.create(ModularGolems.loc("wroughtnaut_items"));

	static {
		WROUGHTNAUT_INGOT = GolemItems.item(MowzieDispatch.MODID, "wroughtnaut_ingot", Item::new);
		AXE_SLAM = reg("wroughtnaut", () -> new SlamModifier(StatFilterType.HEAD, 1),
				"Wroughtnaut Slamming", "Perform falling attack and create shockwave");
		UPGRADE_SLAM = regModUpgrade("wroughtnaut", () -> AXE_SLAM, MowzieDispatch.MODID)
				.lang("Wroughtnaut Slamming Upgrade").register();
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd ->
				pvd.addTag(WROUGHTNAUT_ITEMS)
						.addOptional(ItemHandler.WROUGHT_AXE.getId())
						.addOptional(ItemHandler.WROUGHT_HELMET.getId())
		);
	}
}

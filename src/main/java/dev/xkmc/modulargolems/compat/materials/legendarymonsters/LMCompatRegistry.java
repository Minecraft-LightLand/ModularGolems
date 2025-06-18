package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LMCompatRegistry {

	public static final RegistryEntry<AncientAnchorModifier> ANCHOR;
	public static final RegistryEntry<ThunderAttackModifier> THUNDER;
	public static final RegistryEntry<RootPercAttackModifier> PERC;
	public static final ItemEntry<Item> CLOUD_CUBE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_THUNDER;

	static {
		ANCHOR = reg("ancient_anchor", () -> new AncientAnchorModifier(StatFilterType.MASS, 4),
				"Ancient Anchor", "Deal fall attack, cause shockwaves, and stun targets");

		THUNDER = reg("thunderstorm", () -> new ThunderAttackModifier(StatFilterType.MASS, 4),
				"Thunderstorm", "Summon thunderstorm to attack multiple targets. When attacked, summon electric bursts around the golem.");

		PERC = reg("cloud_forming", RootPercAttackModifier::new,
				"Cloud Forming", "Deal more damage to targets with higher health");

		UPGRADE_THUNDER = regModUpgrade("thunderstorm", () -> THUNDER, LMDispatch.MODID)
				.lang("Thunderstorm Upgrade").register();

		CLOUD_CUBE = GolemItems.item(LMDispatch.MODID, "cloud_cube", Item::new);
	}

	public static void register() {
	}

}

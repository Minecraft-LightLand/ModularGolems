package dev.xkmc.modulargolems.compat.materials.l2complements;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.ConduitModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.EnderTeleportModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.FreezingModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.SoulFlameModifier;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LCCompatRegistry {

	public static final RegistryEntry<ConduitModifier> CONDUIT;
	public static final RegistryEntry<FreezingModifier> FREEZE;
	public static final RegistryEntry<SoulFlameModifier> FLAME;
	public static final RegistryEntry<EnderTeleportModifier> TELEPORT;

	public static final ItemEntry<UpgradeItem> FORCE_FIELD, FREEZE_UP, FLAME_UP, TELEPORT_UP, ATK_UP, SPEED_UP;

	static {
		CONDUIT = reg("conduit", ConduitModifier::new, "When in water: Reduce damage taken to %s%%. Every %s seconds, deal %s conduit damage to target in water/rain remotely. Boost following stats:");
		FREEZE = reg("freezing", FreezingModifier::new, "Get Ice Blade and Ice Thorn enchantment effects. Immune to freezing damage.");
		FLAME = reg("soul_flame", SoulFlameModifier::new, "Get Soul Flame Blade and Soul Flame Thorn enchantment effects. Immune to soul flame damage.");
		TELEPORT = reg("teleport", EnderTeleportModifier::new, "Teleport randomly to avoid attack, and teleport toward target when attacking.");

		FORCE_FIELD = regModUpgrade("force_field", () -> GolemModifiers.PROJECTILE_REJECT, LCDispatch.MODID).lang("Wither Armor Upgrade").register();
		FREEZE_UP = regModUpgrade("freezing", () -> FREEZE, LCDispatch.MODID).lang("Freezing Upgrade").register();
		FLAME_UP = regModUpgrade("soul_flame", () -> FLAME, LCDispatch.MODID).lang("Soul Flame Upgrade").register();
		TELEPORT_UP = regModUpgrade("teleport", () -> TELEPORT, LCDispatch.MODID).lang("Teleport Upgrade").register();
		ATK_UP = regModUpgrade("attack_high", () -> GolemModifiers.DAMAGE, 5, true, LCDispatch.MODID).lang("Attack Upgrade V").register();
		SPEED_UP = regModUpgrade("speed_high", () -> GolemModifiers.SPEED, 5, true, LCDispatch.MODID).lang("Speed Upgrade V").register();
	}

	public static void register() {

	}

}

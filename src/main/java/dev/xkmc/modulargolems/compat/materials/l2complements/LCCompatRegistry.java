package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.ConduitModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.EnderTeleportModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.FreezingModifier;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.SoulFlameModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.PotionAttackModifier;
import dev.xkmc.modulargolems.content.modifier.base.PotionDefenseModifier;
import dev.xkmc.modulargolems.content.modifier.base.TargetBonusModifier;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MobType;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LCCompatRegistry {

	public static final RegistryEntry<ConduitModifier> CONDUIT;
	public static final RegistryEntry<FreezingModifier> FREEZE;
	public static final RegistryEntry<SoulFlameModifier> FLAME;
	public static final RegistryEntry<EnderTeleportModifier> TELEPORT;
	public static final RegistryEntry<PotionAttackModifier> CURSE, INCARCERATE;
	public static final RegistryEntry<PotionDefenseModifier> CLEANSE;
	public static final RegistryEntry<TargetBonusModifier> POSEIDITE, TOTEMIC_GOLD;

	public static final ItemEntry<UpgradeItem> FORCE_FIELD, FREEZE_UP, FLAME_UP, TELEPORT_UP, ATK_UP, SPEED_UP,
			UPGRADE_CURSE, UPGRADE_INCARCERATE, UPGRADE_CLEANSE;

	static {
		CONDUIT = reg("conduit", ConduitModifier::new, "When in water: Reduce damage taken to %s%%. Every %s seconds, deal %s conduit damage to target in water/rain remotely. Boost following stats:");
		FREEZE = reg("freezing", FreezingModifier::new, "Get Ice Blade and Ice Thorn enchantment effects. Immune to freezing damage.");
		FLAME = reg("soul_flame", SoulFlameModifier::new, "Get Soul Flame Blade and Soul Flame Thorn enchantment effects. Immune to soul flame damage.");
		TELEPORT = reg("teleport", EnderTeleportModifier::new, "Teleport randomly to avoid attack, and teleport toward target when attacking.");

		CURSE = reg("curse", () -> new PotionAttackModifier(StatFilterType.MASS, 3,
				i -> new MobEffectInstance(LCEffects.CURSE.get(), 60 * i)), "Potion Upgrade: Curse",null);
		INCARCERATE = reg("incarcerate", () -> new PotionAttackModifier(StatFilterType.MASS, 3,
				i -> new MobEffectInstance(LCEffects.STONE_CAGE.get(), 20 * i)), "Potion Upgrade: Incarceration",null);

		POSEIDITE = reg("poseidite", () -> new TargetBonusModifier(e -> e.isSensitiveToWater() || e.getMobType() == MobType.WATER),
				"Deal %s%% more damage to mobs sensitive to water or water based mobs");
		TOTEMIC_GOLD = reg("totemic_gold", () -> new TargetBonusModifier(e -> e.getMobType() == MobType.UNDEAD),
				"Deal %s%% more damage to undead mobs");
		CLEANSE = reg("cleanse", () -> new PotionDefenseModifier(1, LCEffects.CLEANSE::get), "Potion Upgrade: Cleanse",null);

		FORCE_FIELD = regModUpgrade("force_field", () -> GolemModifiers.PROJECTILE_REJECT, LCDispatch.MODID).lang("Wither Armor Upgrade").register();
		FREEZE_UP = regModUpgrade("freezing", () -> FREEZE, LCDispatch.MODID).lang("Freezing Upgrade").register();
		FLAME_UP = regModUpgrade("soul_flame", () -> FLAME, LCDispatch.MODID).lang("Soul Flame Upgrade").register();
		TELEPORT_UP = regModUpgrade("teleport", () -> TELEPORT, LCDispatch.MODID).lang("Teleport Upgrade").register();
		ATK_UP = regModUpgrade("attack_high", () -> GolemModifiers.DAMAGE, 5, true, LCDispatch.MODID).lang("Attack Upgrade V").register();
		SPEED_UP = regModUpgrade("speed_high", () -> GolemModifiers.SPEED, 5, true, LCDispatch.MODID).lang("Speed Upgrade V").register();
		UPGRADE_CURSE = regModUpgrade("curse", () -> CURSE, LCDispatch.MODID).lang("Potion Upgrade: Curse").register();
		UPGRADE_INCARCERATE = regModUpgrade("incarcerate", () -> INCARCERATE, LCDispatch.MODID).lang("Potion Upgrade: Incarceration").register();
		UPGRADE_CLEANSE = regModUpgrade("cleanse", () -> CLEANSE, LCDispatch.MODID).lang("Potion Upgrade: Cleanse").register();
	}

	public static void register() {

	}

}

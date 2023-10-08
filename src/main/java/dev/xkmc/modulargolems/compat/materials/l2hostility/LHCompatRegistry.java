package dev.xkmc.modulargolems.compat.materials.l2hostility;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.PotionDefenseModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

public class LHCompatRegistry {

	public static final RegistryEntry<HostilityModifier> LH_CORE;

	public static final RegistryEntry<AttributeGolemModifier> LH_TANK, LH_SPEED;
	public static final RegistryEntry<PotionDefenseModifier> LH_PROTECTION;
	public static final RegistryEntry<RegenModifier> LH_REGEN;
	public static final RegistryEntry<ReflectiveModifier> LH_REFLECTIVE;

	public static final ItemEntry<SimpleUpgradeItem> CORE, TANK, SPEED, PROTECTION, REGEN, REFLECTIVE;

	public static final TagKey<Item> HOSTILITY_UPGRADE = ItemTags.create(new ResourceLocation(ModularGolems.MODID, "hostility_upgrades"));

	static {
		LH_CORE = GolemModifiers.reg("hostility_core", () -> new HostilityModifier(StatFilterType.HEALTH, 1), "Hostility Core",
				"All other hostility upgrades don't consume upgrade slots");

		LH_TANK = GolemModifiers.reg("hostility_tank", () -> new AttributeGolemModifier(5,
						new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_HEALTH_P, LHConfig.COMMON.tankHealth::get),
						new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ARMOR, LHConfig.COMMON.tankArmor::get),
						new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_TOUGH, LHConfig.COMMON.tankTough::get)),
				"Hostility Upgrade: Tanky", null);

		LH_SPEED = GolemModifiers.reg("hostility_speed", () -> new AttributeGolemModifier(5,
						new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_SPEED, LHConfig.COMMON.speedy::get)),
				"Hostility Upgrade: Speedy", null);

		LH_PROTECTION = GolemModifiers.reg("hostility_protection", () -> new PotionDefenseModifier(4, () -> MobEffects.DAMAGE_RESISTANCE),
				"Hostility Upgrade: Protection", null);

		LH_REGEN = GolemModifiers.reg("hostility_regen", () -> new RegenModifier(StatFilterType.HEALTH, 5),
				"Hostility Upgrade: Regeneration", null);

		LH_REFLECTIVE = GolemModifiers.reg("hostility_reflect", () -> new ReflectiveModifier(StatFilterType.HEALTH, 5),
				"Hostility Upgrade: Reflective", null);

		CORE = GolemItems.regModUpgrade("hostility_core", () -> LH_CORE, L2Hostility.MODID).lang("Hostility Core").register();
		TANK = GolemItems.regModUpgrade("hostility_tank", () -> LH_TANK, L2Hostility.MODID).lang("Hostility Upgrade: Tanky").register();
		SPEED = GolemItems.regModUpgrade("hostility_speed", () -> LH_TANK, L2Hostility.MODID).lang("Hostility Upgrade: Speedy").register();
		PROTECTION = GolemItems.regModUpgrade("hostility_protection", () -> LH_TANK, L2Hostility.MODID).lang("Hostility Upgrade: Protection").register();
		REGEN = GolemItems.regModUpgrade("hostility_regen", () -> LH_TANK, L2Hostility.MODID).lang("Hostility Upgrade: Regeneration").register();
		REFLECTIVE = GolemItems.regModUpgrade("hostility_reflect", () -> LH_TANK, L2Hostility.MODID).lang("Hostility Upgrade: Reflective").register();
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd -> pvd.addTag(HOSTILITY_UPGRADE)
				.addOptional(TANK.getId())
				.addOptional(SPEED.getId())
				.addOptional(PROTECTION.getId())
				.addOptional(REGEN.getId())
				.addOptional(REFLECTIVE.getId()));
	}

}

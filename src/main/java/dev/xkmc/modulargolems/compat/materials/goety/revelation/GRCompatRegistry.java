package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.goety.title.*;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

import static dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry.REV_ARMOR;
import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class GRCompatRegistry {

	public static final ItemEntry<ApollyonArmorItem> APOCALYPTIUM_HELMET, APOCALYPTIUM_CHESTPLATE, APOCALYPTIUM_SHINGUARD, APOCALYPTIUM_BOOTS;
	public static final ItemEntry<ApollyonSword> STELLAR_APOCALYPSE;
	public static final RegistryEntry<CooldownBypassModifier> CD_BYPASS;
	public static final RegistryEntry<ReviveModifier> REVIVE;
	public static final RegistryEntry<FastBowModifier> BOW;
	public static final RegistryEntry<CurseModifier> CURSE;
	public static final RegistryEntry<FastSkillModifier> FAST;

	public static final ItemEntry<SimpleUpgradeItem> UPGRADE_CD, UPGRADE_BOW, UPGRADE_CURSE, UPGRADE_FAST;

	static {
		APOCALYPTIUM_HELMET = REGISTRATE.item("apocalyptium_helmet", p -> new ApollyonArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 16, 10, GRArmorPaths.APOLLYON_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, grLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		APOCALYPTIUM_CHESTPLATE = REGISTRATE.item("apocalyptium_chestplate", p -> new ApollyonArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 20, 10, GRArmorPaths.APOLLYON_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, grLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		APOCALYPTIUM_SHINGUARD = REGISTRATE.item("apocalyptium_shinguard", p -> new ApollyonArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 12, 10, GRArmorPaths.APOLLYON_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, grLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		APOCALYPTIUM_BOOTS = REGISTRATE.item("apocalyptium_boots", p -> new ApollyonArmorItem(p.stacksTo(1),
						ArmorItem.Type.BOOTS, 12, 10, GRArmorPaths.APOLLYON_BOOTS))
				.model((ctx, pvd) -> pvd.generated(ctx, grLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		STELLAR_APOCALYPSE = ApollyonSword.buildItem("stellar_apocalypse");

		CD_BYPASS = reg("the_abhorrent", CooldownBypassModifier::new, "The Abhorrent",
				"Reduce target invulnerability frames on hit");
		BOW = reg("the_terrible", FastBowModifier::new, "The Terrible",
				"Golem draws bow faster. Shoots %s arrows each toward at most %s targets");
		CURSE = reg("the_profane", CurseModifier::new, "The Profane",
				"Golem remove %s random positive effects from target on hit");
		FAST = reg("the_atrocious", FastSkillModifier::new, "The Atrocious",
				"Reduce Apostle modifier skill cooldown");
		REVIVE = reg("the_risen", ReviveModifier::new, "The Risen",
				"Golem drops holder item with 0 health on death. Golem holder item can still heal when it has no health");

		UPGRADE_CD = regModUpgrade("the_abhorrent", () -> CD_BYPASS, GRDispatch.MODID)
				.lang("Apostle Title: The Abhorrent").register();
		UPGRADE_BOW = regModUpgrade("the_terrible", () -> BOW, GRDispatch.MODID)
				.lang("Apostle Title: The Terrible").register();
		UPGRADE_CURSE = regModUpgrade("the_profane", () -> CURSE, GRDispatch.MODID)
				.lang("Apostle Title: The Profane").register();
		UPGRADE_FAST = regModUpgrade("the_atrocious", () -> FAST, GRDispatch.MODID)
				.lang("Apostle Title: The Atrocious").register();
	}

	public static ResourceLocation grLoc(String id) {
		return new ResourceLocation(GRDispatch.MODID, id);
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd -> pvd.addTag(REV_ARMOR)
				.addOptional(APOCALYPTIUM_HELMET.getId())
				.addOptional(APOCALYPTIUM_CHESTPLATE.getId())
				.addOptional(APOCALYPTIUM_SHINGUARD.getId())
				.addOptional(APOCALYPTIUM_BOOTS.getId())
		);
		MGTagGen.OPTIONAL_ITEM.add(pvd -> pvd.addTag(MGTagGen.SHIELD_BREAKER_WEAPONS)
				.addOptional(STELLAR_APOCALYPSE.getId()));
	}

}

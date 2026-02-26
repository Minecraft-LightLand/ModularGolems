package dev.xkmc.modulargolems.compat.materials.twilightforest;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.equipments.*;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifiers.CarminiteModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifiers.FieryModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifiers.TFDamageModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifiers.TFHealingModifier;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.THORN;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class TFCompatRegistry {

	public static final ItemEntry<IronwoodArmorItem> IRONWOOD_HELMET, IRONWOOD_CHESTPLATE, IRONWOOD_SHINGUARD, IRONWOOD_BOOTS;
	public static final ItemEntry<KnightmetalArmorItem> KNIGHTMETAL_HELMET, KNIGHTMETAL_CHESTPLATE, KNIGHTMETAL_SHINGUARD, KNIGHTMETAL_BOOTS;
	public static final ItemEntry<FieryArmorItem> FIERY_HELMET, FIERY_CHESTPLATE, FIERY_SHINGUARD, FIERY_BOOTS;
	public static final ItemEntry<NagaArmorItem> NAGA_CHESTPLATE, NAGA_SHINGUARD;
	public static final ItemEntry<MetalGolemWeaponItem>[][] TF_GOLEM_WEAPON;
	public static final RegistryEntry<FieryModifier> FIERY;
	public static final RegistryEntry<TFDamageModifier> TF_DAMAGE;
	public static final RegistryEntry<TFHealingModifier> TF_HEALING;
	public static final RegistryEntry<CarminiteModifier> CARMINITE;
	public static final RegistryEntry<AttributeGolemModifier> NAGA;

	public static final RegistryEntry<SimpleUpgradeItem> UP_CARMINITE, UP_STEELEAF, UP_FIERY, UP_IRONWOOD, UP_KNIGHTMETAL, UP_NAGA;

	static {
		IRONWOOD_HELMET = REGISTRATE.item("ironwood_helmet", p -> new IronwoodArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 8, 4, TFArmorPaths.IRONWOOD_HELMETS)) // 护甲值和韧性请根据设计调整
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName()))) // 注意：cataLoc 应替换为您第二个文件中的资源定位方法，例如 modLoc
				.defaultLang().register();
		IRONWOOD_CHESTPLATE = REGISTRATE.item("ironwood_chestplate", p -> new IronwoodArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 10, 4, TFArmorPaths.IRONWOOD_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		IRONWOOD_SHINGUARD = REGISTRATE.item("ironwood_shinguard", p -> new IronwoodArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 6, 4, TFArmorPaths.IRONWOOD_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		IRONWOOD_BOOTS = REGISTRATE.item("ironwood_boots", p -> new IronwoodArmorItem(p.stacksTo(1),
						ArmorItem.Type.BOOTS, 4, 4, TFArmorPaths.IRONWOOD_BOOTS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		NAGA_CHESTPLATE = REGISTRATE.item("naga_chestplate", p -> new NagaArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 13, 5, TFArmorPaths.IRONWOOD_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		NAGA_SHINGUARD = REGISTRATE.item("naga_shinguard", p -> new NagaArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 7, 5, TFArmorPaths.IRONWOOD_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		KNIGHTMETAL_HELMET = REGISTRATE.item("knightmetal_helmet", p -> new KnightmetalArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 11, 6, TFArmorPaths.KNIGHTMETAL_HELMETS)) // 护甲值和韧性请根据设计调整
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName()))) // 注意：cataLoc 应替换为您第二个文件中的资源定位方法，例如 modLoc
				.defaultLang().register();
		KNIGHTMETAL_CHESTPLATE = REGISTRATE.item("knightmetal_chestplate", p -> new KnightmetalArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 14, 6, TFArmorPaths.KNIGHTMETAL_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		KNIGHTMETAL_SHINGUARD = REGISTRATE.item("knightmetal_shinguard", p -> new KnightmetalArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 8, 6, TFArmorPaths.KNIGHTMETAL_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		KNIGHTMETAL_BOOTS = REGISTRATE.item("knightmetal_boots", p -> new KnightmetalArmorItem(p.stacksTo(1),
						ArmorItem.Type.BOOTS, 6, 6, TFArmorPaths.KNIGHTMETAL_BOOTS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		FIERY_HELMET = REGISTRATE.item("fiery_helmet", p -> new FieryArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 11, 6, TFArmorPaths.FIERY_HELMETS)) // 护甲值和韧性请根据设计调整
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName()))) // 注意：cataLoc 应替换为您第二个文件中的资源定位方法，例如 modLoc
				.defaultLang().register();
		FIERY_CHESTPLATE = REGISTRATE.item("fiery_chestplate", p -> new FieryArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 14, 6, TFArmorPaths.FIERY_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		FIERY_SHINGUARD = REGISTRATE.item("fiery_shinguard", p -> new FieryArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 8, 6, TFArmorPaths.FIERY_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		FIERY_BOOTS = REGISTRATE.item("fiery_boots", p -> new FieryArmorItem(p.stacksTo(1),
						ArmorItem.Type.BOOTS, 6, 6, TFArmorPaths.FIERY_BOOTS))
				.model((ctx, pvd) -> pvd.generated(ctx, tfLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		TF_GOLEM_WEAPON = GolemWeaponType.build(TFGolemWeaponMaterial.values());

		FIERY = reg("fiery", FieryModifier::new, "Deal %s%% fire damage to mobs not immune to fire");
		TF_DAMAGE = reg("tf_damage", TFDamageModifier::new, "TF Damage Bonus", "Deal %s%% extra damage in twilight forest");
		TF_HEALING = reg("tf_healing", TFHealingModifier::new, "TF Healing Bonus", "Healing becomes %s%% more in twilight forest");
		CARMINITE = reg("carminite", CarminiteModifier::new, "After being hurt, turn invisible and invinsible for %s seconds");
		NAGA = reg("naga", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ARMOR, () -> 10),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_SPEED, () -> 0.3),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATTACK, () -> 4),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, () -> 1)
		)).register();

		UP_CARMINITE = regModUpgrade("carminite", () -> CARMINITE, TFDispatch.MODID).lang("Carminite Upgrade").register();
		UP_STEELEAF = regModUpgrade("steeleaf", () -> TF_DAMAGE, TFDispatch.MODID).lang("Steeleaf Upgrade").register();
		UP_FIERY = regModUpgrade("fiery", () -> FIERY, TFDispatch.MODID).lang("Fiery Upgrade").register();
		UP_IRONWOOD = regModUpgrade("ironwood", () -> TF_HEALING, TFDispatch.MODID).lang("Ironwood Upgrade").register();
		UP_KNIGHTMETAL = regModUpgrade("knightmetal", () -> THORN, TFDispatch.MODID).lang("Knightmetal Upgrade").register();
		UP_NAGA = regModUpgrade("naga", () -> NAGA, TFDispatch.MODID).lang("Naga Upgrade").register();

	}

	public static void register() {

	}

	public static ResourceLocation tfLoc(String id) {
		return new ResourceLocation(TFDispatch.MODID, id);
	}
}

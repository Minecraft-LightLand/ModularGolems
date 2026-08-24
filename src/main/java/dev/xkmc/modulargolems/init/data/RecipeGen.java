package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.generators.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.golemdungeons.init.GolemDungeons;
import dev.xkmc.golemdungeons.init.reg.GDItems;
import dev.xkmc.l2core.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.NameFilterCard;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleBuilder;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceBuilder;
import dev.xkmc.modulargolems.content.recipe.GolemSmithBuilder;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;

import java.util.function.BiFunction;

public class RecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		var ri = pvd.registries().lookupOrThrow(Registries.ITEM);

		// golem base
		{

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.GOLEM_TEMPLATE.get()),
					Items.CLAY).pattern("CBC").pattern("BAB").pattern("CBC")
					.define('A', Items.COPPER_INGOT).define('B', Items.STICK)
					.define('C', Items.CLAY_BALL).save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.RETRIEVAL_WAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.ENDER_PEARL)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.DISPENSE_WAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.DISPENSER)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.COMMAND_WAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.GOLD_INGOT)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.RIDER_WAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.WHITE_BANNER)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.SQUAD_WAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern("  T").pattern(" E ").pattern("S  ")
					.define('E', Items.WHITE_BANNER)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.OMNI_COMMAND.get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" 1T").pattern("2S3").pattern("S4 ")
					.define('1', GolemItems.COMMAND_WAND.get())
					.define('2', GolemItems.DISPENSE_WAND.get())
					.define('3', GolemItems.RETRIEVAL_WAND.get())
					.define('4', GolemItems.RIDER_WAND.get())
					.define('S', Items.GOLD_INGOT)
					.define('T', Items.REDSTONE_BLOCK)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.EMPTY_UPGRADE.get(), 4),
					Items.AMETHYST_SHARD).pattern("CBC").pattern("BAB").pattern("CBC")
					.define('A', Items.AMETHYST_SHARD).define('B', Items.IRON_INGOT)
					.define('C', Items.CLAY_BALL).save(pvd);


			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.TABLE.get(), 1),
					Items.ECHO_SHARD).pattern("AGA").pattern("RTR").pattern("EIE")
					.define('A', Items.AMETHYST_SHARD)
					.define('G', GolemItems.SLICING_AXE)
					.define('R', Items.REDSTONE)
					.define('T', Items.SMITHING_TABLE)
					.define('E', Items.ECHO_SHARD)
					.define('I', Items.ANVIL).save(pvd);

			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_ARM);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_LEGS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_ARMS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_LEGS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.DOG_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.DOG_LEGS);

			unlock(pvd, new GolemAssembleBuilder(ri, GolemItems.HOLDER_GOLEM.get()),
					GolemItems.GOLEM_BODY.get())
					.pattern("ABA").pattern(" L ")
					.define('A', GolemItems.GOLEM_ARM.get())
					.define('B', GolemItems.GOLEM_BODY.get())
					.define('L', GolemItems.GOLEM_LEGS.get())
					.save(pvd);

			unlock(pvd, new GolemAssembleBuilder(ri, GolemItems.HOLDER_HUMANOID.get()),
					GolemItems.HUMANOID_BODY.get())
					.pattern("A").pattern("B").pattern("C")
					.define('A', GolemItems.HUMANOID_BODY.get())
					.define('B', GolemItems.HUMANOID_ARMS.get())
					.define('C', GolemItems.HUMANOID_LEGS.get())
					.save(pvd);

			unlock(pvd, new GolemAssembleBuilder(ri, GolemItems.HOLDER_DOG.get()),
					GolemItems.HUMANOID_BODY.get())
					.pattern("A").pattern("B")
					.define('A', GolemItems.DOG_BODY.get())
					.define('B', GolemItems.DOG_LEGS.get())
					.save(pvd);


			{
				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_GOLEM.get()),
						GolemItems.GOLEM_BODY.get())
						.pattern("PH")
						.define('P', GolemItems.GOLEM_ARM.get())
						.define('H', GolemItems.HOLDER_GOLEM.get())
						.save(pvd, getID(GolemItems.HOLDER_GOLEM, "_replace_right_arm"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_GOLEM.get()),
						GolemItems.GOLEM_BODY.get())
						.pattern("HP")
						.define('P', GolemItems.GOLEM_ARM.get())
						.define('H', GolemItems.HOLDER_GOLEM.get())
						.save(pvd, getID(GolemItems.HOLDER_GOLEM, "_replace_left_arm"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_GOLEM.get()),
						GolemItems.GOLEM_BODY.get())
						.pattern("P").pattern("H")
						.define('P', GolemItems.GOLEM_BODY.get())
						.define('H', GolemItems.HOLDER_GOLEM.get())
						.save(pvd, getID(GolemItems.HOLDER_GOLEM, "_replace_body"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_GOLEM.get()),
						GolemItems.GOLEM_BODY.get())
						.pattern("H").pattern("P")
						.define('P', GolemItems.GOLEM_LEGS.get())
						.define('H', GolemItems.HOLDER_GOLEM.get())
						.save(pvd, getID(GolemItems.HOLDER_GOLEM, "_replace_legs"));
			}

			{
				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_HUMANOID.get()),
						GolemItems.HUMANOID_BODY.get())
						.pattern("P").pattern("H")
						.define('P', GolemItems.HUMANOID_BODY.get())
						.define('H', GolemItems.HOLDER_HUMANOID.get())
						.save(pvd, getID(GolemItems.HOLDER_HUMANOID, "_replace_body"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_HUMANOID.get()),
						GolemItems.HUMANOID_BODY.get())
						.pattern("H").pattern("P")
						.define('P', GolemItems.HUMANOID_ARMS.get())
						.define('H', GolemItems.HOLDER_HUMANOID.get())
						.save(pvd, getID(GolemItems.HOLDER_HUMANOID, "_replace_arms"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_HUMANOID.get()),
						GolemItems.HUMANOID_BODY.get())
						.pattern("H").pattern("P")
						.define('P', GolemItems.HUMANOID_LEGS.get())
						.define('H', GolemItems.HOLDER_HUMANOID.get())
						.save(pvd, getID(GolemItems.HOLDER_HUMANOID, "_replace_legs"));
			}

			{
				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_DOG.get()),
						GolemItems.DOG_BODY.get())
						.pattern("P").pattern("H")
						.define('P', GolemItems.DOG_BODY.get())
						.define('H', GolemItems.HOLDER_DOG.get())
						.save(pvd, getID(GolemItems.HOLDER_DOG, "_replace_body"));

				unlock(pvd, new GolemReplaceBuilder(ri, GolemItems.HOLDER_DOG.get()),
						GolemItems.DOG_BODY.get())
						.pattern("H").pattern("P")
						.define('P', GolemItems.DOG_LEGS.get())
						.define('H', GolemItems.HOLDER_DOG.get())
						.save(pvd, getID(GolemItems.HOLDER_DOG, "_replace_legs"));
			}

			expand(pvd, GolemItems.HOLDER_GOLEM);
			expand(pvd, GolemItems.HOLDER_HUMANOID);
			expand(pvd, GolemItems.HOLDER_DOG);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.ADD_DIAMOND, 1), Items.DIAMOND)
					.pattern("ACA").pattern("CBC").pattern("ACA")
					.define('A', GolemItems.EMPTY_UPGRADE)
					.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.ADD_NETHERITE, 1), Items.NETHERITE_INGOT)
					.pattern("ADA").pattern("CBC").pattern("ACA")
					.define('A', GolemItems.EMPTY_UPGRADE)
					.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
					.define('C', Items.NETHERITE_INGOT)
					.define('D', Items.NETHER_STAR)
					.save(pvd);

		}

		// card
		{

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.CARD[DyeColor.WHITE.getId()].get()), GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" P ").pattern("PTP").pattern(" P ")
					.define('P', Items.PAPER)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd, getID("craft_config_card"));

			for (int i = 0; i < 16; i++) {
				Item dye = BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(DyeColor.byId(i).getName() + "_dye")).orElseThrow().value();
				unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD[i].get()), GolemItems.GOLEM_TEMPLATE.get())
						.requires(MGTagGen.CONFIG_CARD).requires(dye).save(pvd);

			}

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD_PATH.get()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.MAP).requires(Items.INK_SAC)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD_NAME.get()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.BOOK)
					.requires(Items.INK_SAC)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, NameFilterCard.getFriendly()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.BOOK)
					.requires(Items.INK_SAC)
					.requires(Items.PAPER)
					.save(pvd, getID("target_filter_friendly"));

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD_DEF.get()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.PAPER)
					.requires(Items.IRON_INGOT)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD_TYPE.get()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.PAPER)
					.requires(Items.CLAY_BALL)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.CARD_UUID.get()), GolemItems.GOLEM_TEMPLATE.get())
					.requires(GolemItems.GOLEM_TEMPLATE.get())
					.requires(Items.PAPER)
					.requires(Items.INK_SAC)
					.save(pvd);
		}

		// armor
		{
			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.GOLEMGUARD_HELMET.get()), Items.IRON_INGOT)
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', Items.IRON_HELMET)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.GOLEMGUARD_CHESTPLATE.get()), Items.IRON_INGOT)
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', Items.IRON_CHESTPLATE)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.GOLEMGUARD_SHINGUARD.get()), Items.IRON_INGOT)
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', Items.IRON_LEGGINGS)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.WINDSPIRIT_HELMET.get()), Items.DIAMOND)
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', Items.DIAMOND_HELMET)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.LAPIS_LAZULI)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.WINDSPIRIT_CHESTPLATE.get()), Items.DIAMOND)
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', Items.DIAMOND_CHESTPLATE)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.LAPIS_LAZULI)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.WINDSPIRIT_SHINGUARD.get()), Items.DIAMOND)
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', Items.DIAMOND_LEGGINGS)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.LAPIS_LAZULI)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.WINDSPIRIT_BOOTS.get()), Items.DIAMOND)
					.pattern("I I").pattern("BAB")
					.define('I', Items.DIAMOND_BOOTS)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.LAPIS_LAZULI)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()), Items.DIAMOND)
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', Items.NETHERITE_HELMET)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()), Items.DIAMOND)
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', Items.NETHERITE_CHESTPLATE)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()), Items.DIAMOND)
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', Items.NETHERITE_LEGGINGS)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BARBARICFLAMEVANGUARD_BOOTS.get()), Items.DIAMOND)
					.pattern("I I").pattern("BAB")
					.define('I', Items.NETHERITE_BOOTS)
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', Items.ANVIL)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BEACON_BOOTS.get()), Items.BEACON)
					.pattern("SXS").pattern("ABA").pattern("AAA")
					.define('X', Items.BEACON)
					.define('S', Items.NETHER_STAR)
					.define('B', GolemItems.WINDSPIRIT_BOOTS.get())
					.define('A', Items.ANVIL)
					.save(pvd);
		}

		// weapon
		{
			for (var type : GolemWeaponType.values()) {
				for (var mat : VanillaGolemWeaponMaterial.values()) {
					Item item = GolemItems.METALGOLEM_WEAPON[type.ordinal()][mat.ordinal()].get();
					if (mat == VanillaGolemWeaponMaterial.NETHERITE) {
						Item prev = GolemItems.METALGOLEM_WEAPON[type.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()].get();
						smithing(pvd, prev, mat.getIngot(), item);
					} else {
						type.pattern(unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, item), mat.getIngot()))
								.define('I', mat.getIngot())
								.define('S', Items.STICK)
								.define('T', GolemItems.GOLEM_TEMPLATE.get())
								.save(pvd);
					}
				}
			}

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.HEAVY_SPEAR), Items.HEAVY_CORE)
					.pattern(" HH").pattern("TIH").pattern("IT ")
					.define('H', Items.HEAVY_CORE)
					.define('I', Items.BREEZE_ROD)
					.define('T', GolemItems.GOLEM_TEMPLATE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.IRON_BOW.get()), Items.BOW)
					.pattern("CII").pattern("BCI").pattern("IBC")
					.define('I', Items.IRON_INGOT)
					.define('B', GolemItems.GOLEM_TEMPLATE)
					.define('C', Items.IRON_CHAIN)
					.save(pvd);

			smithing(pvd, GolemItems.IRON_BOW.get(), Items.NETHERITE_INGOT, GolemItems.NETHERITE_BOW.get());

			smithing(pvd,
					GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()].get(),
					Blocks.STONECUTTER.asItem(),
					GolemItems.SLICING_AXE.get()
			);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.BEACON_CANNON.get()), Items.BEACON)
					.pattern("III").pattern("BDD").pattern("TII")
					.define('I', Items.IRON_INGOT)
					.define('T', GolemItems.GOLEM_TEMPLATE)
					.define('D', Items.DIAMOND)
					.define('B', Items.BEACON)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.FLAME_THROWER.get()), Items.BLAZE_ROD)
					.pattern("II ").pattern("BDD").pattern("TI ")
					.define('I', Items.IRON_INGOT)
					.define('T', GolemItems.GOLEM_TEMPLATE)
					.define('D', Items.BLAZE_ROD)
					.define('B', Items.BLAST_FURNACE)
					.save(pvd);

			/* TODO
			if (ModList.get().isLoaded(L2Complements.MODID)) {

				unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.SONIC_CANNON.get()), LCItems.SONIC_SHOOTER.get())
						.pattern("III").pattern("BDD").pattern("TII")
						.define('I', LCItems.WARDEN_BONE_SHARD)
						.define('T', GolemItems.GOLEM_TEMPLATE)
						.define('D', LCItems.RESONANT_FEATHER)
						.define('B', LCItems.SONIC_SHOOTER)
						.save(ConditionalRecipeWrapper.mod(pvd, L2Complements.MODID), "sonic_cannon_complements");
			}*/

			if (ModList.get().isLoaded(GolemDungeons.MODID)) {

				unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.COMBAT, GolemItems.SONIC_CANNON.get()), GDItems.SCULK_SCYTHE.get())
						.pattern("III").pattern("BBD").pattern("TII")
						.define('I', GDItems.SCULK_SCYTHE)
						.define('T', GolemItems.GOLEM_TEMPLATE)
						.define('B', Items.SCULK_CATALYST)
						.define('D', Items.SCULK_SHRIEKER)
						.save(ConditionalRecipeWrapper.mod(pvd, GolemDungeons.MODID), "sonic_cannon_dungeon");
			}
		}

		// upgrades
		{

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.FIRE_IMMUNE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.MAGMA_CREAM)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.THUNDER_IMMUNE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.LIGHTNING_ROD)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.RECYCLE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern(" C ").pattern("ABA").pattern(" D ")
					.define('A', Items.ENDER_PEARL)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.TOTEM_OF_UNDYING)
					.define('D', Items.RESPAWN_ANCHOR)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.DIAMOND.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CCC").pattern("CBC").pattern("CCC")
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.NETHERITE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.NETHERITE_INGOT)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.QUARTZ.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.QUARTZ_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.GOLD.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.GOLDEN_APPLE)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.GOLDEN_CARROT)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.SPONGE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.WET_SPONGE)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.ENCHANTED_GOLD.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.ENCHANTED_GOLDEN_APPLE)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.FLOAT.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(ItemTags.BOATS)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.SWIM.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.HEART_OF_THE_SEA)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.PLAYER_IMMUNE.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.SHIELD)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.BELL.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.BELL)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.ENDER_SIGHT.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.ENDER_EYE)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.SPEED.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.RABBIT_FOOT)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.WEAK.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("CDC")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.FERMENTED_SPIDER_EYE)
					.define('D', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.SLOW.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("EDE")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.FERMENTED_SPIDER_EYE)
					.define('D', Items.REDSTONE)
					.define('E', Items.SUGAR)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.WITHER.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("CDC")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.WITHER_ROSE)
					.define('D', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.EMERALD.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.EMERALD_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.EMERALD)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.PICKUP.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("AAA").pattern("DBD").pattern(" C ")
					.define('A', Items.HOPPER)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.LAVA_BUCKET)
					.define('D', Items.ENDER_PEARL)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.PICKUP_MENDING.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get())
					.requires(EnchantmentIngredient.of(pvd.registries(), Enchantments.MENDING, 1))
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.PICKUP_NO_DESTROY.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get())
					.requires(Items.ZOMBIE_HEAD)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.TALENTED.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CEC").pattern("ABA").pattern("CAC")
					.define('E', Items.NETHER_STAR)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', ItemTags.SKULLS)
					.define('A', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.CAULDRON.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CEC").pattern("ABA").pattern("CDC")
					.define('A', Items.BLAZE_POWDER)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DRAGON_BREATH)
					.define('D', Items.CAULDRON)
					.define('E', Items.NETHER_STAR)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(ri, RecipeCategory.MISC, GolemItems.MOUNT_UPGRADE.get()), GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.SADDLE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(ri, RecipeCategory.MISC, GolemItems.SIZE_UPGRADE.get()), GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.IRON_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.COPPER_BLOCK)
					.save(pvd);

		}

		CompatManager.dispatchGenRecipe(pvd);
	}

	public static <T extends RecipeBuilder> T unlock(RegistrateRecipeProvider pvd, T func, Item item) {
		func.unlockedBy("has_" + pvd.safeName(item), DataIngredient.items(item).getCriterion(pvd));
		return func;
	}

	public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, Criterion<InventoryChangeTrigger.TriggerInstance>, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCriterion(pvd));
	}

	public static void smithing(RegistrateRecipeProvider pvd, Item in, Item mat, Item out) {
		smithing(pvd, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, in, mat, out);
	}


	public static void smithing(RegistrateRecipeProvider pvd, Item template, Item in, Item mat, Item out) {
		Ingredient ing = Ingredient.of(template);
		var ans = SmithingTransformRecipeBuilder.smithing(ing, Ingredient.of(in), Ingredient.of(mat),
				RecipeCategory.COMBAT, out);
		ans.unlocks("has_" + pvd.safeName(mat), DataIngredient.items(mat).getCriterion(pvd));
		ans.save(pvd, getID(out));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> void
	expand(RegistrateRecipeProvider pvd, ItemEntry<GolemHolder<T, P>> holder) {
		var ans = new GolemSmithBuilder(pvd.registries().lookupOrThrow(Registries.ITEM), holder.get(), MGTagGen.EXPANSION);
		ans.unlocks("has_" + pvd.safeName(holder.get()), DataIngredient.items(holder.get()).getCriterion(pvd));
		ans.save(pvd, ModularGolems.loc("expansion_" + holder.getId().getPath()));
	}

	private static ResourceKey<Recipe<?>> getID(String id) {
		return ResourceKey.create(Registries.RECIPE, ModularGolems.loc(id));
	}

	private static ResourceKey<Recipe<?>> getID(Item item) {
		return ResourceKey.create(Registries.RECIPE, ModularGolems.loc(BuiltInRegistries.ITEM.getKey(item).getPath()));
	}

	private static ResourceKey<Recipe<?>> getID(ItemLike item, String suffix) {
		return ResourceKey.create(Registries.RECIPE, ModularGolems.loc(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath() + suffix));
	}


}

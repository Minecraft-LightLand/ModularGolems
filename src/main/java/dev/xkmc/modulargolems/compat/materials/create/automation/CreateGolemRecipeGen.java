package dev.xkmc.modulargolems.compat.materials.create.automation;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.compat.materials.create.CreateDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.equipments.TFGolemWeaponMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import twilightforest.init.TFItems;

import java.util.Set;

public class CreateGolemRecipeGen {

	private static final Set<String> SPECIAL = Set.of("andesite_alloy", "brass", "railway");

	public static void genAllUpgradeRecipes(RegistrateRecipeProvider pvd) {
		var ing = CompatManager.gatherConfig();
		for (var part : GolemPart.LIST) {
			for (var ent : ing.entrySet()) {
				if (SPECIAL.contains(ent.getKey().getPath())) continue;
				genAssembly(pvd, part, ent.getKey(), ent.getValue());
			}
		}
		genSpecialRecipes(pvd);
		genRecycleRecipes(pvd);
	}

	public static void genRecycleRecipes(RegistrateRecipeProvider pvd) {
		genCrushingRecipe(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE)
				.output(Items.DIAMOND, 40)
				.output(Items.NETHERITE_INGOT, 6)
				.build(pvd);

		genCrushingRecipe(GolemItems.BARBARICFLAMEVANGUARD_HELMET)
				.output(Items.DIAMOND, 20)
				.output(Items.NETHERITE_INGOT, 5)
				.build(pvd);

		genCrushingRecipe(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD)
				.output(Items.DIAMOND, 18)
				.output(Items.NETHERITE_INGOT, 3)
				.build(pvd);

		genCrushingRecipe(GolemItems.WINDSPIRIT_CHESTPLATE)
				.output(Items.DIAMOND, 40)
				.build(pvd);

		genCrushingRecipe(GolemItems.WINDSPIRIT_HELMET)
				.output(Items.DIAMOND, 20)
				.build(pvd);

		genCrushingRecipe(GolemItems.WINDSPIRIT_SHINGUARD)
				.output(Items.DIAMOND, 18)
				.build(pvd);

		genCrushingRecipe(GolemItems.WINDSPIRIT_BOOTS)
				.output(Items.DIAMOND, 7)
				.build(pvd);

		genCrushingRecipe(GolemItems.GOLEMGUARD_CHESTPLATE)
				.output(Items.IRON_INGOT, 40)
				.build(pvd);

		genCrushingRecipe(GolemItems.GOLEMGUARD_HELMET)
				.output(Items.IRON_INGOT, 20)
				.build(pvd);

		genCrushingRecipe(GolemItems.GOLEMGUARD_SHINGUARD)
				.output(Items.IRON_INGOT, 18)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()])
				.output(Items.DIAMOND, 4)
				.output(Items.NETHERITE_SCRAP, 4)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()])
				.output(Items.DIAMOND, 5)
				.output(Items.NETHERITE_SCRAP, 4)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()])
				.output(Items.DIAMOND, 3)
				.output(Items.NETHERITE_SCRAP, 4)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()])
				.output(Items.DIAMOND, 4)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()])
				.output(Items.DIAMOND, 5)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()])
				.output(Items.DIAMOND, 3)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()])
				.output(Items.IRON_INGOT, 4)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()])
				.output(Items.IRON_INGOT, 5)
				.build(pvd);

		genCrushingRecipe(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()])
				.output(Items.IRON_INGOT, 3)
				.build(pvd);

		if (ModList.get().isLoaded(TFDispatch.MODID)) {
			genCrushingRecipe(TFCompatRegistry.IRONWOOD_HELMET)
					.output(TFItems.IRONWOOD_INGOT.get(), 20)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.IRONWOOD_CHESTPLATE)
					.output(TFItems.IRONWOOD_INGOT.get(), 40)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.IRONWOOD_SHINGUARD)
					.output(TFItems.IRONWOOD_INGOT.get(), 18)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.IRONWOOD_BOOTS)
					.output(TFItems.IRONWOOD_INGOT.get(), 7)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.NAGA_CHESTPLATE)
					.output(TFItems.NAGA_SCALE.get(), 12)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.NAGA_SHINGUARD)
					.output(TFItems.NAGA_SCALE.get(), 10)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.KNIGHTMETAL_HELMET)
					.output(TFItems.KNIGHTMETAL_INGOT.get(), 20)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.KNIGHTMETAL_CHESTPLATE)
					.output(TFItems.KNIGHTMETAL_INGOT.get(), 40)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.KNIGHTMETAL_SHINGUARD)
					.output(TFItems.KNIGHTMETAL_INGOT.get(), 18)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.KNIGHTMETAL_BOOTS)
					.output(TFItems.KNIGHTMETAL_INGOT.get(), 7)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.FIERY_HELMET)
					.output(TFItems.FIERY_INGOT.get(), 20)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.FIERY_CHESTPLATE)
					.output(TFItems.FIERY_INGOT.get(), 40)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.FIERY_SHINGUARD)
					.output(TFItems.FIERY_INGOT.get(), 18)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			genCrushingRecipe(TFCompatRegistry.FIERY_BOOTS)
					.output(TFItems.FIERY_INGOT.get(), 7)
					.whenModLoaded(TFDispatch.MODID)
					.build(pvd);

			for (var e : TFGolemWeaponMaterial.values()) {

				genCrushingRecipe(TFCompatRegistry.TF_GOLEM_WEAPON
						[GolemWeaponType.AXE.ordinal()][e.ordinal()])
						.output(e.getIngot(), 4)
						.whenModLoaded(TFDispatch.MODID)
						.build(pvd);

				genCrushingRecipe(TFCompatRegistry.TF_GOLEM_WEAPON
						[GolemWeaponType.SWORD.ordinal()][e.ordinal()])
						.whenModLoaded(TFDispatch.MODID)
						.output(e.getIngot(), 5)
						.build(pvd);

				genCrushingRecipe(TFCompatRegistry.TF_GOLEM_WEAPON
						[GolemWeaponType.SPEAR.ordinal()][e.ordinal()])
						.output(e.getIngot(), 3)
						.whenModLoaded(TFDispatch.MODID)
						.build(pvd);

			}


		}

	}

	private static ProcessingRecipeBuilder<?> genCrushingRecipe(ItemEntry<?> item) {
		var recipe = new ProcessingRecipeBuilder<>(CrushingRecipe::new, item.getId());
		recipe.withCondition(new ModLoadedCondition(CreateDispatch.MODID));
		recipe.require(item);
		return recipe;
	}

	public static void genSpecialRecipes(RegistrateRecipeProvider pvd) {
		for (var part : GolemPart.LIST) {
			genAssembly(pvd, part, ResourceLocation.fromNamespaceAndPath(CreateDispatch.MODID, "andesite_alloy"), AllItems.ANDESITE_ALLOY, AllBlocks.COGWHEEL);
			genAssembly(pvd, part, ResourceLocation.fromNamespaceAndPath(CreateDispatch.MODID, "brass"), Ingredient.of(AllTags.commonItemTag("ingots/brass")), AllItems.PRECISION_MECHANISM);
			genAssembly(pvd, part, ResourceLocation.fromNamespaceAndPath(CreateDispatch.MODID, "railway"), Ingredient.of(AllTags.commonItemTag("plates/brass")), AllItems.PRECISION_MECHANISM, AllItems.ELECTRON_TUBE, AllItems.STURDY_SHEET);
		}
	}

	private static void genAssembly(RegistrateRecipeProvider pvd, GolemPart<?, ?> part, ResourceLocation id, ItemLike ingredient, ItemLike... parts) {
		genAssembly(pvd, part, id, Ingredient.of(ingredient), parts);
	}

	private static void genAssembly(RegistrateRecipeProvider pvd, GolemPart<?, ?> part, ResourceLocation id, Ingredient ingredient, ItemLike... parts) {
		var part_rl = BuiltInRegistries.ITEM.getKey(part);
		String item_name = part_rl.getPath();
		var recipe = new ConditionalSARecipeBuilder(ModularGolems.loc(id.getPath() + "_assemble_" + item_name));
		var incomplete = BuiltInRegistries.ITEM.get(part_rl.withPrefix("incomplete_"));
		recipe.require(part).transitionTo(incomplete);
		recipe.addStep(DeployerApplicationRecipe::new, e -> e.require(ingredient));
		if (parts.length == 0) {
			for (int i = 0; i < 3; i++) {
				recipe.addStep(PressingRecipe::new, e -> e);
			}
		} else {
			recipe.addStep(PressingRecipe::new, e -> e);
			for (var p : parts) {
				recipe.addStep(DeployerApplicationRecipe::new, e -> e.require(p));
			}
			recipe.addStep(DeployerApplicationRecipe::new, e -> e.require(AllItems.WRENCH).toolNotConsumed());
		}
		recipe.loops(part.count);
		String modid = id.getNamespace();
		recipe.withCondition(new ModLoadedCondition(CreateDispatch.MODID));
		if (!modid.equals(ModularGolems.MODID) && !modid.equals(CreateDispatch.MODID)) {
			recipe.withCondition(new ModLoadedCondition(modid));
		}
		recipe.addOutput(GolemPart.setMaterial(part.getDefaultInstance(), id), 1);
		recipe.build(pvd);
	}

}

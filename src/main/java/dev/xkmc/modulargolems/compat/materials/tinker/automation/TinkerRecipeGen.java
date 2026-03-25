package dev.xkmc.modulargolems.compat.materials.tinker.automation;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;

import java.util.function.Consumer;

public class TinkerRecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		findAndTry(pvd, "iron", TinkerFluids.moltenIron, 90);
		findAndTry(pvd, "copper", TinkerFluids.moltenCopper, 90);
		findAndTry(pvd, "gold", TinkerFluids.moltenGold, 90);
		findAndTry(pvd, "netherite", TinkerFluids.moltenNetherite, 90);
		findAndTry(pvd, "zinc", TinkerFluids.moltenZinc, 90);
		findAndTry(pvd, "cobalt", TinkerFluids.moltenCobalt, 90);
		findAndTry(pvd, "amethyst_bronze", TinkerFluids.moltenAmethystBronze, 90);
		findAndTry(pvd, "manyullyn", TinkerFluids.moltenManyullyn, 90);
		findAndTry(pvd, "hepatizon", TinkerFluids.moltenHepatizon, 90);
		findAndTry(pvd, "rose_gold", TinkerFluids.moltenRoseGold, 90);

		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.THUNDER_IMMUNE, TinkerFluids.moltenCopper, 810);
		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.DIAMOND, TinkerFluids.moltenDiamond, 600);
		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.NETHERITE, TinkerFluids.moltenNetherite, 360);
		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.GOLD, TinkerFluids.moltenGold, 2430);
		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.QUARTZ, TinkerFluids.moltenQuartz, 1200);
		cast(pvd, GolemItems.EMPTY_UPGRADE, GolemItems.EMERALD, TinkerFluids.moltenEmerald, 2700);

		cast(pvd, Items.IRON_HELMET, GolemItems.GOLEMGUARD_HELMET, TinkerFluids.moltenIron, 16 * 90);
		cast(pvd, Items.IRON_CHESTPLATE, GolemItems.GOLEMGUARD_CHESTPLATE, TinkerFluids.moltenIron, 32 * 90);
		cast(pvd, Items.IRON_LEGGINGS, GolemItems.GOLEMGUARD_SHINGUARD, TinkerFluids.moltenIron, 11 * 90);
		cast(pvd, Items.DIAMOND_HELMET, GolemItems.WINDSPIRIT_HELMET, TinkerFluids.moltenDiamond, 16 * 100);
		cast(pvd, Items.DIAMOND_CHESTPLATE, GolemItems.WINDSPIRIT_CHESTPLATE, TinkerFluids.moltenDiamond, 32 * 100);
		cast(pvd, Items.DIAMOND_LEGGINGS, GolemItems.WINDSPIRIT_SHINGUARD, TinkerFluids.moltenDiamond, 11 * 100);
		cast(pvd, Items.DIAMOND_BOOTS, GolemItems.WINDSPIRIT_BOOTS, TinkerFluids.moltenDiamond, 3 * 100);
		cast(pvd, GolemItems.WINDSPIRIT_HELMET, GolemItems.BARBARICFLAMEVANGUARD_HELMET, TinkerFluids.moltenNetherite, 5 * 90);
		cast(pvd, GolemItems.WINDSPIRIT_CHESTPLATE, GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE, TinkerFluids.moltenNetherite, 6 * 90);
		cast(pvd, GolemItems.WINDSPIRIT_SHINGUARD, GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD, TinkerFluids.moltenNetherite, 3 * 90);

		melt(pvd, GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE, TinkerFluids.moltenNetherite, 6 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(40 * 100)));

		melt(pvd, GolemItems.BARBARICFLAMEVANGUARD_HELMET, TinkerFluids.moltenNetherite, 5 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(20 * 100)));

		melt(pvd, GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD, TinkerFluids.moltenNetherite, 3 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(18 * 100)));

		melt(pvd, GolemItems.WINDSPIRIT_CHESTPLATE, TinkerFluids.moltenDiamond, 40 * 100,
				e -> {
				});

		melt(pvd, GolemItems.WINDSPIRIT_HELMET, TinkerFluids.moltenDiamond, 20 * 100,
				e -> {
				});

		melt(pvd, GolemItems.WINDSPIRIT_SHINGUARD, TinkerFluids.moltenDiamond, 18 * 100,
				e -> {
				});

		melt(pvd, GolemItems.WINDSPIRIT_BOOTS, TinkerFluids.moltenDiamond, 7 * 100,
				e -> {
				});

		melt(pvd, GolemItems.GOLEMGUARD_CHESTPLATE, TinkerFluids.moltenIron, 40 * 90,
				e -> {
				});

		melt(pvd, GolemItems.GOLEMGUARD_HELMET, TinkerFluids.moltenIron, 20 * 90,
				e -> {
				});

		melt(pvd, GolemItems.GOLEMGUARD_SHINGUARD, TinkerFluids.moltenIron, 18 * 90,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()], TinkerFluids.moltenDebris, 4 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(4 * 100)));

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()], TinkerFluids.moltenDebris, 4 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(5 * 100)));

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()], TinkerFluids.moltenDebris, 4 * 90,
				e -> e.addByproduct(TinkerFluids.moltenDiamond.result(3 * 100)));

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()], TinkerFluids.moltenDiamond, 4 * 100,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()], TinkerFluids.moltenDiamond, 5 * 100,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()], TinkerFluids.moltenDiamond, 3 * 100,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()], TinkerFluids.moltenIron, 4 * 90,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()], TinkerFluids.moltenIron, 5 * 90,
				e -> {
				});

		melt(pvd, GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SPEAR.ordinal()][VanillaGolemWeaponMaterial.IRON.ordinal()], TinkerFluids.moltenIron, 3 * 90,
				e -> {
				});

	}

	private static void findAndTry(RegistrateRecipeProvider pvd, String id, FlowingFluidObject<ForgeFlowingFluid> fluid, int amount) {
		var tag = fluid.getCommonTag();
		if (tag == null) return;
		for (var e : CompatManager.gatherConfig().entrySet()) {
			if (e.getKey().getPath().equals(id)) {
				genCasting(pvd, e.getKey(), fluid, amount);
			}
		}
	}

	private static void genCasting(RegistrateRecipeProvider pvd, ResourceLocation id, FlowingFluidObject<ForgeFlowingFluid> fluid, int ingot) {
		for (var part : GolemPart.LIST) {
			var part_rl = ForgeRegistries.ITEMS.getKey(part);
			assert part_rl != null;
			String item_name = part_rl.getPath();
			var rl = new ResourceLocation(ModularGolems.MODID, "casting/" + id.getPath() + "_casting_" + item_name);

			ItemStack result = GolemPart.setMaterial(part.getDefaultInstance(), id);
			RecipeGen.unlock(pvd, ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromStack(result))::unlockedBy, part)
					.setCast(part, true).setFluidAndTime(fluid, ingot * part.count)
					.save(ConditionalRecipeWrapper.mod(pvd, TConstruct.MOD_ID, id.getNamespace()), rl);
		}
	}

	private static void cast(RegistrateRecipeProvider pvd, ItemLike src, ItemEntry<?> upgrade, FlowingFluidObject<ForgeFlowingFluid> fluid, int amount) {
		var rl = upgrade.getId().withPrefix("casting/");
		RecipeGen.unlock(pvd, ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromItem(upgrade))::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
				.setCast(src, true).setFluidAndTime(fluid, amount)
				.save(ConditionalRecipeWrapper.mod(pvd, TConstruct.MOD_ID), rl);
	}

	private static void melt(RegistrateRecipeProvider pvd, ItemEntry<?> src, FlowingFluidObject<ForgeFlowingFluid> fluid, int amount, Consumer<MeltingRecipeBuilder> cons) {
		var rl = src.getId().withPrefix("melting/");
		var recipe = RecipeGen.unlock(pvd, MeltingRecipeBuilder.melting(Ingredient.of(src), fluid, amount, 1)::unlockedBy, GolemItems.EMPTY_UPGRADE.get());
		cons.accept(recipe);
		recipe.save(ConditionalRecipeWrapper.mod(pvd, TConstruct.MOD_ID), rl);
	}


}

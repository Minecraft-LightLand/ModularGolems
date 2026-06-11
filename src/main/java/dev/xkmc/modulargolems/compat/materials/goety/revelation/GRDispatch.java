package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.Polarice3.Goety.common.items.ModItems;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyDispatch;
import dev.xkmc.modulargolems.compat.materials.goety.title.ApollyonBowGoal;
import dev.xkmc.modulargolems.compat.misc.PatchouliFlagHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

public class GRDispatch extends ModDispatch {

	public static boolean isLoaded() {
		return ModList.get().isLoaded(GRDispatch.MODID) || ModList.get().isLoaded(GoetyDispatch.MODID) && !FMLLoader.isProduction();
	}

	public static final String MODID = "goety_revelation";

	public GRDispatch() {
		super(() -> GRClient::new);
		GRCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".apocalyptium", "Apocalyptium");
	}

	@Override
	public void commonSetup() {
		if (ModList.get().isLoaded("patchouli")) {
			boolean flag = ForgeRegistries.ITEMS.containsKey(
					new ResourceLocation(MODID, "apocalyptium_ingot"));
			PatchouliFlagHelper.setFlag("modulargolems:goety_revelation:apocalyptium", flag);
		}
		GolemWeaponRegistry.HUMANOID.register(new ResourceLocation(MODID, "bow"),
				(golem, stack, hand) ->
						golem instanceof HumanoidGolemEntity h && h.getModifiers().getOrDefault(
								GRCompatRegistry.BOW.get(), 0) > 0 ?
								WeaponRegistry.BOW.getProperties(stack) : Optional.empty(),
				(golem, melee) -> new ApollyonBowGoal<>(golem, melee, 1.0D, 35)
		);
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CD.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BFB").pattern("BOB").pattern("BUB")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_BOW.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" B ").pattern("FOF").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_FAST.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BBB").pattern("FOF").pattern("FUF")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CURSE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" F ").pattern("BOB").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_DOOM),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_HELMET.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_HELMET.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), GRCompatRegistry.APOCALYPTIUM_HELMET.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_DOOM),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_CHESTPLATE.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), GRCompatRegistry.APOCALYPTIUM_CHESTPLATE.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_DOOM),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_SHINGUARD.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), GRCompatRegistry.APOCALYPTIUM_SHINGUARD.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_DOOM),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_BOOTS.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_BOOTS.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_BOOTS.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), GRCompatRegistry.APOCALYPTIUM_BOOTS.getId());
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new GRConfigGen(gen);
	}

}

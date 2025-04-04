package dev.xkmc.modulargolems.compat.materials.geoty;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class GoetyDispatch extends ModDispatch {

	public static final String MODID = Goety.MOD_ID;

	public GoetyDispatch() {
		GoetyCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".cursed_metal", "Cursed Metal");
		pvd.add("golem_material." + MODID + ".dark_metal", "Dark Metal");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_BLAST.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.FIRE_BLAST_FOCUS.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_TORNADO.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.CYCLONE_FOCUS.get())
				.requires(Items.LAVA_BUCKET)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_CLOUD.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.HAIL_FOCUS.get())
				.requires(Items.LAVA_BUCKET)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_APOSTLE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_HAT.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new GoetyConfigGen(gen);
	}

}

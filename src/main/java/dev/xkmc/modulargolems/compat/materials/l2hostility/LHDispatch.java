package dev.xkmc.modulargolems.compat.materials.l2hostility;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2complements.init.materials.LCMats;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.l2complements.LCCompatRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class LHDispatch extends ModDispatch {

	public static final String MODID = "l2hostility";

	public LHDispatch() {
		LHCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.CORE.get(), 1)::unlockedBy, LHItems.CHAOS_INGOT.get())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', GolemItems.TALENTED.get())
				.define('B', LHItems.CHAOS_INGOT.get())
				.define('C', LHTraits.ADAPTIVE.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.TANK.get(), 1)::unlockedBy, LHTraits.TANK.get().asItem())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', GolemItems.NETHERITE.get())
				.define('B', LCItems.WARDEN_BONE_SHARD.get())
				.define('C', LHTraits.TANK.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.SPEED.get(), 1)::unlockedBy, LHTraits.SPEEDY.get().asItem())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', LCCompatRegistry.SPEED_UP.get())
				.define('B', LCItems.CAPTURED_WIND.get())
				.define('C', LHTraits.SPEEDY.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.PROTECTION.get(), 1)::unlockedBy, LHTraits.PROTECTION.get().asItem())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', GolemItems.DIAMOND.get())
				.define('B', Items.SCUTE)
				.define('C', LHTraits.PROTECTION.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.REGEN.get(), 1)::unlockedBy, LHTraits.REGEN.get().asItem())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', GolemItems.ENCHANTED_GOLD.get())
				.define('B', LCMats.TOTEMIC_GOLD.getIngot())
				.define('C', LHTraits.REGEN.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapedRecipeBuilder(RecipeCategory.MISC, LHCompatRegistry.REFLECTIVE.get(), 1)::unlockedBy, LHTraits.REFLECT.get().asItem())
				.pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', LCCompatRegistry.ATK_UP.get())
				.define('B', LCItems.EXPLOSION_SHARD)
				.define('C', LHTraits.REFLECT.get().asItem())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LHConfigGen(gen);
	}

	@Override
	public void dispatchClientSetup() {
	}

}

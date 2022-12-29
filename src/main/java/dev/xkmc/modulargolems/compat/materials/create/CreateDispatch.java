package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import dev.xkmc.l2library.base.recipe.ConditionalRecipeWrapper;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

public class CreateDispatch extends ModDispatch {

	public static final String MODID = "create";

	public CreateDispatch() {
		CreateCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".zinc", "Zinc");
		pvd.add("golem_material." + MODID + ".andesite_alloy", "Andesite Alloy");
		pvd.add("golem_material." + MODID + ".brass", "Brass");
		pvd.add("golem_material." + MODID + ".railway", "Railway");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(CreateCompatRegistry.UP_COATING.get())::unlockedBy, AllItems.ZINC_INGOT.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', AllTags.forgeItemTag("ingots/zinc"))
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));


		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(CreateCompatRegistry.UP_PUSH.get())::unlockedBy, AllItems.EXTENDO_GRIP.get())
				.pattern(" C ").pattern("ABA").pattern(" C ")
				.define('A', AllItems.EXTENDO_GRIP.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', AllItems.PRECISION_MECHANISM.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new CreateConfigGen(gen);
	}

}

package dev.xkmc.modulargolems.compat.materials.l2complements;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;

public class LCDispatch extends ModDispatch {

	public static final String MODID = "l2complements";

	public LCDispatch() {
		LCCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".totemic_gold", "Totemic Gold");
		pvd.add("golem_material." + MODID + ".poseidite", "Poseidite");
		pvd.add("golem_material." + MODID + ".shulkerate", "Shulkerate");
		pvd.add("golem_material." + MODID + ".eternium", "Eternium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.FORCE_FIELD.get(), 1)::unlockedBy, LCItems.FORCE_FIELD.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.FORCE_FIELD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.FREEZE_UP.get())::unlockedBy, LCItems.HARD_ICE.get())
				.pattern("CAC").pattern("1B2").pattern("CAC")
				.define('C', Items.GOLD_INGOT)
				.define('A', LCItems.HARD_ICE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', new EnchantmentIngredient(LCEnchantments.ICE_THORN.get(), 1))
				.define('2', new EnchantmentIngredient(LCEnchantments.ICE_BLADE.get(), 1))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.FLAME_UP.get())::unlockedBy, LCItems.SOUL_FLAME.get())
				.pattern("CAC").pattern("1B2").pattern("CAC")
				.define('C', Items.GOLD_INGOT)
				.define('A', LCItems.SOUL_FLAME.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', new EnchantmentIngredient(LCEnchantments.FLAME_THORN.get(), 1))
				.define('2', new EnchantmentIngredient(LCEnchantments.FLAME_BLADE.get(), 1))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.TELEPORT_UP.get(), 1)::unlockedBy, LCItems.VOID_EYE.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.VOID_EYE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(LCCompatRegistry.ATK_UP.get(), 1)::unlockedBy, LCItems.EXPLOSION_SHARD.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.EXPLOSION_SHARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(LCCompatRegistry.SPEED_UP.get(), 1)::unlockedBy, LCItems.CAPTURED_WIND.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.CAPTURED_WIND.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LCConfigGen(gen);
	}

	@Override
	public void dispatchClientSetup(IEventBus bus) {
		ForceFieldLayer.registerLayer();
	}
}

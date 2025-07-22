package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class LMDispatch extends ModDispatch {

	public static final String MODID = LegendaryMonsters.MOD_ID;

	public LMDispatch() {
		LMCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".molten_metal", "Molten Metal");
		pvd.add("golem_material." + MODID + ".cloud", "Cloud");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		LMProxy.genRecipe(pvd);
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LMConfigGen(gen);
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
		LMProxy.genLootModifier(pvd);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(MODID, "cloud"),
				ModelOverride.texturePredicate((e) -> e.getHealth() <= e.getMaxHealth() / 2 ? "_dark" : ""));
	}

}

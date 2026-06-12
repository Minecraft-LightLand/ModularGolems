package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyDispatch;
import dev.xkmc.modulargolems.compat.materials.goety.title.ApollyonBowGoal;
import dev.xkmc.modulargolems.compat.misc.PatchouliFlagHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

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
		GRRecipeGen.genRecipe(pvd);
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new GRConfigGen(gen);
	}

}

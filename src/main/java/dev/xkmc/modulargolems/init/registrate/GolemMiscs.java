package dev.xkmc.modulargolems.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2library.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigMenu;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigScreen;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsScreen;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemMiscs {

	public static final RegistryEntry<AbstractShapedRecipe.Serializer<GolemAssembleRecipe>> ASSEMBLE =
			reg("golem_assemble", () -> new AbstractShapedRecipe.Serializer<>(GolemAssembleRecipe::new));

	public static final MenuEntry<EquipmentsMenu> EQUIPMENTS =
			REGISTRATE.menu("equipments", EquipmentsMenu::fromNetwork, () -> EquipmentsScreen::new)
					.register();

	public static final MenuEntry<ToggleGolemConfigMenu> CONFIG_TOGGLE =
			REGISTRATE.menu("config_toggle", ToggleGolemConfigMenu::fromNetwork, () -> ToggleGolemConfigScreen::new)
					.register();

	private static <A extends RecipeSerializer<?>> RegistryEntry<A> reg(String id, NonNullSupplier<A> sup) {
		return REGISTRATE.simple(id, ForgeRegistries.Keys.RECIPE_SERIALIZERS, sup);
	}

	public static void register() {
	}

}

package dev.xkmc.modulargolems.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2core.init.reg.simple.IngReg;
import dev.xkmc.l2core.init.reg.simple.IngVal;
import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2core.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigMenu;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigScreen;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsScreen;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigMenu;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigScreen;
import dev.xkmc.modulargolems.content.menu.path.PathConfigMenu;
import dev.xkmc.modulargolems.content.menu.path.PathConfigScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemDisinegrateScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemDisintegrateMenu;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeMenu;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeScreen;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigMenu;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigScreen;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemMaterialIngredient;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.loot.DropPartModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemMiscs {

	private static final IngReg INGR = IngReg.of(ModularGolems.REG);

	public static final IngVal<GolemMaterialIngredient> ING_MAT = INGR.reg("material", GolemMaterialIngredient.class);


	private static final SR<RecipeSerializer<?>> RS = SR.of(ModularGolems.REG, BuiltInRegistries.RECIPE_SERIALIZER);

	public static final Val<RecipeSerializer<GolemAssembleRecipe>> ASSEMBLE =
			RS.reg("golem_assemble", () -> AbstractShapedRecipe.serializer(GolemAssembleRecipe::new));
	public static final Val<RecipeSerializer<GolemReplaceRecipe>> REPLACE =
			RS.reg("golem_replace_part", () -> AbstractShapedRecipe.serializer(GolemReplaceRecipe::new));
	public static final Val<RecipeSerializer<GolemSmithAddSlotRecipe>> SMITH_ADD_SLOT =
			RS.reg("golem_add_slot", () -> AbstractSmithingRecipe.serializer(GolemSmithAddSlotRecipe::new));

	public static final MenuEntry<EquipmentsMenu> EQUIPMENTS =
			REGISTRATE.menu("equipments", EquipmentsMenu::fromNetwork, () -> EquipmentsScreen::new)
					.register();

	public static final MenuEntry<ToggleGolemConfigMenu> CONFIG_TOGGLE =
			REGISTRATE.menu("config_toggle", ToggleGolemConfigMenu::fromNetwork, () -> ToggleGolemConfigScreen::new)
					.register();

	public static final MenuEntry<ItemConfigMenu> CONFIG_PICKUP =
			REGISTRATE.menu("config_pickup", ItemConfigMenu::fromNetwork, () -> ItemConfigScreen::new)
					.register();

	public static final MenuEntry<TargetConfigMenu> CONFIG_TARGET =
			REGISTRATE.menu("config_target", TargetConfigMenu::fromNetwork, () -> TargetConfigScreen::new)
					.register();

	public static final MenuEntry<PathConfigMenu> CONFIG_PATH =
			REGISTRATE.menu("config_path", PathConfigMenu::fromNetwork, () -> PathConfigScreen::new)
					.register();

	public static final MenuEntry<GolemUpgradeMenu> UPGRADES =
			REGISTRATE.menu("upgrades", GolemUpgradeMenu::fromNetwork, () -> GolemUpgradeScreen::new)
					.register();

	public static final MenuEntry<GolemDisintegrateMenu> DISINTEGRATE =
			REGISTRATE.menu("disintegrate", GolemDisintegrateMenu::fromNetwork, () -> GolemDisinegrateScreen::new)
					.register();

	static {
		ModularGolems.REGISTRATE.simple("slicing",
				NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
				() -> DropPartModifier.CODEC
		);
	}

	public static void register() {
	}

}

package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2core.util.ContextHelper;
import dev.xkmc.l2tabs.compat.jei.SideTabProperties;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigScreen;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsScreen;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigScreen;
import dev.xkmc.modulargolems.content.menu.path.PathConfigScreen;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.table.GolemDisinegrateScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeScreen;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigScreen;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class GolemJEIPlugin implements IModPlugin {

	public static final Identifier ID = ModularGolems.loc("main");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		for (Item item : GolemPart.LIST) {
			registration.registerSubtypeInterpreter(item, GolemJEIPlugin::partSubtype);
		}
		registration.registerSubtypeInterpreter(GolemItems.FACADE.get(), GolemJEIPlugin::partSubtype);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		GolemMaterialConfig config = GolemMaterialConfig.get();
		List<IJeiAnvilRecipe> recipes = new ArrayList<>();
		addPartCraftRecipes(recipes, config, registration.getVanillaRecipeFactory());
		addRepairRecipes(recipes, config, registration.getVanillaRecipeFactory());
		addUpgradeRecipes(recipes, config, registration.getVanillaRecipeFactory());
		registration.addRecipes(RecipeTypes.ANVIL, recipes);
		NeoForge.EVENT_BUS.post(new CustomRecipeEvent(registration));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addExtension(GolemAssembleRecipe.class, new GolemAssemblyExtension());
		registration.getCraftingCategory().addExtension(GolemReplaceRecipe.class, new GolemReplaceExtension());
		registration.getSmithingCategory().addExtension(GolemSmithAddSlotRecipe.class, new GolemAddSlotExtension());
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGhostIngredientHandler(ItemConfigScreen.class, new ItemFilterHandler());
		var eq = new SideTabProperties(GolemTabRegistry.EQUIPMENTS);
		eq.register(registration, EquipmentsScreen.class);
		new SideTabProperties(GolemTabRegistry.CONFIG).register(registration,
				ToggleGolemConfigScreen.class, ItemConfigScreen.class,
				TargetConfigScreen.class, PathConfigScreen.class,
				GolemUpgradeScreen.class, GolemDisinegrateScreen.class);
		CurioCompatRegistry.onJEIRegistry(e -> eq.register(registration, e));
	}

	private static String partSubtype(ItemStack stack, UidContext ctx) {
		return GolemPart.getMaterial(stack).orElse(GolemMaterial.EMPTY).toString();
	}

	private static void addPartCraftRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (var mat : config.getAllMaterials()) {
			var repair = config.getRepairIngredient(mat);
			var craft = config.getCraftIngredient(mat);
			if (repair == null || craft == null) continue;
			{
				var id = mat.withSuffix("_facade");
				recipes.add(factory.createAnvilRecipe(List.of(GolemItems.EMPTY_UPGRADE.asStack()),
						ContextHelper.resolve(repair),
						List.of(GolemFacade.setMaterial(GolemItems.FACADE.asStack(), mat)), id));
			}
			var arr = ContextHelper.resolve(craft);
			boolean special = false;
			for (ItemStack stack : arr) {
				if (stack.is(MGTagGen.SPECIAL_CRAFT)) {
					special = true;
					break;
				}
			}
			if (special) continue;
			for (var item : GolemPart.LIST) {
				List<ItemStack> list = new ArrayList<>();
				for (ItemStack stack : arr) {
					list.add(new ItemStack(stack.getItem(), item.count));
				}
				var id = mat.withSuffix("_" + item.builtInRegistryHolder().unwrapKey().orElseThrow().identifier().getPath());
				recipes.add(factory.createAnvilRecipe(new ItemStack(item), list,
						List.of(GolemPart.setMaterial(new ItemStack(item), mat)), id));
			}
		}
	}

	private static void addRepairRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
			List<ItemStack> input = new ArrayList<>();
			List<ItemStack> material = new ArrayList<>();
			List<ItemStack> result = new ArrayList<>();
			for (var mat : config.getAllMaterials()) {
				var repair = config.getRepairIngredient(mat);
				if (repair == null) continue;
				ItemStack golem = new ItemStack(types);
				ArrayList<GolemHolderMaterial.Entry> mats = new ArrayList<>();
				for (IGolemPart<?> part : types.getEntityType().values()) {
					mats.add(new GolemHolderMaterial.Entry(part.toItem(), mat));
				}
				golem = GolemItems.HOLDER_MAT.set(golem, new GolemHolderMaterial(mats));
				ItemStack damaged = golem.copy();
				input.add(GolemItems.DC_DISP_HP.set(damaged, 0.75));
				var arr = ContextHelper.resolve(repair);
				material.add(new ItemStack(!arr.isEmpty() ? arr.getFirst().getItem() : Items.BARRIER));
				result.add(GolemItems.DC_DISP_HP.set(golem, 1d));
			}
			recipes.add(factory.createAnvilRecipe(input, material, result, ModularGolems.loc("repair")));
		}
	}

	private static void addUpgradeRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
			List<ItemStack> mats = new ArrayList<>();
			List<ItemStack> result = new ArrayList<>();
			for (UpgradeItem item : UpgradeItem.LIST) {
				mats.add(item.getDefaultInstance());
				result.add(GolemUpgrade.add(new ItemStack(types), item));
			}
			var id = types.getEntityType().getRegistryName().withPrefix("upgrade_");
			recipes.add(factory.createAnvilRecipe(List.of(types.getDefaultInstance()), mats, result, id));
		}

		List<ItemStack> input = new ArrayList<>();
		for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
			input.add(new ItemStack(types));
		}
		{
			List<ItemStack> result = new ArrayList<>();
			for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
				result.add(GolemUpgrade.addSlot(new ItemStack(types), 1));
			}
			var id = ModularGolems.loc("add_slot");
			recipes.add(factory.createAnvilRecipe(input, List.of(GolemItems.ADD_SLOT.asStack()), result, id));
		}

		{
			List<ItemStack> result = new ArrayList<>();
			for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
				result.add(GolemUpgrade.addSlot(new ItemStack(types), 100));
			}
			var id = ModularGolems.loc("inf_slot");
			recipes.add(factory.createAnvilRecipe(input, List.of(GolemItems.INF_SLOT.asStack()), result, id));
		}
	}

}

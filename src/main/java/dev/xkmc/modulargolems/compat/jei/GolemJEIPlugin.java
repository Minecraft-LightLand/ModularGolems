package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemSlot;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.menu.config.ToggleGolemConfigScreen;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsScreen;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigScreen;
import dev.xkmc.modulargolems.content.menu.path.PathConfigScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemAssembleScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemDisinegrateScreen;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeScreen;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.content.menu.target.TargetConfigScreen;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class GolemJEIPlugin implements IModPlugin {

	public static final ResourceLocation ID = new ResourceLocation(ModularGolems.MODID, "main");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		for (Item item : GolemPart.LIST) {
			registration.registerSubtypeInterpreter(item, GolemJEIPlugin::partSubtype);
		}
		registration.registerSubtypeInterpreter(GolemItems.FACADE.get(), GolemJEIPlugin::facadeSubtype);
		for (var holder : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
			registration.registerSubtypeInterpreter(holder, GolemJEIPlugin::holderSubtype);
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		GolemMaterialConfig config = GolemMaterialConfig.get();
		List<IJeiAnvilRecipe> recipes = new ArrayList<>();
		addPartCraftRecipes(recipes, config, registration.getVanillaRecipeFactory());
		addRepairRecipes(recipes, config, registration.getVanillaRecipeFactory());
		addUpgradeRecipes(recipes, config, registration.getVanillaRecipeFactory());
		registration.addRecipes(RecipeTypes.ANVIL, recipes);
		registration.addRecipes(GolemAssemblyJeiCategory.TYPE, assemblyRecipes());
		registration.addRecipes(GolemPartDropJeiCategory.TYPE, partDropRecipes(config));
		MinecraftForge.EVENT_BUS.post(new CustomRecipeEvent(registration));
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new GolemAssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), defaultGolemIcon()), new GolemPartDropJeiCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(GolemItems.TABLE.get()), GolemAssemblyJeiCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(GolemItems.SIMPLE_TABLE.get()), GolemAssemblyJeiCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(GolemItems.SLICING_AXE.get()), GolemPartDropJeiCategory.TYPE);
	}

	/**
	 * 装配台按「模板 + 各部件格材料」程序化合成，没有数据包配方；这里按
	 * {@code GolemType} × 材料枚举成 JEI 配方，让 JEI 能显示装配台的制作。
	 */
	private static List<GolemAssemblyJeiRecipe> assemblyRecipes() {
		List<GolemAssemblyJeiRecipe> list = new ArrayList<>();
		GolemMaterialConfig config = GolemMaterialConfig.get();
		int slots = GolemSlot.values().length;
		for (GolemType<?, ?> type : GolemTypes.TYPES.get()) {
			Ingredient template = templateOf(type);
			ItemStack typeIcon = typeIconOf(type);
			boolean[] has = new boolean[slots];
			ItemStack[] ghost = new ItemStack[slots];
			for (IGolemPart<?> part : type.values()) {
				int idx = part.getSlot().ordinal();
				has[idx] = true;
				ghost[idx] = part.toItem().getDefaultInstance();
			}
			List<ItemStack> ghosts = Arrays.asList(ghost);
			for (var mat : config.getAllMaterials()) {
				Ingredient ing = config.getCraftIngredient(mat);
				if (ing.isEmpty()) continue;
				// 装配台不能用 SPECIAL_CRAFT（特殊合成材料）作为直接材料，与 GolemAssembleMenu 一致
				if (isSpecialCraft(ing)) continue;
				// 单部件：模板 + 该部件格材料 → 部件（其余属于该类型的部件格显示为空槽 + 部位虚影）
				for (IGolemPart<?> part : type.values()) {
					if (!GolemMaterialConfig.mayApply(part.toItem(), mat)) continue;
					Ingredient[] partIng = new Ingredient[slots];
					for (int i = 0; i < slots; i++) partIng[i] = has[i] ? Ingredient.EMPTY : null;
					partIng[part.getSlot().ordinal()] = ing;
					ItemStack result = GolemPart.setMaterial(part.toItem().getDefaultInstance(), mat);
					if (!result.isEmpty())
						list.add(new GolemAssemblyJeiRecipe(template, Arrays.asList(partIng), ghosts, typeIcon, partPreview(type, part, mat), result));
				}
				// 整只傀儡：模板 + 各部件格材料 → 傀儡
				boolean ok = true;
				for (IGolemPart<?> part : type.values()) {
					if (!GolemMaterialConfig.mayApply(part.toItem(), mat)) {
						ok = false;
						break;
					}
				}
				if (!ok) continue;
				ItemStack golem = buildGolem(type, mat);
				if (golem.isEmpty()) continue;
				Ingredient[] parts = new Ingredient[slots];
				for (int i = 0; i < slots; i++) parts[i] = has[i] ? ing : null;
				// 成品配方里「材料或部件」轮换时，部件应是该材料的部件，而不是空白胚料
				ItemStack[] ghostMat = new ItemStack[slots];
				for (IGolemPart<?> part : type.values())
					ghostMat[part.getSlot().ordinal()] = GolemPart.setMaterial(part.toItem().getDefaultInstance(), mat);
				list.add(new GolemAssemblyJeiRecipe(template, Arrays.asList(parts), Arrays.asList(ghostMat), typeIcon, golem, golem));
			}
		}
		return list;
	}

	/** 部件预览傀儡：目标部件用材料，其余部件用 EMPTY（空白胚料）。 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static ItemStack partPreview(GolemType<?, ?> type, IGolemPart<?> targetPart, ResourceLocation mat) {
		ItemStack stack = new ItemStack(GolemType.getGolemHolder((GolemType) type));
		for (IGolemPart<?> part : type.values()) {
			GolemHolder.addMaterial(stack, part.toItem(), part.getSlot() == targetPart.getSlot() ? mat : GolemMaterial.EMPTY);
		}
		return stack;
	}

	/** 类型标签图标：与制作界面左侧切换傀儡类型的标签页图标一致（该类型胚料物品）。 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static ItemStack typeIconOf(GolemType<?, ?> type) {
		return new ItemStack(GolemType.getGolemHolder((GolemType) type));
	}

	/** 模板可来自数据包配置；未配置时与 {@code GolemType#getTemplate / isTemplate} 一致，回退到通用模板物品。 */
	private static Ingredient templateOf(GolemType<?, ?> type) {
		Ingredient template = type.getTemplateIngredient();
		if (template.isEmpty()) template = Ingredient.of(GolemItems.GOLEM_TEMPLATE.get());
		return template;
	}

	private static boolean isSpecialCraft(Ingredient ing) {
		for (ItemStack stack : ing.getItems()) {
			if (stack.is(MGTagGen.SPECIAL_CRAFT) || stack.is(Items.BARRIER)) return true;
		}
		return false;
	}

	private static String holderSubtype(ItemStack stack, UidContext ctx) {
		StringBuilder sb = new StringBuilder(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())));
		for (GolemMaterial mat : GolemHolder.getMaterial(stack)) {
			if (mat != null) sb.append('|').append(mat.id());
		}
		return sb.toString();
	}

	/** 傀儡部件掉落数据：与 MGGLMGen / 各 compat 的 slicing_axe_drop_* 掉落修改器一致。{生物, 材料}。 */
	private static final String[][] PART_DROPS = {
			{"minecraft:iron_golem", "modulargolems:iron"},
			{"minecraft:warden", "modulargolems:sculk"},
			{"cataclysm:ignis", "cataclysm:ignitium"},
			{"cataclysm:the_harbinger", "cataclysm:witherite"},
			{"cataclysm:maledictus", "cataclysm:cursium"},
			{"cataclysm:netherite_monstrosity", "modulargolems:netherite"},
			{"cataclysm:ender_guardian", "cataclysm:ender_guardian"},
			{"cataclysm:scylla", "cataclysm:storm"},
			{"cataclysm:ancient_remnant", "cataclysm:ancient_metal"},
			{"legendary_monsters:ancient_guardian", "legendary_monsters:molten_metal"},
			{"legendary_monsters:cloud_golem", "legendary_monsters:cloud"},
			{"legendary_monsters:the_obliterator", "legendary_monsters:obliterator"},
			{"legendary_monsters:posessed_paladin", "legendary_monsters:paladin"},
			{"mowziesmobs:ferrous_wroughtnaut", "mowziesmobs:wroughtnaut"},
	};

	private static List<GolemPartDropJeiRecipe> partDropRecipes(GolemMaterialConfig config) {
		List<GolemPartDropJeiRecipe> list = new ArrayList<>();
		List<ResourceLocation> mats = config.getAllMaterials();
		for (String[] e : PART_DROPS) {
			ResourceLocation erl = new ResourceLocation(e[0]);
			ResourceLocation mrl = new ResourceLocation(e[1]);
			if (!ForgeRegistries.ENTITY_TYPES.containsKey(erl)) continue;
			if (!mats.contains(mrl)) continue;
			EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(erl);
			if (type == null) continue;
			List<ItemStack> drops = new ArrayList<>();
			Set<Item> seen = new HashSet<>();
			for (MetalGolemPartType t : MetalGolemPartType.values()) {
				ItemStack stack = GolemPart.setMaterial(t.toItem().getDefaultInstance(), mrl);
				if (seen.add(stack.getItem())) drops.add(stack);
			}
			list.add(new GolemPartDropJeiRecipe(type, drops));
		}
		return list;
	}

	/** 分类标签图标：用傀儡胚料 metal_golem_template。 */
	private static ItemStack defaultGolemIcon() {
		return new ItemStack(GolemItems.GOLEM_TEMPLATE.get());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static ItemStack buildGolem(GolemType<?, ?> type, ResourceLocation mat) {
		GolemHolder holder = GolemType.getGolemHolder((GolemType) type);
		ItemStack stack = new ItemStack(holder);
		for (IGolemPart<?> part : (IGolemPart<?>[]) type.values()) {
			GolemHolder.addMaterial(stack, part.toItem(), mat);
		}
		return stack;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(GolemAssembleRecipe.class, GolemAssemblyExtension::new);
		registration.getCraftingCategory().addCategoryExtension(GolemReplaceRecipe.class, GolemReplaceExtension::new);
		registration.getSmithingCategory().addExtension(GolemSmithAddSlotRecipe.class, new GolemAddSlotExtension());
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGlobalGuiHandler(GolemTabExtraAreas.INSTANCE);
		registration.addGhostIngredientHandler(ItemConfigScreen.class, new ItemFilterHandler());

		registration.addGuiScreenHandler(EquipmentsScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(ToggleGolemConfigScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(ItemConfigScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(TargetConfigScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(PathConfigScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(GolemAssembleScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(GolemUpgradeScreen.class, GolemJEIPlugin::create);
		registration.addGuiScreenHandler(GolemDisinegrateScreen.class, GolemJEIPlugin::create);
		CurioCompatRegistry.onJEIRegistry(e -> registration.addGuiScreenHandler(Wrappers.cast(e), GolemJEIPlugin::create));
	}

	@Nullable
	public static IGuiProperties create(ITabScreen screen) {
		if (screen.screenWidth() <= 0 || screen.screenHeight() <= 0) {
			return null;
		}
		int x = screen.getGuiLeft();
		int y = screen.getGuiTop();
		// Only the container itself. Reserving the tab strips here as well would shrink JEI's area a
		// second time, on top of the tab areas already reported as exclusion areas, and at high gui
		// scales that leaves too little room for the ingredient grid.
		int width = screen.getXSize();
		int height = screen.getYSize();
		if (width <= 0 || height <= 0) {
			return null;
		}
		return new GuiProperties(screen.asScreen().getClass(),
				x, y, width, height,
				screen.screenWidth(), screen.screenHeight()
		);
	}

	/**
	 * Tells JEI which screen areas the golem tabs occupy, so its item list is placed out of the way
	 * instead of covering them. The tab strips stick out of the container rectangle, which is all
	 * {@link #create} reserves.
	 */
	private enum GolemTabExtraAreas implements IGlobalGuiHandler {
		INSTANCE;

		@Override
		public Collection<Rect2i> getGuiExtraAreas() {
			Screen screen = Minecraft.getInstance().screen;
			if (screen == null) return List.of();
			return GolemTabManager.getExtraAreas(screen);
		}
	}

	private static String partSubtype(ItemStack stack, UidContext ctx) {
		return GolemPart.getMaterial(stack).orElse(GolemMaterial.EMPTY).toString();
	}

	private static String facadeSubtype(ItemStack stack, UidContext ctx) {
		return GolemFacade.getMaterial(stack).toString();
	}

	private static void addPartCraftRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (var mat : config.getAllMaterials()) {
			{
				if (!GolemMaterialConfig.mayApply(GolemItems.GOLEM_BODY.get(), mat)) continue;
				recipes.add(factory.createAnvilRecipe(new ItemStack(GolemItems.EMPTY_UPGRADE),
						List.of(config.getRepairIngredient(mat).getItems()),
						List.of(GolemFacade.setMaterial(GolemItems.FACADE.asStack(), mat))));
			}
			var arr = config.getCraftIngredient(mat).getItems();
			boolean special = false;
			for (ItemStack stack : arr) {
				if (stack.is(MGTagGen.SPECIAL_CRAFT)) {
					special = true;
					break;
				}
			}
			if (special) continue;
			for (var item : GolemPart.LIST) {
				if (!item.getDefaultInstance().is(MGTagGen.ANVIL_CRAFT)) continue;
				if (!GolemMaterialConfig.mayApply(item, mat)) continue;
				List<ItemStack> list = new ArrayList<>();
				for (ItemStack stack : arr) {
					list.add(new ItemStack(stack.getItem(), item.count));
				}
				recipes.add(factory.createAnvilRecipe(new ItemStack(item), list,
						List.of(GolemPart.setMaterial(new ItemStack(item), mat))));
			}
		}
	}

	private static void addRepairRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
			List<ItemStack> input = new ArrayList<>();
			List<ItemStack> material = new ArrayList<>();
			List<ItemStack> result = new ArrayList<>();
			for (var mat : config.getAllMaterials()) {
				if (!GolemMaterialConfig.mayApply(types, mat)) continue;
				ItemStack golem = new ItemStack(types);
				for (IGolemPart<?> part : types.getEntityType().values()) {
					GolemHolder.addMaterial(golem, part.toItem(), mat);
				}
				ItemStack damaged = golem.copy();
				damaged.getOrCreateTag().putFloat(GolemHolder.KEY_DISPLAY, 0.75f);
				input.add(damaged);
				var arr = config.getRepairIngredient(mat).getItems();
				material.add(new ItemStack(arr.length > 0 ? arr[0].getItem() : Items.BARRIER));
				golem.getOrCreateTag().putFloat(GolemHolder.KEY_DISPLAY, 1f);
				result.add(golem);
			}
			recipes.add(factory.createAnvilRecipe(input, material, result));
		}
	}

	private static void addUpgradeRecipes(List<IJeiAnvilRecipe> recipes, GolemMaterialConfig config, IVanillaRecipeFactory factory) {
		for (UpgradeItem item : UpgradeItem.LIST) {
			List<ItemStack> input = new ArrayList<>();
			List<ItemStack> material = new ArrayList<>();
			List<ItemStack> result = new ArrayList<>();
			for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
				input.add(new ItemStack(types));
			}
			material.add(new ItemStack(item));
			for (var types : GolemType.GOLEM_TYPE_TO_ITEM.values()) {
				result.add(GolemHolder.addUpgrade(new ItemStack(types), item));
			}
			recipes.add(factory.createAnvilRecipe(input, material, result));
		}
	}

}

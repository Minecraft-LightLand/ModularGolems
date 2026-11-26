package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.util.GolemPreviewUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 「傀儡部件掉落」JEI 分类。
 * <p>
 * 金属傀儡手持斩铁巨斧击杀特定生物后，随机掉落该材料的一个金属傀儡部件。布局（整体居中）：
 * 左侧渲染生物（按实际大小自动缩放），中上放斩铁巨斧物品（同时是配方输入 + 该分类催化剂，
 * 对着它按 U 即可查看全部生效生物），中间下方箭头，右侧纵向列出可掉落的部件。
 */
public class GolemPartDropJeiCategory implements IRecipeCategory<GolemPartDropJeiRecipe> {

	public static final RecipeType<GolemPartDropJeiRecipe> TYPE =
			RecipeType.create(ModularGolems.MODID, "golem_part_drop", GolemPartDropJeiRecipe.class);

	private static final int WIDTH = 140;
	private static final int HEIGHT = 68;

	public static final int AXE_X = 86, AXE_Y = 42;
	public static final int ARROW_X = 84, ARROW_Y = 27;
	public static final int DROP_X = 116, DROP_Y0 = 8, DROP_STEP = 18;

	private static final int MOB_X = 41, MOB_Y = 63;
	/** 把生物渲染成大约这么高（像素）；按体型反比缩放，太大缩小、太小放大。 */
	private static final float MOB_TARGET = 54.0F;

	private final IDrawable icon;
	private final IDrawable indicator;
	private final IDrawable slotBg;
	private final IDrawable arrow;
	private final IDrawable background;

	public GolemPartDropJeiCategory(IGuiHelper helper) {
		ResourceLocation tex = new ResourceLocation(ModularGolems.MODID, "textures/gui/container/assemble.png");
		this.icon = helper.createDrawableItemStack(new ItemStack(GolemItems.SLICING_AXE.get()));
		// 快捷栏攻击指示器「满」图标（左侧 (0,94) 是空槽，右侧 (18,94) 是满的）
		this.indicator = helper.createDrawable(new ResourceLocation("textures/gui/icons.png"), 18, 94, 18, 18);
		this.slotBg = helper.createDrawable(tex, 176, 0, 18, 18);
		this.arrow = helper.createDrawable(tex, 176, 104, 22, 15);
		this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
	}

	@Override
	public RecipeType<GolemPartDropJeiRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.modulargolems.golem_part_drop");
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	private IRecipeSlotBuilder addSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		return builder.addSlot(role, x, y).setBackground(slotBg, -1, -1);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GolemPartDropJeiRecipe recipe, IFocusGroup focuses) {
		for (int i = 0; i < recipe.drops().size(); i++) {
			addSlot(builder, RecipeIngredientRole.OUTPUT, DROP_X, DROP_Y0 + i * DROP_STEP).addItemStack(recipe.drops().get(i));
		}
	}

	@Override
	public void draw(GolemPartDropJeiRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics g, double mouseX, double mouseY) {
		indicator.draw(g, AXE_X, AXE_Y);
		arrow.draw(g, ARROW_X, ARROW_Y);
		if (recipe.mob() != null) {
			Entity e = recipe.mob().create(Proxy.getClientWorld());
			if (e instanceof LivingEntity living) {
				// 按体型反比缩放：太小的放大、太大的缩小，让显示高度大致恒定
				float h = Math.max(0.5F, living.getBbHeight());
				float scale = Math.max(8.0F, Math.min(55.0F, MOB_TARGET / h));
				GolemPreviewUtil.renderAtUnclipped(g, MOB_X, MOB_Y, living, scale, (int) mouseX, (int) mouseY);
			}
		}
	}

}

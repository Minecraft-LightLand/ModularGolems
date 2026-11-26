package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * 傀儡装配台（{@code golem_workbench} / {@code golem_workbench_simple}）的 JEI 分类。
 * <p>
 * 背景取 {@code assemble.png} 的「面板内区」（去掉外框），槽位坐标按面板偏移对齐。
 * 每个槽位用 {@code side.slot} 单格贴图单独绘制；槽位数量随傀儡类型不同（金属 4 / 人形 3 / 犬形 2）。
 * 另外复刻制作界面的视觉：胚料右侧的类型标签图标、空格里的部位虚影、左侧的傀儡 3D 预览。
 */
public class GolemAssemblyJeiCategory implements IRecipeCategory<GolemAssemblyJeiRecipe> {

	public static final RecipeType<GolemAssemblyJeiRecipe> TYPE =
			RecipeType.create(ModularGolems.MODID, "golem_assembly", GolemAssemblyJeiRecipe.class);

	private static final int WIDTH = 168;
	private static final int HEIGHT = 83;

	/** assemble.png 中面板内区（不含外框；上下留白各 4px）。 */
	private static final int BG_U = 4, BG_V = 16, BG_W = 168, BG_H = 83;

	/** 各 GolemSlot（UP/LEFT/MIDDLE/RIGHT/DOWN）的槽位左上角，= 菜单布局坐标 - 面板偏移 (4,16)。 */
	public static final int[] PART_X = {76, 58, 76, 94, 76};
	public static final int[] PART_Y = {27, 45, 45, 45, 63};
	public static final int TEMPLATE_X = 76, TEMPLATE_Y = 4;
	public static final int RESULT_X = 147, RESULT_Y = 45;
	public static final int ARROW_X = 119, ARROW_Y = 45;
	/** 类型标签图标（该类型胚料物品），放在胚料右边一段距离并放大。 */
	public static final int TYPE_ICON_X = 126, TYPE_ICON_Y = 4;
	private static final float TYPE_ICON_SCALE = 2.0F;

	/** 左侧傀儡预览锚点：与制作界面一致（GUI 锚点 (31,88) 减去面板偏移 (4,16)）。 */
	private static final int PREVIEW_X = 27, PREVIEW_Y = 72;

	private static final int GHOST_COLOR = 0x7f8B8B8B;

	private final IDrawable icon;
	private final IDrawable slotBg;
	private final IDrawable arrow;
	private final IDrawable background;

	public GolemAssemblyJeiCategory(IGuiHelper helper, ItemStack iconStack) {
		ResourceLocation tex = new ResourceLocation(ModularGolems.MODID, "textures/gui/container/assemble.png");
		this.icon = helper.createDrawableItemStack(iconStack);
		this.background = helper.createDrawable(tex, BG_U, BG_V, BG_W, BG_H);
		// 单格槽位贴图（assemble.png 的 side.slot 区域），只画槽位框
		this.slotBg = helper.createDrawable(tex, 176, 0, 18, 18);
		this.arrow = helper.createDrawable(tex, 176, 104, 22, 15);
	}

	@Override
	public RecipeType<GolemAssemblyJeiRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("block." + ModularGolems.MODID + ".golem_workbench");
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

	@Override
	public void draw(GolemAssemblyJeiRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics g, double mouseX, double mouseY) {
		int n = Math.min(PART_X.length, recipe.parts().size());
		for (int i = 0; i < n; i++) {
			Ingredient ing = recipe.parts().get(i);
			if (ing == null || !ing.isEmpty()) continue;
			ItemStack ghost = i < recipe.ghosts().size() ? recipe.ghosts().get(i) : null;
			if (ghost == null || ghost.isEmpty()) continue;
			int x = PART_X[i], y = PART_Y[i];
			g.renderItem(ghost, x, y);
			g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, GHOST_COLOR, GHOST_COLOR, 0);
		}
		if (!recipe.typeIcon().isEmpty()) {
			g.pose().pushPose();
			g.pose().translate(TYPE_ICON_X, TYPE_ICON_Y, 0);
			g.pose().scale(TYPE_ICON_SCALE, TYPE_ICON_SCALE, 1);
			g.renderItem(recipe.typeIcon(), 0, 0);
			g.pose().popPose();
		}
		AbstractGolemEntity<?, ?> golem = displayGolem(recipe.preview());
		if (golem != null)
			// 用不裁剪版本：JEI 里 scissor 与 pose 的组合会把整块预览裁掉
			GolemPreviewUtil.renderAtUnclipped(g, PREVIEW_X, PREVIEW_Y, golem, golem.getPreviewScale(),
					(int) mouseX, (int) mouseY);
		arrow.draw(g, ARROW_X, ARROW_Y);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static AbstractGolemEntity<?, ?> displayGolem(ItemStack stack) {
		if (!(stack.getItem() instanceof GolemHolder<?, ?> holder)) return null;
		var level = Proxy.getClientWorld();
		if (level == null) return null;
		// 用 createDummy 直接按物品 NBT 造实体，绕开 ClientHolderManager 的按 hashCode 缓存
		AbstractGolemEntity<?, ?> golem = ((GolemHolder) holder).createDummy(stack, level);
		if (golem == null) return null;
		// 与 ClientHolderManager#getEntityForDisplayInternal 一致：没有存档血量时按满血显示，否则破损
		float health = GolemHolder.getHealth(stack);
		golem.setGuardedDataImpl(health >= 0 ? Math.min(health, golem.getMaxHealth()) : golem.getMaxHealth());
		return golem;
	}

	private IRecipeSlotBuilder addSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		return builder.addSlot(role, x, y).setBackground(slotBg, -1, -1);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GolemAssemblyJeiRecipe recipe, IFocusGroup focuses) {
		int n = Math.min(PART_X.length, recipe.parts().size());
		boolean full = recipe.result().getItem() instanceof GolemHolder<?, ?>;
		// 模板需要的数量 = 用材料的部件格数（装配台要求模板数 >= 材料格数）
		int ingotSlots = 0;
		for (int i = 0; i < n; i++) {
			Ingredient ing = recipe.parts().get(i);
			if (ing != null && !ing.isEmpty()) ingotSlots++;
		}
		addSlot(builder, RecipeIngredientRole.INPUT, TEMPLATE_X, TEMPLATE_Y)
				.addItemStacks(withCount(recipe.template(), Math.max(1, ingotSlots)));
		for (int i = 0; i < n; i++) {
			Ingredient ing = recipe.parts().get(i);
			if (ing == null) continue;
			IRecipeSlotBuilder slot = addSlot(builder, RecipeIngredientRole.INPUT, PART_X[i], PART_Y[i]);
			if (ing.isEmpty()) continue;
			ItemStack partStack = i < recipe.ghosts().size() ? recipe.ghosts().get(i) : null;
			int count = partStack != null && partStack.getItem() instanceof GolemPart<?, ?> part ? part.count : 1;
			List<ItemStack> stacks = withCount(ing, Math.max(1, count));
			// 成品配方里每格既可以放材料，也可以放现成部件（轮换显示）
			if (full && partStack != null && !partStack.isEmpty())
				stacks.add(partStack.copy());
			slot.addItemStacks(stacks);
		}
		addSlot(builder, RecipeIngredientRole.OUTPUT, RESULT_X, RESULT_Y).addItemStack(recipe.result());
	}

	/** 把 Ingredient 的每个候选物品都设成需要的数量，让 JEI 槽位显示材料数。 */
	private static List<ItemStack> withCount(Ingredient ing, int count) {
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack stack : ing.getItems()) {
			ItemStack copy = stack.copy();
			copy.setCount(count);
			list.add(copy);
		}
		return list;
	}

}

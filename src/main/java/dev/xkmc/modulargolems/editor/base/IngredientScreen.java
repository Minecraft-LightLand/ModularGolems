package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IngredientScreen extends EditorScreen {

	@Nullable
	private final Ingredient current;
	private final Consumer<Ingredient> onSet;
	private final Screen parent;
	private final EditorSession session;

	public IngredientScreen(Component title, @Nullable Ingredient current, Consumer<Ingredient> onSet, Screen parent, EditorSession session) {
		super(title);
		this.current = current;
		this.onSet = onSet;
		this.parent = parent;
		this.session = session;
	}

	@Override
	protected void init() {
		List<Button> row = new ArrayList<>();
		row.add(Button.builder(EditorText.PICK_ITEM.get(), b -> pickItem()).bounds(0, 0, 70, 20).build());
		row.add(Button.builder(EditorText.PICK_TAG.get(), b -> pickTag()).bounds(0, 0, 70, 20).build());
		row.add(Button.builder(EditorText.CLEAR.get(), b -> apply(Ingredient.EMPTY)).bounds(0, 0, 60, 20).build());
		row.add(Button.builder(EditorText.BACK.get(), b -> Minecraft.getInstance().setScreen(parent)).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
	}

	private void apply(Ingredient ing) {
		onSet.accept(ing);
		session.dirty = true;
		Minecraft.getInstance().setScreen(this);
	}

	private void pickItem() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorText.SELECT_ITEM.get(),
				EditorUtil.listItems(), new PickItemHandler(this), this));
	}

	private void pickTag() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorText.SELECT_TAG.get(),
				EditorUtil.listTags(), new PickTagHandler(this), this));
	}

	private record PickItemHandler(IngredientScreen screen) implements PickListScreen.Handler<Item> {

		@Override
		public Component label(Item t) {
			return EditorUtil.itemName(t);
		}

		@Override
		public ItemStack icon(Item t) {
			return new ItemStack(t);
		}

		@Override
		public void onSelect(Item t) {
			screen.apply(EditorUtil.itemIngredient(t));
		}

	}

	private record PickTagHandler(IngredientScreen screen) implements PickListScreen.Handler<TagKey<Item>> {

		@Override
		public Component label(TagKey<Item> t) {
			return EditorUtil.tagName(t);
		}

		@Override
		@Nullable
		public ItemStack icon(TagKey<Item> t) {
			return null;
		}

		@Override
		public void onSelect(TagKey<Item> t) {
			screen.apply(EditorUtil.tagIngredient(t));
		}

	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 10, 0xFFFFFF);
		ItemStack icon = EditorUtil.ingredientIcon(current);
		if (icon != null) {
			g.renderItem(icon, width / 2 - 8, height / 2 - 30);
		}
		g.drawCenteredString(font, EditorUtil.ingredientText(current), width / 2, height / 2, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

}

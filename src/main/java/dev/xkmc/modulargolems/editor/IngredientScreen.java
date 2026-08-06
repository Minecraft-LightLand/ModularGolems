package dev.xkmc.modulargolems.editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class IngredientScreen extends Screen {

	private final Ingredient current;
	private final Consumer<Ingredient> onSet;
	private final Screen parent;

	public IngredientScreen(Component title, Ingredient current, Consumer<Ingredient> onSet, Screen parent) {
		super(title);
		this.current = current;
		this.onSet = onSet;
		this.parent = parent;
	}

	@Override
	protected void init() {
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.PICK_ITEM.get(), b -> pickItem())
				.bounds(c - 155, height - 30, 70, 20).build());
		addRenderableWidget(Button.builder(EditorLang.PICK_TAG.get(), b -> pickTag())
				.bounds(c - 75, height - 30, 70, 20).build());
		addRenderableWidget(Button.builder(EditorLang.CLEAR.get(), b -> apply(Ingredient.EMPTY))
				.bounds(c + 5, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 75, height - 30, 60, 20).build());
	}

	private void apply(Ingredient ing) {
		onSet.accept(ing);
		Minecraft.getInstance().setScreen(this);
	}

	private void pickItem() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_ITEM.get(),
				EditorData.listItems(), EditorData::itemName, ItemStack::new, item -> apply(EditorData.itemIngredient(item))));
	}

	private void pickTag() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_TAG.get(),
				EditorData.listTags(), EditorData::tagName, t -> null, tag -> apply(EditorData.tagIngredient(tag))));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 10, 0xFFFFFF);
		ItemStack icon = EditorData.ingredientIcon(current);
		if (icon != null) {
			g.renderItem(icon, width / 2 - 8, height / 2 - 30);
		}
		g.drawCenteredString(font, EditorData.ingredientText(current), width / 2, height / 2, 0xFFFFFF);
	}

}

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

import java.util.List;
import java.util.function.Consumer;

public class IngredientScreen extends Screen {

	public interface Source {
		List<Item> items();

		List<TagKey<Item>> tags();

		Component itemName(Item item);

		Component tagName(TagKey<Item> tag);

		Ingredient itemIngredient(Item item);

		Ingredient tagIngredient(TagKey<Item> tag);

		ItemStack ingredientIcon(Ingredient ing);

		Component ingredientText(Ingredient ing);

		Component itemTitle();

		Component tagTitle();
	}

	private final Ingredient current;
	private final Consumer<Ingredient> onSet;
	private final Screen parent;
	private final EditorSession session;
	private final Source source;

	public IngredientScreen(Component title, Ingredient current, Consumer<Ingredient> onSet, Screen parent, EditorSession session, Source source) {
		super(title);
		this.current = current;
		this.onSet = onSet;
		this.parent = parent;
		this.session = session;
		this.source = source;
	}

	@Override
	protected void init() {
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorText.PICK_ITEM.get(), b -> pickItem())
				.bounds(c - 155, height - 30, 70, 20).build());
		addRenderableWidget(Button.builder(EditorText.PICK_TAG.get(), b -> pickTag())
				.bounds(c - 75, height - 30, 70, 20).build());
		addRenderableWidget(Button.builder(EditorText.CLEAR.get(), b -> apply(Ingredient.EMPTY))
				.bounds(c + 5, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorText.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 75, height - 30, 60, 20).build());
	}

	private void apply(Ingredient ing) {
		onSet.accept(ing);
		session.dirty = true;
		Minecraft.getInstance().setScreen(this);
	}

	private void pickItem() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(source.itemTitle(),
				source.items(), source::itemName, ItemStack::new, item -> apply(source.itemIngredient(item)), this));
	}

	private void pickTag() {
		Minecraft.getInstance().setScreen(new PickListScreen<>(source.tagTitle(),
				source.tags(), source::tagName, t -> null, tag -> apply(source.tagIngredient(tag)), this));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 10, 0xFFFFFF);
		ItemStack icon = source.ingredientIcon(current);
		if (icon != null) {
			g.renderItem(icon, width / 2 - 8, height / 2 - 30);
		}
		g.drawCenteredString(font, source.ingredientText(current), width / 2, height / 2, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

}

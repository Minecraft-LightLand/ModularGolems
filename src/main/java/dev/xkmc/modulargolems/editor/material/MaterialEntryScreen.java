package dev.xkmc.modulargolems.editor.material;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.editor.base.DoubleMapScreen;
import dev.xkmc.modulargolems.editor.base.EditorList;
import dev.xkmc.modulargolems.editor.base.EditorScreen;
import dev.xkmc.modulargolems.editor.base.EditorSession;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
import dev.xkmc.modulargolems.editor.base.IngredientScreen;
import dev.xkmc.modulargolems.editor.base.ItemListScreen;
import dev.xkmc.modulargolems.editor.base.Obj2IntMapScreen;
import dev.xkmc.modulargolems.editor.util.GolemEditorHandlers;
import dev.xkmc.modulargolems.editor.util.GolemEditorLang;
import dev.xkmc.modulargolems.editor.util.GolemEditorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaterialEntryScreen extends EditorScreen {

	private final GolemMaterialConfig config;
	private final ResourceLocation id;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;

	public MaterialEntryScreen(GolemMaterialConfig config, ResourceLocation id, Screen parent, EditorSession session) {
		super(GolemEditorLang.MATERIAL.get());
		this.config = config;
		this.id = id;
		this.parent = parent;
		this.session = session;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		addRenderableWidget(Button.builder(EditorText.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(width / 2 - 30, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		Ingredient ing = config.ingredients.get(id);
		Ingredient rep = config.repairIngredients.containsKey(id) ? config.repairIngredients.get(id) : ing;
		List<EditorList.Entry> entries = new ArrayList<>();
		ItemStack ingIcon = EditorUtil.ingredientIcon(ing);
		ItemStack repIcon = EditorUtil.ingredientIcon(rep);

		entries.add(row(GolemEditorLang.INGREDIENT.get(), EditorUtil.ingredientText(ing), ingIcon,
				() -> openIngredient(GolemEditorLang.INGREDIENT.get(), ing,
						x -> setIngredient(config.ingredients, x))));
		entries.add(row(GolemEditorLang.REPAIR.get(), EditorUtil.ingredientText(rep), repIcon,
				() -> openIngredient(GolemEditorLang.REPAIR.get(), rep,
						x -> setIngredient(config.repairIngredients, x))));
		entries.add(row(GolemEditorLang.STATS.get(statsCount()), null, null,
				() -> Minecraft.getInstance().setScreen(new DoubleMapScreen<>(GolemEditorLang.STATS.get(statsCount()),
						config.stats.get(id), () -> config.stats.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>()),
						GolemEditorUtil.listStats(), GolemEditorHandlers.STAT, MaterialEntryScreen.this, session))));
		entries.add(row(GolemEditorLang.MODIFIERS.get(modCount()), null, null,
				() -> Minecraft.getInstance().setScreen(new Obj2IntMapScreen<>(GolemEditorLang.MODIFIERS.get(modCount()),
						config.modifiers.get(id), () -> config.modifiers.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>()),
						GolemEditorUtil.listModifiers(), GolemEditorHandlers.MODIFIER,
						GolemEditorLang.SELECT_MODIFIER.get(), MaterialEntryScreen.this, session))));
		entries.add(row(GolemEditorLang.LIMITATION.get(limitCount()), null, null,
				() -> Minecraft.getInstance().setScreen(new ItemListScreen<>(GolemEditorLang.LIMITATION.get(limitCount()),
						config.partLimitation.get(id), () -> config.partLimitation.computeIfAbsent(id, k -> new java.util.LinkedHashSet<>()),
						EditorUtil.listItems(), GolemEditorHandlers.ITEM,
						EditorText.SELECT_ITEM.get(), MaterialEntryScreen.this, session))));
		list.setData(entries);
	}

	private static EditorList.Entry row(Component label, @Nullable Component desc, @Nullable ItemStack icon, Runnable onClick) {
		Component text = desc == null ? label : label.copy().append(Component.literal(":  ")).append(desc);
		return new EditorList.Entry(text, icon, onClick);
	}

	private int statsCount() {
		var map = config.stats.get(id);
		return map == null ? 0 : map.size();
	}

	private int modCount() {
		var map = config.modifiers.get(id);
		return map == null ? 0 : map.size();
	}

	private int limitCount() {
		var set = config.partLimitation.get(id);
		return set == null ? 0 : set.size();
	}

	private void setIngredient(Map<ResourceLocation, Ingredient> map, Ingredient ing) {
		if (ing.isEmpty()) {
			map.remove(id);
		} else {
			map.put(id, ing);
		}
	}

	private void openIngredient(Component title, @Nullable Ingredient current, java.util.function.Consumer<Ingredient> onSet) {
		Minecraft.getInstance().setScreen(new IngredientScreen(title, current,
				onSet, MaterialEntryScreen.this, session));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, EditorText.FILE.get(id), width / 2, 10, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

}

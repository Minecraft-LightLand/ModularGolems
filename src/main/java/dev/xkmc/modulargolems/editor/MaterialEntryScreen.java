package dev.xkmc.modulargolems.editor;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MaterialEntryScreen extends Screen {

	private final GolemMaterialConfig config;
	private final ResourceLocation id;
	private final Screen parent;

	private EditorList list;

	public MaterialEntryScreen(GolemMaterialConfig config, ResourceLocation id, Screen parent) {
		super(EditorLang.MATERIAL.get());
		this.config = config;
		this.id = id;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(width / 2 - 30, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		Ingredient ing = config.ingredients.get(id);
		Ingredient rep = config.repairIngredients.containsKey(id) ? config.repairIngredients.get(id) : ing;
		List<EditorList.Entry> entries = new ArrayList<>();
		ItemStack ingIcon = EditorData.ingredientIcon(ing);
		ItemStack repIcon = EditorData.ingredientIcon(rep);

		entries.add(row(EditorLang.INGREDIENT.get(), EditorData.ingredientText(ing), ingIcon,
				() -> openIngredient(EditorLang.INGREDIENT.get(), ing, x -> config.ingredients.put(id, x))));
		entries.add(row(EditorLang.REPAIR.get(), EditorData.ingredientText(rep), repIcon,
				() -> openIngredient(EditorLang.REPAIR.get(), rep, x -> config.repairIngredients.put(id, x))));
		entries.add(row(EditorLang.STATS.get(statsMap().size()), null, null,
				() -> Minecraft.getInstance().setScreen(new DoubleMapScreen<>(EditorLang.STATS.get(statsMap().size()),
						statsMap(), EditorData.listStats(), statLabel(), t -> null, MaterialEntryScreen.this))));
		entries.add(row(EditorLang.MODIFIERS.get(modMap().size()), null, null,
				() -> Minecraft.getInstance().setScreen(new ModifierMapScreen(EditorLang.MODIFIERS.get(modMap().size()),
						modMap(), EditorData.listModifiers(), MaterialEntryScreen.this))));
		entries.add(row(EditorLang.LIMITATION.get(limitSet().size()), null, null,
				() -> Minecraft.getInstance().setScreen(new ItemListScreen(EditorLang.LIMITATION.get(limitSet().size()),
						limitSet(), EditorData.listItems(), MaterialEntryScreen.this))));
		list.setData(entries);
	}

	private static EditorList.Entry row(Component label, Component desc, ItemStack icon, Runnable onClick) {
		Component text = desc == null ? label : label.copy().append(Component.literal(":  ")).append(desc);
		return new EditorList.Entry(text, icon, onClick);
	}

	private Map<GolemStatType, Double> statsMap() {
		return config.stats.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
	}

	private Map<GolemModifier, Integer> modMap() {
		return config.modifiers.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
	}

	private java.util.Set<net.minecraft.world.item.Item> limitSet() {
		return config.partLimitation.computeIfAbsent(id, k -> new java.util.LinkedHashSet<>());
	}

	private static Function<GolemStatType, Component> statLabel() {
		return GolemStatType::getDesc;
	}

	private void openIngredient(Component title, Ingredient current, java.util.function.Consumer<Ingredient> onSet) {
		Minecraft.getInstance().setScreen(new IngredientScreen(title, current,
				onSet, MaterialEntryScreen.this));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, EditorLang.FILE.get(id), width / 2, 10, 0xFFFFFF);
	}

}

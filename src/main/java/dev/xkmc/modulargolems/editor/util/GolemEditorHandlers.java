package dev.xkmc.modulargolems.editor.util;

import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.editor.base.DoubleMapScreen;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
import dev.xkmc.modulargolems.editor.base.ItemListScreen;
import dev.xkmc.modulargolems.editor.base.Obj2IntMapScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class GolemEditorHandlers {

	public static final DoubleMapScreen.Handler<GolemStatType> STAT = new StatHandler();
	public static final DoubleMapScreen.Handler<StatFilterType> FILTER = new FilterHandler();
	public static final Obj2IntMapScreen.Handler<GolemModifier> MODIFIER = new ModifierHandler();
	public static final ItemListScreen.Handler<Item> ITEM = new ItemHandler();

	private record StatHandler() implements DoubleMapScreen.Handler<GolemStatType> {

		@Override
		public Component label(GolemStatType t) {
			return GolemEditorUtil.statName(t);
		}

		@Override
		@Nullable
		public ItemStack icon(GolemStatType t) {
			return null;
		}

		@Override
		public boolean percent(GolemStatType t) {
			return t.percentDisplay();
		}

	}

	private record FilterHandler() implements DoubleMapScreen.Handler<StatFilterType> {

		@Override
		public Component label(StatFilterType t) {
			return GolemEditorUtil.statFilterName(t);
		}

		@Override
		@Nullable
		public ItemStack icon(StatFilterType t) {
			return null;
		}

		@Override
		public boolean percent(StatFilterType t) {
			return false;
		}

	}

	private record ModifierHandler() implements Obj2IntMapScreen.Handler<GolemModifier> {

		@Override
		public Component label(GolemModifier m) {
			return m.getDesc();
		}

		@Override
		public int maxLevel(GolemModifier m) {
			return m.maxLevel;
		}

	}

	private record ItemHandler() implements ItemListScreen.Handler<Item> {

		@Override
		public Component label(Item t) {
			return EditorUtil.itemName(t);
		}

		@Override
		public ItemStack icon(Item t) {
			return new ItemStack(t);
		}

	}

	private GolemEditorHandlers() {
	}

}

package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.PersistentWheel;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GolemModeWheel(
		AbstractGolemEntity<?, ?> golem
) implements PersistentWheel<GolemModeEntry> {

	@Override
	public WheelKeyHandler getInputHandler() {
		return new GolemWheelKeyHandler();
	}

	@Override
	public @Nullable WheelAdaptor<?> getAtIndex(Player player, int index, boolean main) {
		if (!main && index != 0) {
			if (index < 0) {
				var entry = golem.getConfigEntry(MGLangData.LOADING.get());
				if (entry == null) return null;
				return new GolemFakeWheel(GolemItems.CARD[entry.getColor()].asStack(), MGLangData.TAB_TOGGLE.get());
			} else {
				ItemStack armor;
				if (golem instanceof MetalGolemEntity) armor = GolemItems.WINDSPIRIT_CHESTPLATE.asStack();
				else if (golem instanceof DogGolemEntity) armor = GolemItems.DOG_ARMOR_DIAMOND.asStack();
				else armor = Items.DIAMOND_CHESTPLATE.getDefaultInstance();
				return new GolemFakeWheel(armor, MGLangData.TAB_EQUIPMENT.get());
			}
		}
		return PersistentWheel.super.getAtIndex(player, index, main);
	}

	@Override
	public boolean isValid(Player player) {
		return player.isAlive() && golem.isAlive() && golem.canWandModify(player);
	}

	@Override
	public List<GolemModeEntry> getWheelContent() {
		return GolemModes.LIST.stream().map(e -> new GolemModeEntry(e)).toList();
	}

	@Override
	public int getIndex(Player player) {
		return golem.getMode().getID();
	}

	@Override
	public void select(int index) {
		ModularGolems.HANDLER.toServer(GolemSetModeToServer.of(golem, index));
	}

	@Override
	public void renderImpl(GuiGraphics g, Player player, List<GolemModeEntry> list, WheelContext ctx) {
		PersistentWheel.super.renderImpl(g, player, list, ctx);
		int index = ctx.hover() >= 0 ? ctx.hover() : ctx.sel();
		if (index < 0) index = getIndex(player);
		if (index < 0) return;
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float r = Math.min(x0 / 1.5f, y0) / 1.5f;
		float s = r * 0.02f;
		g.pose().pushPose();
		g.pose().translate(x0, y0, 0);
		g.pose().scale(s, s, s);
		g.pose().translate(0, -8, 0);
		var entry = list.get(index);
		entry.renderIcon(g);
		g.pose().popPose();
		var text = entry.mode().getName().plainCopy();
		var font = Minecraft.getInstance().font;
		int y = (int) (y0 + s * 3);
		for (var line : font.split(text, (int) r)) {
			g.drawString(font, line, x0 - font.width(line) / 2, y, 0xffffff, true);
			y += font.lineHeight + 1;
		}
	}

	@Override
	public void renderIcon(GuiGraphics g, int x0, int y0, boolean left, float sideWidth, boolean hover) {

	}

}

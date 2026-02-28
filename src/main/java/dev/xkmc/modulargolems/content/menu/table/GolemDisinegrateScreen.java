package dev.xkmc.modulargolems.content.menu.table;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2itemselector.overlay.TextBox;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class GolemDisinegrateScreen extends BaseContainerScreen<GolemDisintegrateMenu> implements ITabScreen {

	private Button disintegrate;
	private Component buttonError;

	public GolemDisinegrateScreen(GolemDisintegrateMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		TableTab.initScreen(TableTabType.DISINTEGRATE, this, this::addRenderableWidget);

		var ref = menu.getLayout().getComp("button");
		this.addRenderableWidget(disintegrate = Button.builder(Component.empty(), (e) -> this.click(1))
				.pos(leftPos + ref.x, topPos + ref.y).size(14, 14)
				.build(b -> new SpriteButton(b, "dissembly/")));
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		var sr = getRenderer();
		sr.start(g);
		for (var e : menu.partSlots) {
			if (e.isActive()) {
				if (e == menu.body) {
					sr.draw(g, e.name, "core_slot", -2, -2);
				} else {
					sr.draw(g, e.name, "slot", -1, -1);
				}
				if (e.getItem().isEmpty() && !e.partShadow.isEmpty())
					drawShadow(g, e, e.partShadow);
			}
		}
		if (menu.extra.isActive()) {
			sr.draw(g, "extra_mat", "slot", -1, -1);
			if (menu.extra.getItem().isEmpty() && !menu.extra.ingot.isEmpty()) {
				drawShadow(g, menu.extra, getExtraMat());
			}
		}
		if (menu.result.isActive()) {
			sr.draw(g, "result", "result_slot", -5, -5);
			sr.draw(g, "arrow", "arrow_0", -3, 0);
			if (menu.result.getItem().isEmpty() && !menu.result.output.isEmpty())
				drawShadow(g, menu.result, menu.result.output);
		}
		var input = menu.main.getItem();
		buttonError = null;
		boolean mayBreak = !input.isEmpty();
		for (var e : menu.partSlots)
			mayBreak &= e.getItem().isEmpty();
		disintegrate.visible = mayBreak;
		if (mayBreak) {
			float max = GolemHolder.getMaxHealth(input);
			float health = GolemHolder.getHealth(input);
			int reforge = GolemHolder.getReforge(input);
			if (max > 0 && health < max || reforge > 0) {
				mayBreak = false;
				buttonError = MGLangData.UI_FIX_FIRST.get();
			}
		}
		disintegrate.active = mayBreak;
		var result = menu.result.getItem();
		if (result.isEmpty())
			result = menu.result.output;
		renderPreview(g, mx, my, result.isEmpty() ? input : result);
		if (!result.isEmpty()) renderDiff(g, input, result);
	}

	private ItemStack getExtraMat() {
		var list = menu.extra.ingot.getItems();
		if (list.length == 0) return ItemStack.EMPTY;
		var time = menu.inventory.player.level().getGameTime() / 30;
		return list[(int) (time % list.length)];
	}

	private void drawShadow(GuiGraphics g, Slot e, ItemStack stack) {
		int x = leftPos + e.x;
		int y = topPos + e.y;
		g.renderItem(stack, x, y, e.x + e.y * this.imageWidth);
		g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7f8B8B8B, 0x7f8B8B8B, 0);

	}

	private void renderPreview(GuiGraphics g, int mx, int my, ItemStack preview) {
		if (preview.getItem() instanceof GolemHolder<?, ?> holder) {
			AbstractGolemEntity<?, ?> golem = ClientHolderManager.getEntityForDisplay(holder, preview);
			if (golem != null) {
				int x = leftPos + 30;
				int y = topPos + 80;
				double lx = x - mx;
				double ly = y - 40 - my;
				int scale = golem instanceof MetalGolemEntity ? 18 :
						golem instanceof HumanoidGolemEntity ? 24 :
								golem instanceof DogGolemEntity ? 32 : 18;
				float ax = (float) Math.atan(lx / 50.0);
				float ay = (float) Math.atan(ly / 50.0);
				InventoryScreen.renderEntityInInventoryFollowsAngle(g,//TODO
						leftPos + 3, topPos + 16, leftPos + 58, topPos + 99, 20,
						1f/scale, ax, ay, golem);
			}
		}
	}

	private void renderDiff(GuiGraphics g, ItemStack input, ItemStack result) {
		var matI = GolemHolder.getMaterial(input);
		var matR = GolemHolder.getMaterial(result);
		var upI = GolemHolder.getUpgrades(input);
		var upR = GolemHolder.getUpgrades(result);

		List<Component> comp = new ArrayList<>();
		{
			var statI = GolemMaterial.collectAttributes(matI, upI);
			var statR = GolemMaterial.collectAttributes(matR, upR);
			Map<Holder<Attribute>, Pair<GolemStatType, Double>> ans = new LinkedHashMap<>();
			var add = new LinkedHashSet<>(statR.keySet());
			add.removeAll(statI.keySet());
			for (var e : add)
				ans.put(e, statR.get(e));
			var common = new LinkedHashSet<>(statI.keySet());
			common.retainAll(statR.keySet());
			for (var e : common) {
				var er = statR.get(e);
				var ei = statI.get(e);
				if (er.getFirst() != ei.getFirst()) continue;
				ans.put(e, Pair.of(er.getFirst(), er.getSecond() - ei.getSecond()));
			}
			var old = new LinkedHashSet<>(statI.keySet());
			old.removeAll(statR.keySet());
			for (var e : old)
				ans.put(e, statI.get(e).mapSecond(x -> -x));
			for (var ent : ans.entrySet()) {
				var v = ent.getValue();
				if (Math.abs(v.getSecond()) > 1e-3) {
					comp.add(v.getFirst().getAdderTooltip(v.getSecond()));
				}
			}
		}

		{
			var modI = GolemMaterial.collectModifiers(matI, upI);
			var modR = GolemMaterial.collectModifiers(matR, upR);
			Map<GolemModifier, Integer> ans = new LinkedHashMap<>();
			var add = new LinkedHashSet<>(modR.keySet());
			add.removeAll(modI.keySet());
			for (var e : add)
				ans.put(e, modR.get(e));
			var common = new LinkedHashSet<>(modI.keySet());
			common.retainAll(modR.keySet());
			for (var e : common) {
				ans.put(e, modR.get(e) - modI.get(e));
			}
			var old = new LinkedHashSet<>(modI.keySet());
			old.removeAll(modR.keySet());
			for (var e : old)
				ans.put(e, -modI.get(e));
			for (var ent : ans.entrySet()) {
				int lv = ent.getValue();
				if (lv == 0) continue;
				var col = lv > 0 ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.RED;
				comp.add(Component.literal(lv > 0 ? "+ " : "- ")
						.append(ent.getKey().getTooltip(Math.abs(lv)).copy()
								.withStyle(col)).withStyle(col));
			}
		}
		if (comp.isEmpty()) return;
		comp.addFirst(MGLangData.UI_DIFF_STAT.get());
		var box = new TextBox(g, 2, 0, leftPos - 6, topPos + 6, leftPos - 10);
		box.renderLongText(font, comp);
	}

	protected void renderTooltip(GuiGraphics g, int x, int y) {
		if (disintegrate.isHovered()) {
			if (buttonError != null) {
				g.renderTooltip(font,
						List.of(buttonError),
						Optional.empty(),
						ItemStack.EMPTY, x, y);
				return;
			}
			if (disintegrate.isActive()) {
				var list = menu.main.dropList;
				if (list.isEmpty()) {
					g.renderTooltip(font,
							List.of(MGLangData.UI_DISINTEGRATE.get()),
							Optional.empty(),
							ItemStack.EMPTY, x, y);
				} else if (list.size() > 54) {
					g.renderTooltip(font,
							List.of(MGLangData.UI_RETURN_MANY.get(list.size())),
							Optional.empty(),
							ItemStack.EMPTY, x, y);
				} else {
					g.renderTooltip(font,
							List.of(MGLangData.UI_RETURN_ITEMS.get()),
							Optional.of(new ItemListTooltip(list)),
							ItemStack.EMPTY, x, y);
				}
				return;
			}
		}
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.getItem().isEmpty()) {
			if (hoveredSlot instanceof GolemDisintegrateMenu.ResultSlot slot && slot.error != null) {
				Optional<TooltipComponent> item = Optional.empty();
				if (!menu.extra.ingot.isEmpty()) {
					item = Optional.of(new ItemListTooltip(List.of(getExtraMat().copyWithCount(menu.extra.count))));
				}
				g.renderTooltip(font, List.of(slot.error), item, ItemStack.EMPTY, x, y);
				return;
			}
			if (hoveredSlot instanceof GolemDisintegrateMenu.PartSlot slot && !slot.partShadow.isEmpty()) {
				var stack = slot.partShadow;
				g.renderTooltip(font, getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack, x, y);
				return;
			}
		}
		super.renderTooltip(g, x, y);
	}

	@Override
	public int getLeftExpansion() {
		return menu.result.getItem().isEmpty() && menu.result.output.isEmpty() ? 0 : leftPos;
	}

	@Override
	public int getRightExpansion() {
		return 0;
	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

}

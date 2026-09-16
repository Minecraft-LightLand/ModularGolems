package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2library.base.overlay.TextBox;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeSort;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GolemUpgradeScreen extends BaseContainerScreen<GolemUpgradeMenu> implements ITabScreen {

	private Button left, right;

	private boolean lockUpgrade = false;
	private static final int CONTENT_PER_PAGE = 12;
	private int infoPage = 0;
	private int infoPages = 1;
	private final List<GolemType<?, ?>> types = new ArrayList<>();
	@Nullable
	private Set<GolemModifier> lastHoverMods = null;

	public GolemUpgradeScreen(GolemUpgradeMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		for (var t : GolemTypes.TYPES.get())
			types.add(t);
	}

	@Override
	protected void renderBg(GuiGraphics g, float p_97788_, int p_97789_, int p_97790_) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		var golemSlot = menu.getAsPredSlot("golem");
		if (golemSlot.getItem().isEmpty())
			drawShadow(g, golemSlot, getMainShadow());
		updatePage();
		renderUpgradeInfo(g);
	}

	private ItemStack getMainShadow() {
		if (types.isEmpty()) return ItemStack.EMPTY;
		long time = menu.inventory.player.level().getGameTime();
		int index = (int) ((time / 20) % types.size());
		return GolemType.getGolemHolder(types.get(index)).getDefaultInstance();
	}

	private void drawShadow(GuiGraphics g, Slot e, ItemStack stack) {
		int x = leftPos + e.x;
		int y = topPos + e.y;
		g.renderItem(stack, x, y, e.x + e.y * this.imageWidth);
		g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7f8B8B8B, 0x7f8B8B8B, 0);
	}

	private static final String[] ROMAN = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
	private static final int[] ROMAN_VAL = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

	private static String roman(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ROMAN_VAL.length; i++) {
			while (n >= ROMAN_VAL[i]) {
				n -= ROMAN_VAL[i];
				sb.append(ROMAN[i]);
			}
		}
		return sb.toString();
	}

	private static Component skillName(GolemModifier mod, int lv) {
		var name = mod.getDesc().copy();
		if (mod.maxLevel > 1)
			name.append(" ").append(roman(lv)).append(" / ").append(roman(mod.maxLevel));
		return name.withStyle(ChatFormatting.LIGHT_PURPLE);
	}

	private void renderUpgradeInfo(GuiGraphics g) {
		var golemSlot = menu.getAsPredSlot("golem");
		if (golemSlot.getItem().isEmpty()) return;
		var golem = golemSlot.getItem();
		var mats = GolemHolder.getMaterial(golem);
		var upgrades = GolemHolder.getUpgrades(golem);
		var sorted = new ArrayList<IUpgradeItem>(upgrades);
		sorted.sort(UpgradeSort.upgradeComparator());
		var holder = (GolemHolder<?, ?>) golem.getItem();
		var map = GolemMaterial.collectModifiers(mats, upgrades);
		var entries = new ArrayList<Map.Entry<GolemModifier, Integer>>(map.entrySet());
		entries.sort(UpgradeSort.entryComparator(sorted));
		var hovered = getHoveredMods();
		List<List<Component>> blocks = new ArrayList<>();
		if (map.isEmpty()) {
			blocks.add(List.of(MGLangData.UI_NO_UPGRADE.get()));
		} else {
			boolean shift = Screen.hasShiftDown();
			for (var e : entries) {
				List<Component> block = new ArrayList<>();
				var mod = e.getKey();
				int lv = e.getValue();
				Component name = skillName(mod, lv);
				if (hovered != null && hovered.contains(mod))
					name = name.copy().withStyle(ChatFormatting.UNDERLINE);
				block.add(name);
				if (shift)
					block.addAll(mod.getDetail(lv));
				blocks.add(block);
			}
		}
		List<List<Component>> pages = new ArrayList<>();
		List<Integer> blockPages = new ArrayList<>();
		List<Component> body = new ArrayList<>();
		int rows = 0;
		int pageIdx = 0;
		for (var block : blocks) {
			if (!body.isEmpty() && rows + block.size() > CONTENT_PER_PAGE) {
				pages.add(body);
				body = new ArrayList<>();
				rows = 0;
				pageIdx++;
			}
			body.addAll(block);
			rows += block.size();
			blockPages.add(pageIdx);
		}
		if (!body.isEmpty()) pages.add(body);
		infoPages = pages.size();
		if (hovered == null) {
			lastHoverMods = null;
		} else if (!hovered.equals(lastHoverMods)) {
			lastHoverMods = hovered;
			int index = 0;
			for (var e : entries) {
				if (hovered.contains(e.getKey())) {
					infoPage = blockPages.get(index);
					break;
				}
				index++;
			}
		}
		if (infoPage >= infoPages) infoPage = infoPages - 1;
		List<Component> page = new ArrayList<>();
		Component title = MGLangData.UI_SKILL_TITLE.get();
		if (infoPages > 1)
			title = title.copy().append(" (" + (infoPage + 1) + "/" + infoPages + ")");
		page.add(title);
		page.add(MGLangData.SLOT.get(holder.getRemaining(mats, upgrades)).withStyle(ChatFormatting.AQUA));
		page.add(MGLangData.UPGRADE_COUNT.get(map.size(), upgrades.size()));
		page.addAll(pages.get(infoPage));
		var box = new TextBox(g, 2, 0, leftPos - 6, topPos + 6, leftPos - 10);
		box.renderLongText(font, page);
	}

	@Nullable
	private Set<GolemModifier> getHoveredMods() {
		var slot = hoveredSlot;
		if (slot == null || !(slot instanceof UpgradeSlot)) return null;
		var stack = slot.getItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeItem item)) return null;
		var list = item.get();
		if (list.isEmpty()) return null;
		var ans = new HashSet<GolemModifier>();
		for (var e : list) ans.add(e.mod());
		return ans;
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double delta) {
		if (infoPages > 1) {
			int next = infoPage + (delta < 0 ? 1 : -1);
			infoPage = Math.max(0, Math.min(next, infoPages - 1));
			return true;
		}
		return super.mouseScrolled(mx, my, delta);
	}

	@Override
	protected void init() {
		super.init();
		TableTab.initScreen(TableTabType.UPGRADE, this, this::addRenderableWidget);

		int w = 11;
		int h = 11;
		int x = (this.width + this.imageWidth) / 2 - 70;
		int y = (this.height - this.imageHeight) / 2 + 27;
		this.addRenderableWidget(left = Button.builder(Component.empty(), (e) -> this.click(-1))
				.pos(x - w - 36, y).size(w, h).build(b -> new SpriteButton(b, menu.sprite.get(),
						"page_prev_on", "page_prev_down", "page_prev_ban")));
		this.addRenderableWidget(right = Button.builder(Component.empty(), (e) -> this.click(1))
				.pos(x, y).size(w, h).build(b -> new SpriteButton(b, menu.sprite.get(),
						"page_next_on", "page_next_down", "page_next_ban")));
		updatePage();
	}

	private void updatePage() {
		left.active = left.visible = menu.page.get() > 0;
		right.active = right.visible = menu.page.get() < menu.maxPage.get() - 1;
	}


	protected void renderTooltip(GuiGraphics g, int x, int y) {
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.getItem().isEmpty()) {
			var golemSlot = menu.getAsPredSlot("golem");
			if (hoveredSlot == golemSlot) {
				g.renderTooltip(font, List.of(MGLangData.UI_PUT_GOLEM.get()), Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
			if (hoveredSlot instanceof UpgradeSlot) {
				g.renderTooltip(font, List.of(MGLangData.UI_PUT_UPGRADE.get()), Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
		}
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
			ItemStack stack = hoveredSlot.getItem();
			if (hoveredSlot instanceof UpgradeSlot) {
				if (stack.getItem() instanceof IUpgradeItem item && !item.canBeRemoved()) {
					g.renderTooltip(font, List.of(MGLangData.UI_REMOVE_TEMPLATE.get()), Optional.empty(), stack, x, y);
					return;
				}
				if (!hoveredSlot.mayPickup(menu.inventory.player)) {
					g.renderTooltip(font, List.of(MGLangData.UI_NO_SLOT.get()), Optional.empty(), stack, x, y);
					return;
				}
			}
			g.renderTooltip(font, getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack, x, y);
		}

	}

	@Override
	public int getRightExpansion() {
		return 0;
	}

	@Override
	public int getLeftExpansion() {
		return menu.getAsPredSlot("golem").getItem().isEmpty() ? 0 : leftPos;
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

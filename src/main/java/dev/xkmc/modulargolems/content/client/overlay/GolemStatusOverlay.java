package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2core.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2itemselector.overlay.OverlayUtil;
import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import dev.xkmc.l2itemselector.wheel.WheelHandler;
import dev.xkmc.l2library.content.raytrace.IGlowingTarget;
import dev.xkmc.l2library.content.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.wand.GolemInteractItem;
import dev.xkmc.modulargolems.events.event.GolemInfoEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		if (Minecraft.getInstance().screen != null) return;
		if (WheelHandler.wheel != null) return;
		boolean offset = ItemSelectionOverlay.INSTANCE.isRendering();
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!(player.getMainHandItem().getItem() instanceof GolemInteractItem wand)) return;
		Entity target;
		if (wand instanceof IGlowingTarget) {
			target = RayTraceUtil.serverGetTarget(player);
		} else {
			var hit = Minecraft.getInstance().hitResult;
			if (!(hit instanceof EntityHitResult entityHit)) return;
			target = entityHit.getEntity();
		}
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (golem.isHostile()) return;
		Font font = Minecraft.getInstance().font;
		int screenWidth = g.guiWidth();
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		float health = golem.getGuardedDataImpl();
		float max = golem.getMaxHealth();
		float f = Mth.clamp(health / max, 0f, 1f);
		int color = Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
		MutableComponent hc = Component.literal("" + Math.round(health)).setStyle(Style.EMPTY.withColor(color));
		text.add(MGLangData.HEALTH.get(hc, Math.round(max)).withStyle(health <= 0 ? ChatFormatting.RED : ChatFormatting.AQUA));
		NeoForge.EVENT_BUS.post(new GolemInfoEvent(golem, text));
		text.add(golem.getMode().getDesc(golem));
		var config = golem.getConfigEntry(MGLangData.LOADING.get());
		if (config != null) {
			config.clientTick(player.level(), false);
			text.add(config.getDisplayName());
			if (config.locked) {
				text.add(MGLangData.CONFIG_LOCK.get().withStyle(ChatFormatting.RED));
			}
		}
		var modifiers = golem.getModifiers();
		if (modifiers.size() > 8) {
			var upgrades = golem.getUpgrades();
			text.add(MGLangData.UPGRADE_COUNT.get(modifiers.size(), upgrades.size()));
		} else {
			modifiers.forEach((k, v) -> text.add(k.getTooltip(v)));
		}
		int textPos = offset ? Math.round(screenWidth * 3 / 4f) : Math.round(screenWidth / 8f);
		new OverlayUtil(g, textPos, -1, -1)
				.renderLongText(font, text);
		OverlayUtil util = new OverlayUtil(g, (int) (screenWidth * 0.6), -1, -1);
		util.bg = 0xffc6c6c6;
		List<ClientTooltipComponent> list = List.of(new GolemEquipmentTooltip(golem));
		util.renderTooltipInternal(font, list);
	}

	private record GolemEquipmentTooltip(AbstractGolemEntity<?, ?> golem) implements ClientTooltipComponent {

		public static final SpriteManager SPRITE = new SpriteManager(ModularGolems.MODID, "equipments");

		@Override
		public int getHeight() {
			if (golem instanceof DogGolemEntity) return 38;
			return 74;
		}

		@Override
		public int getWidth(Font pFont) {
			if (golem instanceof DogGolemEntity) return 18;
			return 54;
		}

		@Override
		public void renderImage(Font font, int mx, int my, GuiGraphics g) {
			if (golem instanceof DogGolemEntity) {
				renderSlot(g, mx, my, golem.getItemBySlot(EquipmentSlot.HEAD), "altas_helmet");
				renderSlot(g, mx, my + 18, golem.getItemBySlot(EquipmentSlot.BODY), "altas_chestplate");
				return;
			}
			renderSlot(g, mx + 18, my, golem.getItemBySlot(EquipmentSlot.HEAD), "altas_helmet");
			renderSlot(g, mx + 18, my + 18, golem.getItemBySlot(EquipmentSlot.CHEST), "altas_chestplate");
			renderSlot(g, mx + 18, my + 36, golem.getItemBySlot(EquipmentSlot.LEGS), "altas_leggings");
			renderSlot(g, mx + 18, my + 54, golem.getItemBySlot(EquipmentSlot.FEET), "altas_boots");

			renderSlot(g, mx, my + 18, golem.getItemBySlot(EquipmentSlot.MAINHAND), "slotbg_sword");
			renderSlot(g, mx + 36, my + 18, golem.getItemBySlot(EquipmentSlot.OFFHAND), "altas_shield");

			if (golem instanceof SweepGolemEntity<?, ?> h) {
				renderSlot(g, mx, my + 36, h.getBackupHand().getItem(), "slotbg_bow");
				renderSlot(g, mx + 36, my + 36, h.getArrowSlot().getItem(), "slotbg_arrow");
			}

			if (golem instanceof MetalGolemEntity e) {
				renderSlot(g, mx, my, e.getRightShoulder().getItem(), "slotbg_shoulder");
				renderSlot(g, mx + 36, my, e.getLeftShoulder().getItem(), "slotbg_shoulder");
			}
		}

		private void renderSlot(GuiGraphics g, int x, int y, ItemStack stack, String bgName) {
			if (bgName.startsWith("altas_")) {
				blitSlotBg(g, x, y, "slot");
				if (stack.isEmpty())
					blitSlotBg(g, x + 1, y + 1, bgName);
			} else {
				if (stack.isEmpty()) blitSlotBg(g, x, y, bgName);
				else blitSlotBg(g, x, y, "slot");
			}
			if (stack.isEmpty()) {
				return;
			}
			g.renderItem(stack, x + 1, y + 1, 0);
			g.renderItemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 1);
		}

		private void blitSlotBg(GuiGraphics g, int x, int y, String bgName) {
			var level = Minecraft.getInstance().level;
			if (level == null) return;
			var tex = MenuLayoutConfig.getTexture(SPRITE.id());
			var side = SPRITE.get(level.registryAccess()).getSide(bgName);
			g.blit(tex, x, y, side.x, side.y, side.w, side.h);
		}

	}

}

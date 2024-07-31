package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2itemselector.overlay.OverlayUtil;
import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import dev.xkmc.l2library.content.raytrace.IGlowingTarget;
import dev.xkmc.l2library.content.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.item.wand.GolemInteractItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		if (Minecraft.getInstance().screen != null) return;
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
		Font font = Minecraft.getInstance().font;
		int screenWidth = g.guiWidth();
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		float health = golem.getHealth();
		float max = golem.getMaxHealth();
		float f = Mth.clamp(health / max, 0f, 1f);
		int color = Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
		MutableComponent hc = Component.literal("" + Math.round(health)).setStyle(Style.EMPTY.withColor(color));
		text.add(MGLangData.HEALTH.get(hc, Math.round(max)).withStyle(health <= 0 ? ChatFormatting.RED : ChatFormatting.AQUA));
		if (golem.hasFlag(GolemFlags.BOTANIA)) {
			//TODO bot text.add(BotUtils.getDesc(golem));
		}
		text.add(golem.getMode().getDesc(golem));
		var config = golem.getConfigEntry(MGLangData.LOADING.get());
		if (config != null) {
			config.clientTick(player.level(), false);
			text.add(config.getDisplayName());
			if (config.locked) {
				text.add(MGLangData.CONFIG_LOCK.get().withStyle(ChatFormatting.RED));
			}
		}
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		int textPos = offset ? Math.round(screenWidth * 3 / 4f) : Math.round(screenWidth / 8f);
		new OverlayUtil(g, textPos, -1, -1)
				.renderLongText(font, text);
		if (!(golem instanceof HumanoidGolemEntity humanoid)) return;
		OverlayUtil util = new OverlayUtil(g, (int) (screenWidth * 0.6), -1, -1);
		util.bg = 0xffc6c6c6;
		List<ClientTooltipComponent> list = List.of(new GolemEquipmentTooltip(humanoid));
		util.renderTooltipInternal(font, list);
	}

	private record GolemEquipmentTooltip(HumanoidGolemEntity golem) implements ClientTooltipComponent {

		public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("container/bundle/slot");

		@Override
		public int getHeight() {
			return 72;
		}

		@Override
		public int getWidth(Font pFont) {
			return 36;
		}

		@Override
		public void renderImage(Font font, int mx, int my, GuiGraphics g) {
			renderSlot(font, mx, my + 18, g, golem.getItemBySlot(EquipmentSlot.MAINHAND), null);
			renderSlot(font, mx, my + 36, g, golem.getItemBySlot(EquipmentSlot.OFFHAND), InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			renderSlot(font, mx + 18, my, g, golem.getItemBySlot(EquipmentSlot.HEAD), InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
			renderSlot(font, mx + 18, my + 18, g, golem.getItemBySlot(EquipmentSlot.CHEST), InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
			renderSlot(font, mx + 18, my + 36, g, golem.getItemBySlot(EquipmentSlot.LEGS), InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
			renderSlot(font, mx + 18, my + 54, g, golem.getItemBySlot(EquipmentSlot.FEET), InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
		}

		private void renderSlot(Font font, int x, int y, GuiGraphics g, ItemStack stack, @Nullable ResourceLocation atlasID) {
			this.blit(g, x, y);
			if (stack.isEmpty()) {
				if (atlasID != null) {
					TextureAtlasSprite atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
							.apply(atlasID);
					g.blit(x + 1, y + 1, 0, 16, 16, atlas);
				}
				return;
			}
			g.renderItem(stack, x + 1, y + 1, 0);
			g.renderItemDecorations(font, stack, x + 1, y + 1);
		}

		private void blit(GuiGraphics g, int x, int y) {
			g.blitSprite(TEXTURE_LOCATION, x, y, 18, 20);
		}

	}

}

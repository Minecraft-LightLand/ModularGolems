package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import dev.xkmc.l2library.base.overlay.OverlayUtil;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.raytrace.IGlowingTarget;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.compat.materials.botania.BotUtils;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.wand.GolemInteractItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay implements IGuiOverlay {

	@Override
	public void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
		if (Minecraft.getInstance().screen != null) return;
		boolean offset = ItemSelectionOverlay.INSTANCE.isRendering();
		LocalPlayer player = Proxy.getClientPlayer();
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
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		if (golem.hasFlag(GolemFlags.BOTANIA)) {
			text.add(BotUtils.getDesc(golem));
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
				.renderLongText(gui.getFont(), text);
		OverlayUtil util = new OverlayUtil(g, (int) (screenWidth * 0.6), -1, -1);
		util.bg = 0xffc6c6c6;
		List<ClientTooltipComponent> list = List.of(new GolemEquipmentTooltip(golem));
		util.renderTooltipInternal(gui.getFont(), list);
	}

	private record GolemEquipmentTooltip(AbstractGolemEntity<?, ?> golem) implements ClientTooltipComponent {

		public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ModularGolems.MODID, "textures/gui/container/equipments_extra.png");

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
				renderSlot(g, mx, my + 18, golem.getItemBySlot(EquipmentSlot.CHEST), "altas_chestplate");
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
				blitSlotBg(g, x + 1, y + 1, bgName);
			} else {
				blitSlotBg(g, x, y, bgName);
			}
			if (stack.isEmpty()) {
				return;
			}
			g.renderItem(stack, x + 1, y + 1, 0);
			g.renderItemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 1);
		}

		private void blitSlotBg(GuiGraphics g, int x, int y, String bgName) {
			int u = 0, v = 0, w = 18, h = 18;
			switch (bgName) {
				case "altas_helmet" -> { u = 176; v = 0; w = 16; h = 16; }
				case "altas_chestplate" -> { u = 192; v = 0; w = 16; h = 16; }
				case "altas_leggings" -> { u = 208; v = 0; w = 16; h = 16; }
				case "altas_boots" -> { u = 224; v = 0; w = 16; h = 16; }
				case "altas_shield" -> { u = 176; v = 16; w = 16; h = 16; }
				case "slotbg_arrow" -> { u = 176; v = 32; }
				case "slotbg_bow" -> { u = 194; v = 32; }
				case "slotbg_sword" -> { u = 212; v = 32; }
				case "slotbg_shoulder" -> { u = 230; v = 32; }
				case "slot" -> { u = 176; v = 50; }
			}
			g.blit(TEXTURE_LOCATION, x, y, u, v, w, h);
		}

	}

}

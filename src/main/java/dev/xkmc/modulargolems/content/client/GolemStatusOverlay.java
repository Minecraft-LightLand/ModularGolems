package dev.xkmc.modulargolems.content.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.overlay.OverlayUtils;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.item.WandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay extends GuiComponent implements IGuiOverlay {

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (!(player.getMainHandItem().getItem() instanceof WandItem)) return;
		var hit = Minecraft.getInstance().hitResult;
		if (!(hit instanceof EntityHitResult entityHit)) return;
		if (!(entityHit.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		gui.setupOverlayRenderState(true, false);
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		text.add(golem.getMode().getDesc(golem));
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		OverlayUtils util = new OverlayUtils(screenWidth, screenHeight);
		util.renderLongText(gui, poseStack, text);
		if (!(golem instanceof HumanoidGolemEntity humanoid)) return;
		util.bg = 0xffc6c6c6;
		List<ClientTooltipComponent> list = List.of(new GolemEquipmentTooltip(humanoid));
		util.renderTooltipInternal(poseStack, list, (int) (screenWidth * 0.6), -1);
	}

	private record GolemEquipmentTooltip(HumanoidGolemEntity golem) implements ClientTooltipComponent {

		public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");

		@Override
		public int getHeight() {
			return 72;
		}

		@Override
		public int getWidth(Font pFont) {
			return 36;
		}

		@Override
		public void renderImage(Font font, int mx, int my, PoseStack pose, ItemRenderer ir) {
			renderSlot(font, mx, my + 18, pose, golem.getItemBySlot(EquipmentSlot.MAINHAND), ir, null);
			renderSlot(font, mx, my + 36, pose, golem.getItemBySlot(EquipmentSlot.OFFHAND), ir, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			renderSlot(font, mx + 18, my, pose, golem.getItemBySlot(EquipmentSlot.HEAD), ir, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
			renderSlot(font, mx + 18, my + 18, pose, golem.getItemBySlot(EquipmentSlot.CHEST), ir, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
			renderSlot(font, mx + 18, my + 36, pose, golem.getItemBySlot(EquipmentSlot.LEGS), ir, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
			renderSlot(font, mx + 18, my + 54, pose, golem.getItemBySlot(EquipmentSlot.FEET), ir, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
		}

		private void renderSlot(Font font, int x, int y, PoseStack pose, ItemStack stack, ItemRenderer ir, @Nullable ResourceLocation atlasID) {
			this.blit(pose, x, y);
			if (stack.isEmpty()) {
				if (atlasID != null) {
					TextureAtlasSprite atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
							.apply(atlasID);
					RenderSystem.disableDepthTest();
					RenderSystem.setShaderTexture(0, atlas.atlasLocation());
					GuiComponent.blit(pose, x + 1, y + 1, 100, 16, 16, atlas);
					RenderSystem.enableDepthTest();
				}
				return;
			}
			ir.renderAndDecorateItem(pose, stack, x + 1, y + 1, 0);
			ir.renderGuiItemDecorations(pose, font, stack, x + 1, y + 1);
		}

		private void blit(PoseStack poseStack, int x, int y) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
			GuiComponent.blit(poseStack, x, y, 0, 0, 0, 18, 18, 128, 128);
		}

	}

}

package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.ConvertMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGarageKit;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.ClientSkinDispatch;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderSkin;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsScreen;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MaidSkinCompat {

	private static final ResourceLocation MAID_BUTTON = new ResourceLocation("touhou_little_maid", "textures/gui/maid_gui_button.png");

	private static EntityMaidRenderer RENDERER;

	@SubscribeEvent
	public static void onMaidConvert(ConvertMaidEvent event) {
		if (!(event.getEntity() instanceof HumanoidGolemEntity golem)) return;
		MaidWrapper data = golem.renderCompatData instanceof MaidWrapper ans ? ans : new MaidWrapper(golem);
		event.setMaid(data);
	}

	@SubscribeEvent
	public static void onHumanoidSkin(HumanoidSkinEvent event) {
		ItemStack stack = event.getStack();
		if (stack.is(InitItems.GARAGE_KIT.get())) {
			var id = ItemGarageKit.getMaidData(stack).getString("ModelId");
			event.setSkin(new MaidSkin(id));
		} else {
			HumanoidGolemEntity golem = event.getGolem();
			String modelId = golem.getMaidModelId();
			if (!modelId.isEmpty()) {
				event.setSkin(new MaidSkin(modelId));
			}
		}
	}

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		if (!(event.getScreen() instanceof EquipmentsScreen screen)) return;
		if (!(screen.getMenu().golem instanceof HumanoidGolemEntity golem)) return;
		int x = screen.getGuiLeft() + 150;
		int y = screen.getGuiTop() + 5;
		screen.addSkinWidget(new MaidSkinButton(x, y, golem, b -> {
			var mc = Minecraft.getInstance();
			if (mc.player != null) {
				mc.setScreen(new GolemMaidModelGui(golem));
			}
		}));
	}

	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		RENDERER = new EntityMaidRenderer(event.getContext());
	}

	private record MaidSkin(String id) implements SpecialRenderSkin {

		@Override
		public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
			if (RENDERER == null) return;
			stack.pushPose();
			try {
				float r = entity.getScale();
				stack.scale(r, r, r);
				RENDERER.render(entity, f1, f2, stack, source, i);
			} catch (Exception e) {
				ModularGolems.LOGGER.debug("Error rendering golem with TLM skin", e);
			} finally {
				stack.popPose();
			}
		}

	}

	private static final class MaidWrapper implements IMaid {

		private final HumanoidGolemEntity mob;

		private final ItemStack[] maidAnimItemCache = {ItemStack.EMPTY, ItemStack.EMPTY};

		private MaidWrapper(HumanoidGolemEntity mob) {
			this.mob = mob;
			mob.renderCompatData = this;
		}

		@Override
		public Mob asEntity() {
			return mob;
		}

		@Override
		public String getModelId() {
			if (ClientSkinDispatch.get(mob) instanceof MaidSkin skin)
				return skin.id();
			return "";
		}

		@Override
		public boolean isYsmModel() {
			return false;
		}

		@Override
		public String getYsmModelId() {
			return "";
		}

		@Override
		public String getYsmModelTexture() {
			return "";
		}

		@Override
		public Component getYsmModelName() {
			return Component.empty();
		}

		@Override
		public ItemStack[] getHandItemsForAnimation() {
			return maidAnimItemCache;
		}

		@Override
		public boolean isSwingingArms() {
			return mob.isAggressive();
		}

	}

	private static final class MaidSkinButton extends ImageButton {

		private final HumanoidGolemEntity golem;

		public MaidSkinButton(int x, int y, HumanoidGolemEntity golem, OnPress onPress) {
			super(x, y, 9, 9, 72, 43, 10, MAID_BUTTON, onPress);
			this.golem = golem;
			setTooltip(Tooltip.create(Component.translatable(ModularGolems.MODID + ".tooltip.maid_skin_button")));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
				if (button == 1) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					ModularGolems.HANDLER.toServer(SetMaidModelToServer.of(golem.getId(), "", ""));
					golem.setMaidModelId("");
					golem.setSoundPackId("");
					return true;
				}
			}
			return super.mouseClicked(mouseX, mouseY, button);
		}

	}

}

package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.model.AbstractModelGui;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.client.event.SpecialMaidRenderEvent.EASTER_EGG_MODEL;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class GolemMaidModelGui extends AbstractModelGui<HumanoidGolemEntity, MaidModelInfo> {

	private static int PAGE_INDEX = 0;
	private static int PACK_INDEX = 0;
	private static int ROW_INDEX = 0;

	public GolemMaidModelGui(HumanoidGolemEntity golem) {
		super(golem, CustomPackLoader.MAID_MODELS.getPackList());
	}

	@Override
	protected void drawLeftEntity(GuiGraphics graphics, int middleX, int middleY, float mouseX, float mouseY) {
		String modelId = entity.getMaidModelId();
		float renderItemScale = modelId.isEmpty() ? 1.0f : CustomPackLoader.MAID_MODELS.getModelRenderItemScale(modelId);
		int centerX = (middleX - 128) / 2;
		int yOffset = (int)(45.0F * (renderItemScale - 1.0F));
		InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
				centerX - 100, middleY - 100,
				centerX + 100, middleY + 200 - yOffset,
				(int)(45.0F * renderItemScale), 0.1F, mouseX, mouseY, this.entity);
	}

	@Override
	protected void drawRightEntity(GuiGraphics graphics, int posX, int posY, MaidModelInfo modelItem) {
		drawMaidEntity(graphics, posX, posY, modelItem);
	}

	@Override
	protected void openDetailsGui(HumanoidGolemEntity golem, MaidModelInfo modelInfo) {
	}

	@Override
	protected void notifyModelChange(HumanoidGolemEntity golem, MaidModelInfo info) {
		if (info.getEasterEgg() == null) {
			String modelId = info.getModelId().toString();
			String soundPackId = info.getUseSoundPackId();
			if (soundPackId == null) soundPackId = "";
			ModularGolems.HANDLER.toServer(SetMaidModelToServer.of(golem.getId(), modelId, soundPackId));
			if (minecraft != null && minecraft.player != null) {
				golem.setMaidModelId(modelId);
				golem.setSoundPackId(soundPackId);
			}
		}
	}

	@Override
	protected void addModelCustomTips(MaidModelInfo modelItem, List<Component> tooltips) {
		String useSoundPackId = modelItem.getUseSoundPackId();
		if (StringUtils.isNotBlank(useSoundPackId)) {
			tooltips.add(Component.translatable("gui.touhou_little_maid.skin.tooltips.maid_use_sound_pack_id", useSoundPackId).withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	protected int getPackIndex() {
		return PACK_INDEX;
	}

	@Override
	protected void setPackIndex(int packIndex) {
		PACK_INDEX = packIndex;
	}

	@Override
	protected int getRowIndex() {
		return ROW_INDEX;
	}

	@Override
	protected void setRowIndex(int rowIndex) {
		ROW_INDEX = rowIndex;
	}

	@Override
	protected int getPageIndex() {
		return PAGE_INDEX;
	}

	@Override
	protected void setPageIndex(int pageIndex) {
		PAGE_INDEX = pageIndex;
	}

	private void drawMaidEntity(GuiGraphics graphics, int posX, int posY, MaidModelInfo modelItem) {
		Level world = getMinecraft().level;
		if (world == null) return;

		EntityMaid maid;
		try {
			maid = (EntityMaid) EntityCacheUtil.ENTITY_CACHE.get(EntityMaid.TYPE, () -> {
				Entity e = EntityMaid.TYPE.create(world);
				return Objects.requireNonNullElseGet(e, () -> new EntityMaid(world));
			});
		} catch (ExecutionException | ClassCastException e) {
			e.fillInStackTrace();
			return;
		}

		clearMaidDataResidue(maid, false);
		if (modelItem.getEasterEgg() != null) {
			maid.setModelId(EASTER_EGG_MODEL);
		} else {
			maid.setModelId(modelItem.getModelId().toString());
		}
		InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, posX - 10, posY - 32, posX + 10, posY + 12, (int)(12.0F * modelItem.getRenderItemScale()), 0.1F, (float)(posX + 25), (float)(posY + 5), maid);
	}

	@Override
	protected void onClickCloseButton() {
		this.onClose();
	}

}

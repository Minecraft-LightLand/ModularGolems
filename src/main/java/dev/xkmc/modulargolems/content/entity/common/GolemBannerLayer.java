package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

import java.util.UUID;

public class GolemBannerLayer<T extends AbstractGolemEntity<?, ?>, M extends EntityModel<T> & IHeadedModel> extends RenderLayer<T, M> {
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final ItemInHandRenderer itemInHandRenderer;

	public GolemBannerLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer iihr) {
		this(parent, 1.0F, 1.0F, 1.0F, iihr);
	}

	public GolemBannerLayer(RenderLayerParent<T, M> parent, float sx, float sy, float sz, ItemInHandRenderer iihr) {
		super(parent);
		this.scaleX = sx;
		this.scaleY = sy;
		this.scaleZ = sz;
		this.itemInHandRenderer = iihr;
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		ItemStack stack = getBanner(entity);
		if (!renders(stack)) return;
		pose.pushPose();
		pose.scale(this.scaleX, this.scaleY, this.scaleZ);
		this.getParentModel().getHead().translateAndRotate(pose);
		this.getParentModel().translateToHead(pose);
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.HEAD, false, pose, buffer, light);
		pose.popPose();

	}

	public ItemStack getBanner(T entity) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (entity instanceof HumanoidGolemEntity && renders(stack)) {
			return ItemStack.EMPTY;
		}
		if (entity instanceof MetalGolemEntity && !renders(stack)) {
			stack = entity.getItemBySlot(EquipmentSlot.FEET);
		}
		if (renders(stack)) return stack;
		var entry = entity.getConfigEntry(MGLangData.LOADING.get());
		if (entry != null) {
			entry.clientTick(entity.level(), false);
			UUID captainId = entry.squadConfig.getCaptainId();
			boolean showFlag = captainId != null && entity.getUUID().equals(captainId);
			if (showFlag) {
				String color = DyeColor.values()[entry.getColor()].getName();
				Item item = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color + "_banner"));
				return item.getDefaultInstance();
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean renders(ItemStack stack) {
		return stack.getItem() instanceof BannerItem;
	}

}
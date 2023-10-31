package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MetalGolemBannerLayer<T extends AbstractGolemEntity<?, ?>, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final Map<SkullBlock.Type, SkullModelBase> skullModels;
	private final ItemInHandRenderer itemInHandRenderer;

	public MetalGolemBannerLayer(RenderLayerParent<T, M> parent, EntityModelSet models, ItemInHandRenderer iihr) {
		this(parent, models, 1.0F, 1.0F, 1.0F, iihr);
	}

	public MetalGolemBannerLayer(RenderLayerParent<T, M> parent, EntityModelSet models, float sx, float sy, float sz, ItemInHandRenderer iihr) {
		super(parent);
		this.scaleX = sx;
		this.scaleY = sy;
		this.scaleZ = sz;
		this.skullModels = SkullBlockRenderer.createSkullRenderers(models);
		this.itemInHandRenderer = iihr;
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (!renders(stack)) {
			stack = entity.getItemBySlot(EquipmentSlot.FEET);
		}
		if (!renders(stack)) {
			var entry = entity.getConfigEntry(MGLangData.LOADING.get());
			if (entry != null) {
				entry.clientTick(entity.level(), false);
				//stack = entry.squadConfig.getBanner(); TODO get banner
			}
		}
		pose.pushPose();
		pose.scale(this.scaleX, this.scaleY, this.scaleZ);
		this.getParentModel().getHead().translateAndRotate(pose);
		translateToHead(pose);
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.HEAD, false, pose, buffer, light);
		pose.popPose();

	}

	public boolean renders(ItemStack stack) {
		return stack.getItem() instanceof BannerItem;
	}

	public static void translateToHead(PoseStack pose) {
		pose.translate(0.0F, -0.25F, 0.0F);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F));
		pose.scale(0.625F, -0.625F, -0.625F);
	}
}
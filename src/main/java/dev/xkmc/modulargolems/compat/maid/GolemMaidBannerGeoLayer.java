package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemBannerUtil;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 女仆模型（Gecko 风格）上的傀儡旗帜层。
 * TLM 自带 GeckoLayerMaidBanner 写死 IMaid#convertToMaid，对傀儡恒为 null，旗帜不画。
 * 这里以女仆模型的 BackpackLocator 骨骼为原点补一层，旗帜来源复用 GolemBannerUtil。
 */
public class GolemMaidBannerGeoLayer<T extends Mob, R extends IGeoEntityRenderer<T>> extends GeoLayerRenderer<T, R> {

	private static final float OFFSET_X = 0F;
	private static final float OFFSET_Y = 0.5F;
	private static final float OFFSET_Z = -0.1F;
	private static final float SCALE = 0.65F;
	private static final float ROT_Y = 180F;
	private static final float ROT_X = -5F;
	private static final float ROT_Z = 180F;

	private final ItemInHandRenderer itemInHandRenderer;

	public GolemMaidBannerGeoLayer(R renderer, ItemInHandRenderer itemInHandRenderer) {
		super(renderer);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public GeoLayerRenderer<T, R> copy(R rendererIn) {
		return new GolemMaidBannerGeoLayer<>(rendererIn, itemInHandRenderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
					   float netHeadYaw, float headPitch) {
		if (!(entity instanceof AbstractGolemEntity<?, ?>)) return;
		if (IMaid.convert(entity) == null) return;
		ItemStack stack = GolemBannerUtil.getBanner((AbstractGolemEntity<?, ?>) entity);
		if (!(stack.getItem() instanceof BannerItem)) return;
		var info = getGeoEntity(entity).getMaidInfo();
		if (info == null || !info.isShowBackpack() || entity.isSleeping() || entity.isInvisible()) return;
		ILocationModel geoModel = getLocationModel(entity);
		if (geoModel == null || geoModel.backpackBones().isEmpty()) return;

		poseStack.pushPose();
		RenderUtils.prepMatrixForLocator(poseStack, geoModel.backpackBones());
		poseStack.translate(OFFSET_X, OFFSET_Y, OFFSET_Z);
		poseStack.scale(SCALE, -SCALE, -SCALE);
		poseStack.mulPose(Axis.YP.rotationDegrees(ROT_Y));
		poseStack.mulPose(Axis.XP.rotationDegrees(ROT_X));
		poseStack.mulPose(Axis.ZP.rotationDegrees(ROT_Z));
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.HEAD, false, poseStack, bufferIn, packedLight);
		poseStack.popPose();
	}

}

package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MetalGolemRenderer extends MobRenderer<MetalGolemEntity, MetalGolemModel<MetalGolemEntity>> {

	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	private final ThreadLocal<RenderHandle<MetalGolemEntity>> handle = new ThreadLocal<>();

	public MetalGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new MetalGolemModel<>(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
		this.addLayer(new MetalGolemCrackinessLayer(this));
	}

	@Deprecated
	public ResourceLocation getTextureLocation(MetalGolemEntity entity) {
		return GOLEM_LOCATION;
	}

	protected void setupRotations(MetalGolemEntity entity, PoseStack stack, float v1, float v2, float v3) {
		super.setupRotations(entity, stack, v1, v2, v3);
		if (!((double) entity.animationSpeed < 0.01D)) {
			float f = 13.0F;
			float f1 = entity.animationPosition - entity.animationSpeed * (1.0F - v3) + 6.0F;
			float f2 = (Math.abs(f1 % f - 6.5F) - 3.25F) / 3.25F;
			stack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
		}
	}

	@Override
	public void render(MetalGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		this.handle.set(new RenderHandle<>(entity, f1, f2, stack, source, i));
		super.render(entity, f1, f2, stack, source, i);
		this.handle.set(null);
	}

	@Nullable
	@Override
	protected RenderType getRenderType(MetalGolemEntity entity, boolean b1, boolean b2, boolean b3) {
		if (this.handle.get() == null) return null;
		boolean flag = this.isBodyVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleTo(Proxy.getClientPlayer());
		renderPart(MetalGolemPartType.BODY, entity, b1, b2, b3, flag1);
		renderPart(MetalGolemPartType.LEFT, entity, b1, b2, b3, flag1);
		renderPart(MetalGolemPartType.RIGHT, entity, b1, b2, b3, flag1);
		renderPart(MetalGolemPartType.LEG, entity, b1, b2, b3, flag1);
		return null;
	}

	private void renderPart(MetalGolemPartType type, MetalGolemEntity entity, boolean b1, boolean b2, boolean b3, boolean flag1) {
		RenderType rendertype = this.getRenderTypeInternal(type, entity, b1, b2, b3);
		RenderHandle<MetalGolemEntity> hand = handle.get();
		if (rendertype != null) {
			VertexConsumer vertexconsumer = hand.source().getBuffer(rendertype);
			int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, hand.f2()));
			this.model.renderToBufferInternal(type, hand.stack(), vertexconsumer, hand.i(), i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
		}
	}

	@Nullable
	private RenderType getRenderTypeInternal(MetalGolemPartType type, MetalGolemEntity entity, boolean b1, boolean b2, boolean b3) {
		ResourceLocation resourcelocation = this.getTextureLocationInternal(type, entity);
		if (b2) {
			return RenderType.itemEntityTranslucentCull(resourcelocation);
		} else if (b1) {
			return this.model.renderType(resourcelocation);
		} else {
			return b3 ? RenderType.outline(resourcelocation) : null;
		}
	}

	private ResourceLocation getTextureLocationInternal(MetalGolemPartType type, MetalGolemEntity entity) {
		var material = entity.getMaterials().get(type.ordinal());
		String id = material.id().getNamespace();
		String mat = material.id().getPath();
		return new ResourceLocation(id, "textures/entity/metal_golem/" + mat + ".png");
	}

}
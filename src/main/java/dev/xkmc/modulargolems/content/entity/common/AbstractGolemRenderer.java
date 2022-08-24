package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.metalgolem.RenderHandle;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGolemRenderer<T extends AbstractGolemEntity<T, P>, P extends IGolemPart, M extends HierarchicalModel<T> & IGolemModel<T, P, M>> extends MobRenderer<T, M> {

	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	private final ThreadLocal<RenderHandle<T>> handle = new ThreadLocal<>();
	private final Supplier<P[]> list;

	public AbstractGolemRenderer(EntityRendererProvider.Context ctx, M model, float f, Supplier<P[]> list) {
		super(ctx, model, f);
		this.list = list;
	}

	@Deprecated
	public ResourceLocation getTextureLocation(T entity) {
		return GOLEM_LOCATION;
	}

	protected void setupRotations(T entity, PoseStack stack, float v1, float v2, float v3) {
		super.setupRotations(entity, stack, v1, v2, v3);
		if (!((double) entity.animationSpeed < 0.01D)) {
			float f = 13.0F;
			float f1 = entity.animationPosition - entity.animationSpeed * (1.0F - v3) + 6.0F;
			float f2 = (Math.abs(f1 % f - 6.5F) - 3.25F) / 3.25F;
			stack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
		}
	}

	@Override
	public void render(T entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		this.handle.set(new RenderHandle<>(entity, f1, f2, stack, source, i));
		super.render(entity, f1, f2, stack, source, i);
		this.handle.set(null);
	}

	@Nullable
	@Override
	protected RenderType getRenderType(T entity, boolean b1, boolean b2, boolean b3) {
		if (this.handle.get() == null) return null;
		boolean flag = this.isBodyVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleTo(Proxy.getClientPlayer());
		for (P p : list.get()) {
			renderPart(p, entity, b1, b2, b3, flag1);
		}
		return null;
	}

	private void renderPart(P type, T entity, boolean b1, boolean b2, boolean b3, boolean flag1) {
		RenderType rendertype = this.getRenderTypeInternal(type, entity, b1, b2, b3);
		RenderHandle<T> hand = handle.get();
		if (rendertype != null) {
			VertexConsumer vertexconsumer = hand.source().getBuffer(rendertype);
			int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, hand.f2()));
			this.model.renderToBufferInternal(type, hand.stack(), vertexconsumer, hand.i(), i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
		}
	}

	@Nullable
	private RenderType getRenderTypeInternal(P type, T entity, boolean b1, boolean b2, boolean b3) {
		ResourceLocation resourcelocation = this.getTextureLocationInternal(type, entity);
		if (b2) {
			return RenderType.itemEntityTranslucentCull(resourcelocation);
		} else if (b1) {
			return this.model.renderType(resourcelocation);
		} else {
			return b3 ? RenderType.outline(resourcelocation) : null;
		}
	}

	protected abstract ResourceLocation getTextureLocationInternal(P type, T entity);

}
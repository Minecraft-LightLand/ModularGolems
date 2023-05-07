package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGolemRenderer<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>, M extends EntityModel<T> & IGolemModel<T, P, M>> extends MobRenderer<T, M> {

	public static final List<Function<AbstractGolemRenderer<?, ?, ?>, RenderLayer<? extends AbstractGolemEntity<?, ?>, ?>>> LIST = new ArrayList<>();

	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	private final ThreadLocal<RenderHandle<T>> handle = new ThreadLocal<>();
	private final Supplier<P[]> list;

	public AbstractGolemRenderer(EntityRendererProvider.Context ctx, M model, float f, Supplier<P[]> list) {
		super(ctx, model, f);
		this.list = list;
		LIST.forEach(e -> this.addLayer(Wrappers.cast(e.apply(this))));

	}

	@Deprecated
	public ResourceLocation getTextureLocation(T entity) {
		return GOLEM_LOCATION;
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
		var materials = entity.getMaterials();
		int index = type.ordinal();
		ResourceLocation material = materials.size() > index ? materials.get(index).id() : GolemMaterial.EMPTY;
		ResourceLocation resourcelocation = model.getTextureLocationInternal(material);
		if (b2) {
			return RenderType.itemEntityTranslucentCull(resourcelocation);
		} else if (b1) {
			return this.model.renderType(resourcelocation);
		} else {
			return b3 ? RenderType.outline(resourcelocation) : null;
		}
	}

}
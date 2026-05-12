package dev.xkmc.modulargolems.content.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGolemRenderer<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IGolemModel<E, S, P, M>
		> extends MobRenderer<E, S, M> {

	public static final List<Function<AbstractGolemRenderer<?, ?, ?, ?>, RenderLayer<? extends AbstractGolemRenderState<?, ?, ?>, ?>>> LIST = new ArrayList<>();

	private static final Identifier GOLEM_LOCATION = Identifier.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");

	private final Supplier<P[]> list;

	public AbstractGolemRenderer(EntityRendererProvider.Context ctx, M model, float f, Supplier<P[]> list) {
		super(ctx, model, f);
		this.list = list;
		addLayer(new GolemDefaultLayer<>(this));
		LIST.forEach(e -> this.addLayer(Wrappers.cast(e.apply(this))));
	}

	public Identifier getTextureLocation(S entity) {
		return GOLEM_LOCATION;
	}

	protected boolean delegated(S entity) {
		return false;
	}

	@Override
	public void submit(S state, PoseStack pose, SubmitNodeCollector col, CameraRenderState cam) {
		if (model instanceof IHeadedModel headed) headed.getHead().visible = true;
		super.submit(state, pose, col, cam);
	}

	@Nullable
	@Override
	protected RenderType getRenderType(S entity, boolean b1, boolean b2, boolean b3) {
		if (delegated(entity))
			return super.getRenderType(entity, b1, b2, b3);
		return null;
	}

	protected void renderAllParts(PoseStack pose, SubmitNodeCollector buffer, int light, S entity) {
		if (delegated(entity)) return;
		var player = Proxy.getClientPlayer();
		boolean visible = isBodyVisible(entity);
		boolean ghost = !visible && player != null && !entity.isInvisibleToPlayer;
		boolean glowing = entity.appearsGlowing();
		pose.pushPose();
		Identifier facade = null;
		var opt = entity.getSkin();
		if (opt.getItem() instanceof GolemFacade)
			facade = GolemFacade.getMaterial(opt);
		var materials = entity.getMaterials();
		for (P part : list.get()) {
			Identifier rl = facade;
			if (rl == null) {
				int index = part.ordinal();
				rl = materials.size() > index ? materials.get(index).id() : GolemMaterial.EMPTY;
			}

			ModelOverride override = ModelOverrides.getOverride(rl);
			override.renderAll(this, entity, part, pose, buffer, rl, light, visible, ghost, glowing);
		}
		pose.popPose();
	}

	public void renderPartModel(S entity, P part, PoseStack pose, SubmitNodeCollector buffer, RenderType type, int light, boolean ghost) {
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity));
		this.model.renderToBufferInternal(part, e -> buffer.submitModelPart(
				e, pose, type, light, overlay,
				null, false, false,
				ghost ? 0x26FFFFFF : -1, null, entity.outlineColor));
	}

}